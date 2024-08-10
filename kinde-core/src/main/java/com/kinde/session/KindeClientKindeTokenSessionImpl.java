package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import lombok.SneakyThrows;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class KindeClientKindeTokenSessionImpl extends KindeClientSessionImpl {

    private KindeToken kindeToken;

    @Inject
    public KindeClientKindeTokenSessionImpl(
            KindeConfig kindeConfig,
            OidcMetaData oidcMetaData,
            @KindeAnnotations.KindeToken KindeToken kindeToken) {
        super(kindeConfig,oidcMetaData);
        this.kindeToken = kindeToken;
    }

    @Override
    @SneakyThrows
    public List<KindeToken> retrieveTokens() {
        // Construct the grant from the saved refresh token
        RefreshToken refreshToken = new RefreshToken(kindeToken.token());
        AuthorizationGrant refreshTokenGrant = new RefreshTokenGrant(refreshToken);

        ClientID clientID = new ClientID(this.kindeConfig.clientId());
        Secret clientSecret = new Secret(this.kindeConfig.clientSecret());
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);

        URI tokenEndpoint = this.oidcMetaData.getOpMetadata().getTokenEndpointURI();

        TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, refreshTokenGrant);

        TokenResponse response = TokenResponse.parse(request.toHTTPRequest().send());

        if (! response.indicatesSuccess()) {
            // We got an error response...
            throw new Exception(response.toErrorResponse().toString());
        }

        AccessTokenResponse successResponse = response.toSuccessResponse();

        return Arrays.asList(
                com.kinde.token.AccessToken.init(successResponse.getTokens().getAccessToken().getValue(),true),
                com.kinde.token.RefreshToken.init(successResponse.getTokens().getRefreshToken().getValue(),true));
    }
}
