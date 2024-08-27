package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.client.KindeClientImpl;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeToken;
import com.kinde.user.UserInfo;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallenge;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
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
    public List<KindeToken> retrieveTokens() {
        // Construct the client credentials grant
        AuthorizationGrant clientGrant = new ClientCredentialsGrant();

        ClientID clientID = new ClientID(this.kindeConfig.clientId());
        Secret clientSecret = new Secret(this.kindeConfig.clientSecret());
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);

        Scope scope = new Scope();
        if (this.kindeConfig.scopes() != null && !this.kindeConfig.scopes().isEmpty()) {
            scope = new Scope(this.kindeConfig.scopes().toArray(new String[0]));
        }

        URI tokenEndpoint = this.oidcMetaData.getOpMetadata().getTokenEndpointURI();
        TokenRequest request = null;
        if (this.kindeConfig.audience() !=null && !this.kindeConfig.audience().isEmpty()) {
            HashMap<String,List<String>> customParameters = new HashMap<>();
            customParameters.put("audience",this.kindeConfig.audience());
            request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant, scope,null, customParameters);
        } else {
            request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant, scope);
        }

        HTTPRequest httpRequest = request.toHTTPRequest();
        httpRequest.setHeader("Kinde-SDK","Java/2.0.0");

        TokenResponse response = TokenResponse.parse(httpRequest.send());

        if (! response.indicatesSuccess()) {
            // We got an error response...
            throw new Exception("Token request failed: " + response.toErrorResponse().toString());
        }

        AccessTokenResponse successResponse = response.toSuccessResponse();

        return Arrays.asList(
                AccessToken.init(successResponse.getTokens().getAccessToken().getValue(),true));
    }

    @Override
    @SneakyThrows
    public AuthorizationUrl authorizationUrl() {
        URI authzEndpoint = this.oidcMetaData.getOpMetadata().getAuthorizationEndpointURI();
        ClientID clientID = new ClientID(this.kindeConfig.clientId());
        Scope scope = new Scope();
        if (this.kindeConfig.scopes() != null && !this.kindeConfig.scopes().isEmpty()) {
            scope = new Scope(this.kindeConfig.scopes().toArray(new String[0]));
        }
        URI callback = new URI(this.kindeConfig.redirectUri());
        State state = new State();

        if (this.kindeConfig.grantType() == AuthorizationType.CODE) {
            CodeVerifier codeVerifier = new CodeVerifier(); // Random 43-character string
            AuthorizationRequest request = new AuthorizationRequest.Builder(
                    new ResponseType(this.kindeConfig.grantType() == AuthorizationType.CODE ? ResponseType.Value.CODE : ResponseType.Value.TOKEN), clientID)
                    .scope(scope)
                    .state(state)
                    .redirectionURI(callback)
                    .endpointURI(authzEndpoint)
                    .codeChallenge(codeVerifier,CodeChallengeMethod.S256)
                    .build();
            return new AuthorizationUrl(request.toURI().toURL(),codeVerifier);
        } else {
            AuthorizationRequest request = new AuthorizationRequest.Builder(
                    new ResponseType(this.kindeConfig.grantType() == AuthorizationType.CODE ? ResponseType.Value.CODE : ResponseType.Value.TOKEN), clientID)
                    .scope(scope)
                    .state(state)
                    .redirectionURI(callback)
                    .endpointURI(authzEndpoint)
                    .build();
            return new AuthorizationUrl(request.toURI().toURL(),null);
        }
    }

    @Override
    public UserInfo retrieveUserInfo() {
        throw new RuntimeException("Not Implemented");
    }
}
