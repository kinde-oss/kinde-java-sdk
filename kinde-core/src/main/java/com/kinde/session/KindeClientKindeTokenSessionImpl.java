package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.entitlements.KindeEntitlements;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.AccessToken;
import com.kinde.token.IDToken;
import com.kinde.token.KindeToken;
import com.kinde.token.KindeTokens;
import com.kinde.user.UserInfo;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import lombok.SneakyThrows;

import java.net.URI;

/**
 * Implementation of KindeClientSession for token-based sessions.
 * This implementation extends KindeClientSessionImpl and inherits entitlements functionality
 * through Guice dependency injection.
 */
public class KindeClientKindeTokenSessionImpl extends KindeClientSessionImpl {

    private KindeToken kindeToken;

    @Inject
    public KindeClientKindeTokenSessionImpl(
            KindeConfig kindeConfig,
            OidcMetaData oidcMetaData,
            @KindeAnnotations.KindeToken KindeToken kindeToken) {
        super(kindeConfig, oidcMetaData);
        this.kindeToken = kindeToken;
    }

    /**
     * Sets the entitlements instance. This method is called by Guice after construction
     * to inject the KindeEntitlements dependency.
     * 
     * @param entitlements The KindeEntitlements instance to inject
     */
    @Inject
    public void setEntitlements(KindeEntitlements entitlements) {
        super.setEntitlements(entitlements);
    }

    @Override
    @SneakyThrows
    public KindeTokens retrieveTokens() {
        RefreshToken refreshToken = new RefreshToken(kindeToken.token());
        AuthorizationGrant refreshTokenGrant = new RefreshTokenGrant(refreshToken);

        ClientID clientID = new ClientID(this.kindeConfig.clientId());
        Secret clientSecret = new Secret(this.kindeConfig.clientSecret());
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);

        URI tokenEndpoint = this.oidcMetaData.getOpMetadata().getTokenEndpointURI();

        TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, refreshTokenGrant);
        HTTPRequest httpRequest = request.toHTTPRequest();
        httpRequest.setHeader("Kinde-SDK", "Java/2.0.1");

        TokenResponse response = TokenResponse.parse(httpRequest.send());

        if (!response.indicatesSuccess()) {
            throw new Exception(response.toErrorResponse().toString());
        }

        AccessTokenResponse successResponse = response.toSuccessResponse();

        String idTokenStr = (String) successResponse.getCustomParameters().get("id_token");

        IDToken idToken = null;
        if (idTokenStr != null) {
            idToken = IDToken.init(idTokenStr, true);
        }

        AccessToken accessToken = com.kinde.token.AccessToken.init(successResponse.getTokens().getAccessToken().getValue(), true);
        com.kinde.token.RefreshToken kindeRefreshToken = null;
        if (successResponse.getTokens().getRefreshToken() != null) {
            kindeRefreshToken = com.kinde.token.RefreshToken.init(successResponse.getTokens().getRefreshToken().getValue(), true);
        }

        return new KindeTokens(this.kindeConfig.scopes(), idToken, accessToken, kindeRefreshToken);
    }


    @Override
    @SneakyThrows
    public UserInfo retrieveUserInfo() {
        if (!(this.kindeToken instanceof AccessToken)) {
            throw new IllegalArgumentException("Expected an access token to be present.");
        }
        BearerAccessToken token = new BearerAccessToken(this.kindeToken.token());

        HTTPResponse httpResponse = new UserInfoRequest(this.oidcMetaData.getOpMetadata().getUserInfoEndpointURI(), token)
                .toHTTPRequest()
                .send();

        UserInfoResponse userInfoResponse = UserInfoResponse.parse(httpResponse);

        if (!userInfoResponse.indicatesSuccess()) {
            throw new Exception(userInfoResponse.toErrorResponse().toString());
        }

        return new UserInfo(userInfoResponse.toSuccessResponse().getUserInfo());
    }
}
