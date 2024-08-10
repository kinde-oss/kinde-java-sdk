package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.client.KindeClientImpl;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;
import com.kinde.user.UserInfo;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import lombok.SneakyThrows;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

        Scope scope = new Scope(this.kindeConfig.scopes().toArray(new String[0]));

        URI tokenEndpoint = this.oidcMetaData.getOpMetadata().getTokenEndpointURI();

        TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant, scope);

        TokenResponse response = TokenResponse.parse(request.toHTTPRequest().send());

        if (! response.indicatesSuccess()) {
            // We got an error response...
            TokenErrorResponse errorResponse = response.toErrorResponse();
        }

        AccessTokenResponse successResponse = response.toSuccessResponse();

        return Arrays.asList(
                com.kinde.token.AccessToken.init(successResponse.getTokens().getAccessToken().getValue(),true));
    }

    @Override
    @SneakyThrows
    public URL authorizationUrl(AuthorizationType authorizationType) {
        URI authzEndpoint = this.oidcMetaData.getOpMetadata().getAuthorizationEndpointURI();
        ClientID clientID = new ClientID(this.kindeConfig.clientId());
        Scope scope = new Scope(this.kindeConfig.scopes().toArray(new String[0]));
        URI callback = new URI(this.kindeConfig.redirectUri());
        State state = new State();

        AuthorizationRequest request = new AuthorizationRequest.Builder(
                new ResponseType(authorizationType == AuthorizationType.CODE ? ResponseType.Value.CODE : ResponseType.Value.TOKEN), clientID)
                .scope(scope)
                .state(state)
                .redirectionURI(callback)
                .endpointURI(authzEndpoint)
                .build();

        return request.toURI().toURL();
    }

    @Override
    public UserInfo retrieveUserInfo() {
        throw new NotImplementedException();
    }
}
