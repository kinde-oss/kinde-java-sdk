package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import lombok.SneakyThrows;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class KindeClientCodeSessionImpl extends KindeClientSessionImpl {

    private String code;

    @Inject
    public KindeClientCodeSessionImpl(
            KindeConfig kindeConfig,
            OidcMetaData oidcMetaData,
            @KindeAnnotations.KindeCode String code) {
        super(kindeConfig,oidcMetaData);
        this.code = code;
    }

    @Override
    @SneakyThrows
    public List<KindeToken> retrieveTokens() {
        AuthorizationCode code = new AuthorizationCode(this.code);
        URI callback = new URI(this.kindeConfig.redirectUri());
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(code, callback);

        ClientID clientID = new ClientID(this.kindeConfig.clientId());
        Secret clientSecret = new Secret(this.kindeConfig.clientSecret());
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);
        URI tokenEndpoint = this.oidcMetaData.getOpMetadata().getTokenEndpointURI();

        TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant);

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
