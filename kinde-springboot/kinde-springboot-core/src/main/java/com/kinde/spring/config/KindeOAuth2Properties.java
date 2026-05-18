package com.kinde.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientProperties;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.Set;

@ConfigurationProperties("kinde.oauth2")
public final class KindeOAuth2Properties implements Validator {

    private final OAuth2ClientProperties clientProperties;

    /**
     * Login route path. This property should NOT be used with applications that have multiple OAuth2 providers.
     * NOTE: this does NOT work with WebFlux, where the redirect URI is derived from the OAuth2 client
     * registration id and defaults to {@code /login/oauth2/code/kinde} for the Kinde registration.
     */
    private String redirectUri;

    /**
     *  OAuth2 clientId value.
     */
    private String clientId;

    /**
     * OAuth2 client secret value.
     */
    private String clientSecret;

    /**
     * OAuth2 client secret value.
     */
    private String authorizationGrantType;

    /**
     * Kinde authorization server URL: e.g. {@code https://your-subdomain.kinde.com}. This is the
     * issuer URI for the OIDC provider.
     */
    private String domain;

    /**
     * Authorization scopes.
     */
    private Set<String> scopes;

    /**
     * Expected access token audience claim value.
     *
     * <p>Defaults to {@code null}, meaning audience validation is disabled. This matches Kinde's
     * out-of-the-box behaviour: tokens issued for clients without a configured API resource
     * carry an empty {@code aud} array. When you have a Kinde API resource configured, set this
     * property to the matching audience value so {@link com.kinde.spring.TokenUtil#jwtValidator}
     * enforces it.
     */
    private String audience;

    /**
     * Access token permissions claim key.
     */
    private String permissionsClaim = "permissions";

    /**
     * URL to redirect to after an RP-Initiated logout (SSO Logout).
     */
    private String postLogoutRedirectUri;

    /**
     * Proxy Properties
     */
    private Proxy proxy;

    // work around for https://github.com/spring-projects/spring-boot/issues/17035
    private KindeOAuth2Properties() {
        this(null);
    }

    @Autowired
    public KindeOAuth2Properties(@Autowired(required = false) OAuth2ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    public String getClientId() {
        return getRegistration().map(OAuth2ClientProperties.Registration::getClientId)
                                .orElse(clientId);
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return getRegistration().map(OAuth2ClientProperties.Registration::getClientSecret)
                                .orElse(clientSecret);
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAuthorizationGrantType() {
        return getRegistration().map(OAuth2ClientProperties.Registration::getClientSecret)
                .orElse(authorizationGrantType);
    }

    public void setAuthorizationGrantType(String authorizationGrantType) {
         this.authorizationGrantType = authorizationGrantType;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getPermissionsClaim() {
        return permissionsClaim;
    }

    public void setPermissionsClaim(String permissionsClaim) {
        this.permissionsClaim = permissionsClaim;
    }


    public Set<String> getScopes() {
        return getRegistration().map(OAuth2ClientProperties.Registration::getScope)
                                .orElse(scopes);
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    private Optional<OAuth2ClientProperties.Registration> getRegistration() {
        return Optional.ofNullable(this.clientProperties.getRegistration().get("kinde"));
    }

    public String getPostLogoutRedirectUri() {
        return postLogoutRedirectUri;
    }

    public void setPostLogoutRedirectUri(String postLogoutRedirectUri) {
        this.postLogoutRedirectUri = postLogoutRedirectUri;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return KindeOAuth2Properties.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {


        KindeOAuth2Properties properties = (KindeOAuth2Properties) target;
        // TODO: Add extra validation
        /*
        if (properties.getClientId() != null) {
        ConfigurationValidator.validateClientId(properties.getClientId()).ifInvalid(res ->
                errors.rejectValue("clientId", res.getMessage()));
        }

        if (properties.getClientSecret() != null) {
            ConfigurationValidator.validateClientSecret(properties.getClientSecret()).ifInvalid(res ->
                    errors.rejectValue("clientSecret", res.getMessage()));
        }

        if (properties.getIssuer() != null) {
            ConfigurationValidator.validateIssuer(properties.getIssuer()).ifInvalid(res ->
                    errors.rejectValue("issuer", res.getMessage()));
        }*/
    }

    public static class Proxy {

        private String host;

        private int port;

        private String username;

        private String password;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}