package com.kinde.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.kinde.KindeClientSession;
import com.kinde.KindeTokenFactory;
import com.kinde.accounts.KindeAccountsClient;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.entitlements.KindeEntitlements;
import com.kinde.exceptions.KindeClientSessionException;
import com.kinde.token.KindeTokens;
import com.kinde.token.KindeTokenFactoryImpl;
import com.kinde.token.KindeToken;
import com.kinde.token.AccessToken;
import com.kinde.token.jwk.KindeJwkStore;
import com.kinde.token.jwk.KindeJwkStoreImpl;
import com.kinde.user.UserInfo;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.Prompt;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class KindeClientSessionImpl implements KindeClientSession {

    protected KindeConfig kindeConfig;
    protected OidcMetaData oidcMetaData;
    private KindeEntitlements entitlements;
    private KindeTokenFactory tokenFactory;

    @Inject
    public KindeClientSessionImpl(KindeConfig kindeConfig, OidcMetaData oidcMetaData) {
        this.kindeConfig = kindeConfig;
        this.oidcMetaData = oidcMetaData;
    }

    /**
     * Sets the entitlements instance. This method is called by Guice after construction
     * to inject the KindeEntitlements dependency.
     * 
     * @param entitlements The KindeEntitlements instance to inject
     */
    @Inject
    public void setEntitlements(KindeEntitlements entitlements) {
        this.entitlements = entitlements;
    }

    /**
     * Sets the token factory instance. This method is called by Guice after construction
     * to inject the KindeTokenFactory dependency.
     * 
     * @param tokenFactory The KindeTokenFactory instance to inject
     */
    @Inject
    public void setTokenFactory(KindeTokenFactory tokenFactory) {
        this.tokenFactory = tokenFactory;
    }

    /**
     * Creates a KindeTokenFactory for token parsing.
     * This method creates the factory manually when Guice injection is not available.
     * 
     * @return KindeTokenFactory instance, or null if creation fails
     */
    private KindeTokenFactory createTokenFactory() {
        try {
            // Create KindeJwkStore and KindeTokenFactory manually
            KindeJwkStore jwkStore = new KindeJwkStoreImpl(this.oidcMetaData);
            return new KindeTokenFactoryImpl(jwkStore);
        } catch (Exception e) {
            log.warn("Failed to create KindeTokenFactory: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Creates a KindeAccountsClient for hard check functionality.
     * This method creates the client lazily to avoid circular dependencies.
     * 
     * @return KindeAccountsClient instance, or null if creation fails
     */
    private KindeAccountsClient createAccountsClient() {
        try {
            // Create KindeAccountsClient using the backward compatibility constructor
            // This avoids circular dependency issues
            return new KindeAccountsClient(this, true);
        } catch (Exception e) {
            log.warn("Failed to create KindeAccountsClient for hard check functionality: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Gets the token factory, creating it if necessary.
     * 
     * @return KindeTokenFactory instance, or null if creation fails
     */
    private KindeTokenFactory getTokenFactory() {
        if (tokenFactory != null) {
            return tokenFactory;
        }
        return createTokenFactory();
    }

    @Override
    @SneakyThrows
    public KindeTokens retrieveTokens() {
        AuthorizationGrant clientGrant = new ClientCredentialsGrant();

        ClientID clientID = new ClientID(this.kindeConfig.clientId());
        Secret clientSecret = new Secret(this.kindeConfig.clientSecret());
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);

        URI tokenEndpoint = this.oidcMetaData.getOpMetadata().getTokenEndpointURI();
        TokenRequest request;
        if (this.kindeConfig.audience() != null && !this.kindeConfig.audience().isEmpty()) {
            HashMap<String, List<String>> customParameters = new HashMap<>();
            customParameters.put("audience", this.kindeConfig.audience());
            request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant, null, null, customParameters);
        } else {
            request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant);
        }

        HTTPRequest httpRequest = request.toHTTPRequest();
        httpRequest.setHeader("Kinde-SDK", "Java/2.0.1");

        HTTPResponse httpResponse = httpRequest.send();
        TokenResponse response = TokenResponse.parse(httpResponse);

        if (!response.indicatesSuccess()) {
            throw new Exception("Token request failed: " + httpResponse.getContent());
        }

        AccessTokenResponse successResponse = response.toSuccessResponse();
        String accessTokenValue = successResponse.getTokens().getAccessToken().getValue();

        // Use token factory to create tokens with hard check capabilities
        KindeTokenFactory factory = getTokenFactory();
        if (factory != null) {
            try {
                // Try to create token with hard check functionality
                KindeAccountsClient accountsClient = createAccountsClient();
                if (accountsClient != null) {
                    KindeToken token = factory.parse(accessTokenValue, accountsClient);
                    if (token instanceof AccessToken) {
                        return new KindeTokens(null, null, (AccessToken) token, null);
                    } else {
                        log.warn("Expected AccessToken but got: {}", token.getClass().getSimpleName());
                    }
                }
            } catch (Exception e) {
                log.debug("Failed to create token with hard check functionality, falling back to basic token: {}", e.getMessage());
            }
            
            // Fallback to basic token creation
            KindeToken token = factory.parse(accessTokenValue);
            if (token instanceof AccessToken) {
                return new KindeTokens(null, null, (AccessToken) token, null);
            } else {
                log.warn("Expected AccessToken but got: {}", token.getClass().getSimpleName());
            }
        } else {
            // Fallback when token factory is not available
            log.warn("TokenFactory not available, creating basic token without hard check functionality");
        }
        
        // Final fallback - create basic AccessToken
        return new KindeTokens(null, null, AccessToken.init(accessTokenValue, true), null);
    }

    @Override
    @SneakyThrows
    public AuthorizationUrl authorizationUrl() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("supports_reauth", "true");
        return authorizationUrlWithParameters(parameters);
    }

    @Override
    @SneakyThrows
    public AuthorizationUrl authorizationUrlWithParameters(Map<String, String> parameters) {
        URI authzEndpoint = this.oidcMetaData.getOpMetadata().getAuthorizationEndpointURI();
        ClientID clientID = new ClientID(this.kindeConfig.clientId());
        Scope scope = new Scope();
        if (this.kindeConfig.scopes() != null && !this.kindeConfig.scopes().isEmpty()) {
            scope = new Scope(this.kindeConfig.scopes().toArray(new String[0]));
        }
        URI callback = new URI(this.kindeConfig.redirectUri());
        State state = new State();

        CodeVerifier codeVerifier = null;
        AuthorizationRequest.Builder builder = null;
        if (this.kindeConfig.grantType() == AuthorizationType.CODE) {
            codeVerifier = new CodeVerifier();
            builder = new AuthorizationRequest.Builder(
                    new ResponseType(ResponseType.Value.CODE), clientID)
                    .codeChallenge(codeVerifier, CodeChallengeMethod.S256);
        } else {
            builder = new AuthorizationRequest.Builder(
                    new ResponseType(ResponseType.Value.TOKEN), clientID);
        }
        builder.scope(scope)
                .state(state)
                .redirectionURI(callback)
                .endpointURI(authzEndpoint);

        if (this.kindeConfig.hasSuccessPage() != null && this.kindeConfig.hasSuccessPage()) {
            parameters.put(KindeRequestParameters.HAS_SUCCESS_PAGE, Boolean.TRUE.toString());
        }

        if (this.kindeConfig.lang() != null && !this.kindeConfig.lang().isEmpty()) {
            parameters.put(KindeRequestParameters.LANG, this.kindeConfig.lang());
        }

        if (this.kindeConfig.orgCode() != null && !this.kindeConfig.orgCode().isEmpty()) {
            parameters.put(KindeRequestParameters.ORG_CODE, this.kindeConfig.orgCode());
        }

        if (!parameters.isEmpty()) {
            parameters.forEach(builder::customParameter);
        }

        AuthorizationRequest request = builder.build();

        return new AuthorizationUrl(request.toURI().toURL(), codeVerifier);
    }

    @Override
    public AuthorizationUrl login() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("supports_reauth", "true");
        return authorizationUrlWithParameters(parameters);
    }

    @Override
    public AuthorizationUrl createOrg(String orgName) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("prompt", Prompt.Type.CREATE.toString());
        parameters.put("is_create_org", Boolean.TRUE.toString());
        parameters.put("org_name", orgName);
        return authorizationUrlWithParameters(parameters);
    }

    @Override
    public AuthorizationUrl register() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("prompt", Prompt.Type.CREATE.toString());
        parameters.put("supports_reauth", "true");
        return authorizationUrlWithParameters(parameters);
    }

    @Override
    public AuthorizationUrl logout() throws Exception {
        return logout(this.kindeConfig.logoutRedirectUri());
    }

    @Override
    public AuthorizationUrl logout(String logoutRedirectUri) throws Exception {
        if (logoutRedirectUri == null || logoutRedirectUri.isEmpty()) {
            throw new Exception("Logout redirect URI is not provided");
        }
        return new AuthorizationUrl(new URL(String.format("%s?redirect=%s",this.oidcMetaData.getOpMetadata().getEndSessionEndpointURI().toURL(),
                logoutRedirectUri)),null);
    }

    @Override
    public UserInfo retrieveUserInfo() {
        throw new RuntimeException("Not Implemented");
    }

    /**
     * Generates a portal URL for the user to access their account portal.
     *
     * @param domain    The base domain of the Kinde service (e.g., "https://example.kinde.com").
     * @param returnUrl URL to redirect to after completing the profile flow.
     * @param subNav    Optional sub-navigation parameter to specify which section of the portal to open.
     * @return An AuthorizationUrl containing the generated portal URL.
     */
    @Override
    @SneakyThrows
    public AuthorizationUrl generatePortalUrl(String domain, String returnUrl, String subNav) {
        if (returnUrl == null || !returnUrl.startsWith("http")) {
            throw new IllegalArgumentException("generatePortalUrl: returnUrl must be an absolute URL");
        }

        String accessToken = null;
        KindeTokens tokens = retrieveTokens();
        if (tokens != null && tokens.getAccessToken() != null) {
            accessToken = tokens.getAccessToken().token();
        }
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalStateException("generatePortalUrl: Access Token not found");
        }

        String subNavValue = (subNav != null && !subNav.isEmpty()) ? subNav : "profile";
        String params = String.format("sub_nav=%s&return_url=%s",
                URLEncoder.encode(subNavValue, StandardCharsets.UTF_8),
                URLEncoder.encode(returnUrl, StandardCharsets.UTF_8));

        String sanitizedDomain = domain.endsWith("/") ? domain.substring(0, domain.length() - 1) : domain;
        String urlString = sanitizedDomain + "/account_api/v1/portal_link?" + params;
        URL url = new URL(urlString);

        log.info("Generating portal URL: {}", url);

        HttpURLConnection conn = openConnection(url);
        try {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                log.error("Failed to fetch profile URL: {} {}", responseCode, conn.getResponseMessage());
                throw new KindeClientSessionException("Failed to fetch profile URL: " + responseCode + " " + conn.getResponseMessage());
            }

            String responseBody;
            try (InputStream is = conn.getInputStream();
                 Scanner s = new Scanner(is).useDelimiter("\\A")) {
                responseBody = s.hasNext() ? s.next() : "";
            }

            String portalUrl = getPortalUrl(responseBody)
                    .orElseThrow(() -> new KindeClientSessionException("Failed to extract portal URL from response"));
            return new AuthorizationUrl(new URL(portalUrl), null);
        } finally {
            conn.disconnect();
        }
    }

    private static Optional<String> getPortalUrl(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode urlNode = rootNode.get("url");
            if (urlNode == null || urlNode.asText().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(urlNode.asText());
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    /**
     * For testability: allows mocking HTTP connections in tests.
     */
    protected HttpURLConnection openConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    @Override
    public KindeEntitlements entitlements() {
        return this.entitlements;
    }

    @Override
    public String getDomain() {
        return this.kindeConfig.domain();
    }

    @Override
    public String getAccessToken() {
        try {
            KindeTokens tokens = retrieveTokens();
            if (tokens != null && tokens.getAccessToken() != null) {
                return tokens.getAccessToken().token();
            }
        } catch (Exception e) {
            log.warn("Failed to retrieve access token", e);
        }
        return null;
    }
}
