package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeTokens;
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

import java.net.URI;
import java.net.URL;
import java.util.*;

public class KindeClientSessionImpl implements KindeClientSession {

    protected KindeConfig kindeConfig;
    protected OidcMetaData oidcMetaData;

    @Inject
    public KindeClientSessionImpl(KindeConfig kindeConfig, OidcMetaData oidcMetaData) {
        this.kindeConfig = kindeConfig;
        this.oidcMetaData = oidcMetaData;
    }


    @Override
    @SneakyThrows
    public KindeTokens retrieveTokens() {
        // Construct the client credentials grant
        AuthorizationGrant clientGrant = new ClientCredentialsGrant();

        ClientID clientID = new ClientID(this.kindeConfig.clientId());
        Secret clientSecret = new Secret(this.kindeConfig.clientSecret());
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);

        URI tokenEndpoint = this.oidcMetaData.getOpMetadata().getTokenEndpointURI();
        TokenRequest request = null;
        if (this.kindeConfig.audience() !=null && !this.kindeConfig.audience().isEmpty()) {
            HashMap<String,List<String>> customParameters = new HashMap<>();
            customParameters.put("audience",this.kindeConfig.audience());
            request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant, null,null, customParameters);
        } else {
            request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant);
        }

        HTTPRequest httpRequest = request.toHTTPRequest();
        httpRequest.setHeader("Kinde-SDK","Java/2.0.1");

        // make request
        HTTPResponse httpResponse = httpRequest.send();
        TokenResponse response = TokenResponse.parse(httpResponse);

        if (! response.indicatesSuccess()) {
            // Retrieve the content of the response.
            throw new Exception("Token request failed: " + httpResponse.getContent());
        }

        AccessTokenResponse successResponse = response.toSuccessResponse();

        return new KindeTokens(null,null,
                (AccessToken) AccessToken.init(successResponse.getTokens().getAccessToken().getValue(),true),
                null);

    }

    @Override
    @SneakyThrows
    public AuthorizationUrl authorizationUrl() {
        return authorizationUrlWithParameters(new HashMap<>());
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
            codeVerifier = new CodeVerifier(); // Random 43-character string
            builder = new AuthorizationRequest.Builder(
                    new ResponseType(ResponseType.Value.CODE), clientID)
                    .codeChallenge(codeVerifier,CodeChallengeMethod.S256);
        } else {
            builder = new AuthorizationRequest.Builder(
                    new ResponseType(ResponseType.Value.TOKEN), clientID);
        }
        builder.scope(scope)
                .state(state)
                .redirectionURI(callback)
                .endpointURI(authzEndpoint);

        if (this.kindeConfig.hasSuccessPage() != null && this.kindeConfig.hasSuccessPage()) {
            parameters.put(KindeRequestParameters.HAS_SUCCESS_PAGE,Boolean.TRUE.toString());
        }

        if (this.kindeConfig.lang() != null && !this.kindeConfig.lang().isEmpty()) {
            parameters.put(KindeRequestParameters.LANG,this.kindeConfig.lang());
        }

        if (this.kindeConfig.orgCode() != null && !this.kindeConfig.orgCode().isEmpty()) {
            parameters.put(KindeRequestParameters.ORG_CODE,this.kindeConfig.orgCode());
        }

        // add the custom parameters for either login or register
        if (!parameters.isEmpty()) {
            parameters.forEach(builder::customParameter);
        }

        AuthorizationRequest request = builder.build();

        return new AuthorizationUrl(request.toURI().toURL(),codeVerifier);
    }

    @Override
    public AuthorizationUrl login() {
        return authorizationUrlWithParameters(new HashMap<>());
    }

    @Override
    public AuthorizationUrl createOrg(String orgName, String pricingTableKey) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("prompt",Prompt.Type.CREATE.toString());
        parameters.put("is_create_org",Boolean.TRUE.toString());
        parameters.put("org_name",orgName);
        if (pricingTableKey != null && !pricingTableKey.isEmpty()) {
            parameters.put("pricing_table_key", pricingTableKey);
        }
        return authorizationUrlWithParameters(parameters);
    }

    @Override
    public AuthorizationUrl register(String pricingTableKey) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("prompt",Prompt.Type.CREATE.toString());
        if (pricingTableKey != null && !pricingTableKey.isEmpty()) {
            parameters.put("pricing_table_key", pricingTableKey);
        }
        return authorizationUrlWithParameters(parameters);
    }

    public AuthorizationUrl logout() throws Exception {
        if (this.kindeConfig.logoutRedirectUri() == null || this.kindeConfig.logoutRedirectUri().isEmpty()) {
            throw new Exception("Logout url is not provided");
        }
        return new AuthorizationUrl(new URL(String.format("%s?redirect=%s",this.oidcMetaData.getOpMetadata().getEndSessionEndpointURI().toURL(),
                this.kindeConfig.logoutRedirectUri())),null);
    }

    @Override
    public UserInfo retrieveUserInfo() {
        throw new RuntimeException("Not Implemented");
    }
}
