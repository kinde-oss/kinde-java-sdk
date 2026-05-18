package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Field;
import java.util.Optional;


final class KindeOAuth2Configurer extends AbstractHttpConfigurer<KindeOAuth2Configurer, HttpSecurity> {

    private static final Logger log = LoggerFactory.getLogger(KindeOAuth2Configurer.class);

    @SuppressWarnings("rawtypes")
    @Override
    public void init(HttpSecurity http) {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);

        if (context.getBeansOfType(KindeOAuth2Properties.class).isEmpty()) {
            return;
        }
        KindeOAuth2Properties kindeOAuth2Properties = context.getBean(KindeOAuth2Properties.class);

        // Auth Code Flow Config

        // if OAuth2ClientProperties bean is not available do NOT configure
        OAuth2ClientProperties.Provider propertiesProvider;
        OAuth2ClientProperties.Registration propertiesRegistration;
        if (!context.getBeansOfType(OAuth2ClientProperties.class).isEmpty()
            && (propertiesProvider = context.getBean(OAuth2ClientProperties.class).getProvider().get("kinde")) != null
            && (propertiesRegistration = context.getBean(OAuth2ClientProperties.class).getRegistration().get("kinde")) != null
            && StringUtils.hasText(propertiesProvider.getIssuerUri())
            && StringUtils.hasText(propertiesRegistration.getClientId())) {

            configureLogin(http, kindeOAuth2Properties, context.getEnvironment());

            // check for RP-Initiated logout
            if (!context.getBeansOfType(OidcClientInitiatedLogoutSuccessHandler.class).isEmpty()) {
                OidcClientInitiatedLogoutSuccessHandler handler =
                        context.getBean(OidcClientInitiatedLogoutSuccessHandler.class);
                http.logout(logout -> logout.logoutSuccessHandler(handler));
            }

            // Resource Server Config
            OAuth2ResourceServerConfigurer oAuth2ResourceServerConfigurer = http.getConfigurer(OAuth2ResourceServerConfigurer.class);

            if (getJwtConfigurer(oAuth2ResourceServerConfigurer).isPresent()) {
                log.debug("JWT configurer is set in OAuth resource server configuration. " +
                    "JWT validation will be configured.");
                configureResourceServerForJwtValidation(http, kindeOAuth2Properties);
            } else if (getOpaqueTokenConfigurer(oAuth2ResourceServerConfigurer).isPresent()) {
                log.debug("Opaque Token configurer is set in OAuth resource server configuration. " +
                    "Opaque Token validation/introspection will be configured.");
                configureResourceServerForOpaqueTokenValidation(http, kindeOAuth2Properties);
            } else {
                log.debug("OAuth2ResourceServerConfigurer bean not configured, Resource Server support will not be enabled.");
            }
        } else {
            log.debug("Kinde/OIDC Login not configured due to missing issuer, client-id, or client-secret property");
        }
    }

    private Optional<OAuth2ResourceServerConfigurer<?>.JwtConfigurer> getJwtConfigurer(OAuth2ResourceServerConfigurer<?> oAuth2ResourceServerConfigurer) {
        if (oAuth2ResourceServerConfigurer != null) {
            return getFieldValue(oAuth2ResourceServerConfigurer, "jwtConfigurer");
        }
        return Optional.empty();
    }

    private Optional<OAuth2ResourceServerConfigurer<?>.OpaqueTokenConfigurer> getOpaqueTokenConfigurer(OAuth2ResourceServerConfigurer<?> oAuth2ResourceServerConfigurer) {
        if (oAuth2ResourceServerConfigurer != null) {
            return getFieldValue(oAuth2ResourceServerConfigurer, "opaqueTokenConfigurer");
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> getFieldValue(Object source, String fieldName) {
        Field field;
        try {
            field = OAuth2ResourceServerConfigurer.class.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            log.warn("Could not get field '{}' of {} via reflection",
                fieldName, OAuth2ResourceServerConfigurer.class.getName(), e);
            String errMsg = "Expected field '" + fieldName + "' was not found in OAuth resource server configuration. " +
                "Version incompatibility with Spring Security detected.";
            throw new RuntimeException(errMsg, e);
        }

        try {
            return Optional.ofNullable((T) field.get(source));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to access field '" + fieldName + "' on "
                + OAuth2ResourceServerConfigurer.class.getName(), e);
        }
    }

    /**
     * Method to "unset" Jwt Resource Server Configurer using Reflection API.
     * <p>
     * For Root/Org issuer cases, we automatically configure resource server to use Opaque Token validation mode, but Spring
     * brings in the default Jwt configuration, therefore we are unable to set Opaque Token configuration
     * programmatically (startup fails - Spring only supports Jwt or Opaque is supported, not both simultaneously).
     * To address this, we need this helper method to unset Jwt configurer before attempting to set Opaque Token configuration
     * for Root/Org issuer use case.
     */
    @SuppressWarnings({"PMD.UnusedPrivateMethod", "rawtypes"})
    private void unsetJwtConfigurer(OAuth2ResourceServerConfigurer oAuth2ResourceServerConfigurer) {
        try {
            Field field = OAuth2ResourceServerConfigurer.class.getDeclaredField("jwtConfigurer");
            field.setAccessible(true);
            field.set(oAuth2ResourceServerConfigurer, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn("Could not access field 'jwtConfigurer' of {} via reflection",
                OAuth2ResourceServerConfigurer.class.getName(), e);
        }
    }

    private void configureLogin(HttpSecurity http, KindeOAuth2Properties kindeOAuth2Properties, Environment environment) {

        RestClient restClient = KindeOAuth2ResourceServerAutoConfig.restClient(kindeOAuth2Properties);
        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> tokenResponseClient =
                accessTokenResponseClient(restClient);

        String redirectUriProperty = environment.getProperty("spring.security.oauth2.client.registration.kinde.redirect-uri");

        http.oauth2Login(oauth2 -> {
            oauth2.tokenEndpoint(token -> token.accessTokenResponseClient(tokenResponseClient));
            if (redirectUriProperty != null) {
                //  remove `{baseUrl}` pattern, if present, as Spring will solve this on its own
                String redirectUri = redirectUriProperty.replace("{baseUrl}", "");
                oauth2.redirectionEndpoint(redirect -> redirect.baseUri(redirectUri));
            }
        });
    }

    private void configureResourceServerForJwtValidation(HttpSecurity http, KindeOAuth2Properties kindeOAuth2Properties) {
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(
                        new KindeJwtAuthenticationConverter(kindeOAuth2Properties.getPermissionsClaim()))));
    }

    @SuppressWarnings("rawtypes")
    private void configureResourceServerForOpaqueTokenValidation(HttpSecurity http, KindeOAuth2Properties kindeOAuth2Properties) {

        if (!kindeOAuth2Properties.getClientId().isEmpty() && !kindeOAuth2Properties.getClientSecret().isEmpty()) {
            // Spring (2.7.x+) configures JWT by default and this creates a startup failure ("Spring Security
            // only supports JWTs or Opaque Tokens, not both at the same time") when we try to configure Opaque Token
            // mode in the following call. Therefore, we unset the JWT configuration before attempting to configure
            // Opaque Token mode for the ROOT issuer case.

            OAuth2ResourceServerConfigurer existing = http.getConfigurer(OAuth2ResourceServerConfigurer.class);
            if (existing != null) {
                unsetJwtConfigurer(existing);
            }

            http.oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(opaque -> {}));
        }
    }

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient(RestClient restClient) {
        RestClientAuthorizationCodeTokenResponseClient client = new RestClientAuthorizationCodeTokenResponseClient();
        client.setRestClient(restClient);
        return client;
    }
}
