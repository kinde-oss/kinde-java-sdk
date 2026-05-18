package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import com.kinde.spring.http.KindeClientRequestInterceptor;
import com.kinde.spring.http.UserAgentRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;
import java.util.Optional;

@AutoConfiguration
@AutoConfigureBefore(OAuth2ResourceServerAutoConfiguration.class)
@ConditionalOnClass(JwtAuthenticationToken.class)
@ConditionalOnKindeResourceServerProperties
@EnableConfigurationProperties(KindeOAuth2Properties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class KindeOAuth2ResourceServerAutoConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(KindeOAuth2Properties kindeOAuth2Properties) {
        return new KindeJwtAuthenticationConverter(kindeOAuth2Properties);
    }

    @Bean
    @ConditionalOnMissingBean
    JwtDecoder jwtDecoder(OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
                          KindeOAuth2Properties kindeOAuth2Properties) {

        NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder builder = NimbusJwtDecoder.withJwkSetUri(oAuth2ResourceServerProperties.getJwt().getJwkSetUri());
        builder.restOperations(restTemplate(kindeOAuth2Properties));
        NimbusJwtDecoder decoder = builder.build();
        decoder.setJwtValidator(TokenUtil.jwtValidator(oAuth2ResourceServerProperties.getJwt().getIssuerUri(), kindeOAuth2Properties.getAudience()));
        return decoder;
    }

    /**
     * Builds a {@link RestTemplate} that respects the configured Kinde proxy and adds the standard Kinde
     * request interceptors. Used internally by the JWK fetcher and any other low-level HTTP work that
     * still operates against {@code RestOperations}.
     */
    static RestTemplate restTemplate(KindeOAuth2Properties kindeOAuth2Properties) {

        Proxy proxy = null;

        KindeOAuth2Properties.Proxy proxyProperties = kindeOAuth2Properties.getProxy();
        Optional<BasicAuthenticationInterceptor> basicAuthenticationInterceptor = Optional.empty();
        if (proxyProperties != null && !proxyProperties.getHost().trim().isEmpty() && proxyProperties.getPort() > 0) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyProperties.getHost(), proxyProperties.getPort()));

            if (!proxyProperties.getUsername().trim().isEmpty() &&
                !proxyProperties.getPassword().trim().isEmpty()) {

                basicAuthenticationInterceptor = Optional.of(new BasicAuthenticationInterceptor(proxyProperties.getUsername(),
                    proxyProperties.getPassword()));
            }
        }

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new UserAgentRequestInterceptor());
        restTemplate.getInterceptors().add(new KindeClientRequestInterceptor());
        basicAuthenticationInterceptor.ifPresent(restTemplate.getInterceptors()::add);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if (Objects.nonNull(proxy)) {
            requestFactory.setProxy(proxy);
        }
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

    /**
     * Builds a {@link RestClient} that respects the configured Kinde proxy and adds the standard Kinde
     * request interceptors. Spring Security 7's token response clients (e.g.
     * {@code RestClientAuthorizationCodeTokenResponseClient}) require a {@link RestClient} rather than
     * the legacy {@code RestOperations}.
     */
    static RestClient restClient(KindeOAuth2Properties kindeOAuth2Properties) {

        Proxy proxy = null;

        KindeOAuth2Properties.Proxy proxyProperties = kindeOAuth2Properties.getProxy();
        Optional<BasicAuthenticationInterceptor> basicAuthenticationInterceptor = Optional.empty();
        if (proxyProperties != null && !proxyProperties.getHost().trim().isEmpty() && proxyProperties.getPort() > 0) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyProperties.getHost(), proxyProperties.getPort()));

            if (!proxyProperties.getUsername().trim().isEmpty() &&
                !proxyProperties.getPassword().trim().isEmpty()) {

                basicAuthenticationInterceptor = Optional.of(new BasicAuthenticationInterceptor(proxyProperties.getUsername(),
                    proxyProperties.getPassword()));
            }
        }

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if (Objects.nonNull(proxy)) {
            requestFactory.setProxy(proxy);
        }

        // Spring Security 7's RestClient-based token response clients (e.g.
        // RestClientAuthorizationCodeTokenResponseClient) build their internal RestClient with the
        // FormHttpMessageConverter (for the form-encoded request body) and the
        // OAuth2AccessTokenResponseHttpMessageConverter (which constructs the OAuth2AccessTokenResponse
        // via its Builder, guaranteeing a non-null additionalParameters map). They also install an
        // OAuth2ErrorResponseErrorHandler so non-2xx token endpoint responses surface as
        // OAuth2AuthorizationException. When we replace that RestClient with our own, we must register
        // the same converters / error handler, otherwise the token response gets deserialized by a
        // generic JSON converter into an OAuth2AccessTokenResponse with null additionalParameters,
        // which then NPEs inside OidcAuthorizationCodeAuthenticationProvider when it checks for the
        // id_token. See AbstractRestClientOAuth2AccessTokenResponseClient (Spring Security 7.x).
        RestClient.Builder builder = RestClient.builder()
                .requestFactory(requestFactory)
                .configureMessageConverters(messageConverters -> {
                    messageConverters.addCustomConverter(new FormHttpMessageConverter());
                    messageConverters.addCustomConverter(new OAuth2AccessTokenResponseHttpMessageConverter());
                })
                .defaultStatusHandler(new OAuth2ErrorResponseErrorHandler())
                .requestInterceptor(new UserAgentRequestInterceptor())
                .requestInterceptor(new KindeClientRequestInterceptor());
        basicAuthenticationInterceptor.ifPresent(builder::requestInterceptor);
        return builder.build();
    }
}
