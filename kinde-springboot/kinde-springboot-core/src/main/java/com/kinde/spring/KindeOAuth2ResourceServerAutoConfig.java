/*
 * Copyright 2018-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import com.kinde.spring.http.KindeClientRequestInterceptor;
import com.kinde.spring.http.UserAgentRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

@AutoConfiguration
@AutoConfigureBefore(OAuth2ResourceServerAutoConfiguration.class)
@ConditionalOnClass(JwtAuthenticationToken.class)
//@ConditionalOnOktaResourceServerProperties
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

        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
            new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter(), new StringHttpMessageConverter()
        ));
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
}
