package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class KindeClientCodeSessionImpl extends KindeClientSessionImpl {

    private String code;
    private AuthorizationUrl authorizationUrl;

    @Inject
    public KindeClientCodeSessionImpl(
            KindeConfig kindeConfig,
            OidcMetaData oidcMetaData,
            @KindeAnnotations.KindeCode String code,
            @KindeAnnotations.AuthorizationUrl @Nullable AuthorizationUrl authorizationUrl) {
        super(kindeConfig,oidcMetaData);
        this.code = code;
        this.authorizationUrl = authorizationUrl;
    }

    @Override
    @SneakyThrows
    public List<KindeToken> retrieveTokens() {
        AuthorizationCode code = new AuthorizationCode(this.code);
        URI callback = new URI(this.kindeConfig.redirectUri());
        AuthorizationGrant codeGrant = this.authorizationUrl == null ? new AuthorizationCodeGrant(code, callback) :
                new AuthorizationCodeGrant(code, callback,this.authorizationUrl.getCodeVerifier());

        ClientID clientID = new ClientID(this.kindeConfig.clientId());
        Secret clientSecret = new Secret(this.kindeConfig.clientSecret());
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);
        URI tokenEndpoint = this.oidcMetaData.getOpMetadata().getTokenEndpointURI();

        TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant);

        TokenResponse response = TokenResponse.parse(request.toHTTPRequest().send());
        System.out.println(response.toString());

        if (! response.indicatesSuccess()) {
            // We got an error response...
            throw new Exception(response.toErrorResponse().toString());
        }

        AccessTokenResponse successResponse = response.toSuccessResponse();
        if (successResponse.getTokens() instanceof OIDCTokens) {
            OIDCTokens oidcTokens = successResponse.getTokens().toOIDCTokens();
            return Arrays.asList(
                    com.kinde.token.IDToken.init(oidcTokens.getIDTokenString(), true),
                    com.kinde.token.AccessToken.init(oidcTokens.getAccessToken().getValue(), true),
                    com.kinde.token.RefreshToken.init(oidcTokens.getRefreshToken().getValue(), true));
        } else {
            return Arrays.asList(
                    com.kinde.token.AccessToken.init(successResponse.getTokens().getAccessToken().getValue(), true));
        }
    }
}
