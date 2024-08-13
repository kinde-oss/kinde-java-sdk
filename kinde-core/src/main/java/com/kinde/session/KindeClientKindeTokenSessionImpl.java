package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeToken;
import com.kinde.user.UserInfo;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
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
                com.kinde.token.IDToken.init(successResponse.getTokens().toOIDCTokens().getIDTokenString(),true),
                com.kinde.token.AccessToken.init(successResponse.getTokens().toOIDCTokens().getAccessToken().getValue(),true),
                com.kinde.token.RefreshToken.init(successResponse.getTokens().toOIDCTokens().getRefreshToken().getValue(),true));
    }


    @Override
    @SneakyThrows
    public UserInfo retrieveUserInfo() {
        if (!(this.kindeToken instanceof AccessToken)) {
            throw new IllegalArgumentException("Expected an access token to be present.");
        }
        URI userInfoEndpoint;    // The UserInfoEndpoint of the OpenID provider
        BearerAccessToken token = new BearerAccessToken(this.kindeToken.token()); // The access token

        HTTPResponse httpResponse = new UserInfoRequest(this.oidcMetaData.getOpMetadata().getUserInfoEndpointURI(), token)
                .toHTTPRequest()
                .send();

        UserInfoResponse userInfoResponse = UserInfoResponse.parse(httpResponse);

        if (! userInfoResponse.indicatesSuccess()) {
            // The request failed, e.g. due to invalid or expired token
            System.out.println(userInfoResponse.toErrorResponse().getErrorObject().getCode());
            System.out.println(userInfoResponse.toErrorResponse().getErrorObject().getDescription());
            return null;
        }

        return new UserInfo(userInfoResponse.toSuccessResponse().getUserInfo());
    }
}