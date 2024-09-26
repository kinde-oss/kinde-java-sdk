package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;


final class KindeOAuth2Configurer extends AbstractHttpConfigurer<KindeOAuth2Configurer, HttpSecurity> {

    private static final Logger log = LoggerFactory.getLogger(KindeOAuth2Configurer.class);
    @SuppressWarnings("rawtypes")
    @Override
    public void init(HttpSecurity http) throws Exception {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);

        // make sure OktaOAuth2Properties are available
        if (!context.getBeansOfType(KindeOAuth2Properties.class).isEmpty()) {
            KindeOAuth2Properties kindeOAuth2Properties = context.getBean(KindeOAuth2Properties.class);

            // Auth Code Flow Config

            // if OAuth2ClientProperties bean is not available do NOT configure
            OAuth2ClientProperties.Provider propertiesProvider;
            OAuth2ClientProperties.Registration propertiesRegistration;
            if (!context.getBeansOfType(OAuth2ClientProperties.class).isEmpty()
                && (propertiesProvider = context.getBean(OAuth2ClientProperties.class).getProvider().get("kinde")) != null
                && (propertiesRegistration = context.getBean(OAuth2ClientProperties.class).getRegistration().get("kinde")) != null
                && !propertiesProvider.getIssuerUri().isEmpty()
                && !propertiesRegistration.getClientId().isEmpty()) {
                // configure kinde user services
                configureLogin(http, kindeOAuth2Properties, context.getEnvironment());

                // check for RP-Initiated logout
                if (!context.getBeansOfType(OidcClientInitiatedLogoutSuccessHandler.class).isEmpty()) {
                    http.logout().logoutSuccessHandler(context.getBean(OidcClientInitiatedLogoutSuccessHandler.class));
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
    }

    private Optional<OAuth2ResourceServerConfigurer<?>.JwtConfigurer> getJwtConfigurer(OAuth2ResourceServerConfigurer<?> oAuth2ResourceServerConfigurer) throws IllegalAccessException {
        if (oAuth2ResourceServerConfigurer != null) {
            return getFieldValue(oAuth2ResourceServerConfigurer, "jwtConfigurer");
        }
        return Optional.empty();
    }

    private Optional<OAuth2ResourceServerConfigurer<?>.OpaqueTokenConfigurer> getOpaqueTokenConfigurer(OAuth2ResourceServerConfigurer<?> oAuth2ResourceServerConfigurer) throws IllegalAccessException {
        if (oAuth2ResourceServerConfigurer != null) {
            return getFieldValue(oAuth2ResourceServerConfigurer, "opaqueTokenConfigurer");
        }
        return Optional.empty();
    }

    private <T> Optional<T> getFieldValue(Object source, String fieldName) throws IllegalAccessException {
        Field field = AccessController.doPrivileged((PrivilegedAction<Field>) () -> {
            Field result = null;
            try {
                result = OAuth2ResourceServerConfigurer.class.getDeclaredField(fieldName);
                result.setAccessible(true);
            } catch (NoSuchFieldException e) {
                log.warn("Could not get field '" + fieldName + "' of {} via reflection",
                    OAuth2ResourceServerConfigurer.class.getName(), e);
            }
            return result;
        });

        if (field == null) {
            String errMsg = "Expected field '" + fieldName + "' was not found in OAuth resource server configuration. " +
                "Version incompatibility with Spring Security detected." +
                "Check https://github.com/okta/okta-spring-boot for project updates.";
            throw new RuntimeException(errMsg);
        }

        return Optional.ofNullable((T) field.get(source));
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
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void unsetJwtConfigurer(OAuth2ResourceServerConfigurer oAuth2ResourceServerConfigurer) {

        AccessController.doPrivileged((PrivilegedAction<Field>) () -> {
            Field result = null;
            try {
                result = OAuth2ResourceServerConfigurer.class.getDeclaredField("jwtConfigurer");
                result.setAccessible(true);

                result.set(oAuth2ResourceServerConfigurer, null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.warn("Could not access field '" + "jwtConfigurer" + "' of {} via reflection",
                    OAuth2ResourceServerConfigurer.class.getName(), e);
            }
            return result;
        });
    }

    private void configureLogin(HttpSecurity http, KindeOAuth2Properties kindeOAuth2Properties, Environment environment) throws Exception {

        RestTemplate restTemplate = KindeOAuth2ResourceServerAutoConfig.restTemplate(kindeOAuth2Properties);

        http.oauth2Login()
            .tokenEndpoint()
            .accessTokenResponseClient(accessTokenResponseClient(restTemplate));

        String redirectUriProperty = environment.getProperty("spring.security.oauth2.client.registration.kinde.redirect-uri");
        if (redirectUriProperty != null) {
            //  remove `{baseUrl}` pattern, if present, as Spring will solve this on its own
            String redirectUri = redirectUriProperty.replace("{baseUrl}", "");
            http.oauth2Login().redirectionEndpoint().baseUri(redirectUri);
        }
    }

    private void configureResourceServerForJwtValidation(HttpSecurity http, KindeOAuth2Properties kindeOAuth2Properties) throws Exception {
        http.oauth2ResourceServer()
            .jwt().jwtAuthenticationConverter(new KindeJwtAuthenticationConverter(kindeOAuth2Properties.getPermissionsClaim()));
    }

    private void configureResourceServerForOpaqueTokenValidation(HttpSecurity http, KindeOAuth2Properties kindeOAuth2Properties) throws Exception {

        if (!kindeOAuth2Properties.getClientId().isEmpty() && !kindeOAuth2Properties.getClientSecret().isEmpty()) {
            // Spring (2.7.x+) configures JWT be default and this creates startup failure "Spring Security
            // only supports JWTs or Opaque Tokens, not both at the same time" when we try to configure Opaque Token mode in following line.
            // Therefore, we are unsetting JWT mode before attempting to configure Opaque Token mode for ROOT issuer case.

            if (http.getConfigurer(OAuth2ResourceServerConfigurer.class) != null) {
                unsetJwtConfigurer(http.getConfigurer(OAuth2ResourceServerConfigurer.class));
            }

            http.oauth2ResourceServer().opaqueToken();
        }
    }

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient(RestTemplate restTemplate) {

        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        accessTokenResponseClient.setRestOperations(restTemplate);

        return accessTokenResponseClient;
    }
}
