package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.authorization.AuthorizationUrl;
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
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class KindeClientCodeSessionImpl extends KindeClientSessionImpl {

    private String code;
    private AuthorizationUrl authorizationUrl;
    private KindeToken kindeToken;

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
        HTTPRequest httpRequest = request.toHTTPRequest();
        httpRequest.setHeader("Kinde-SDK","Java/2.0.0");

        TokenResponse response = TokenResponse.parse(httpRequest.send());
        System.out.println(response.toString());

        if (! response.indicatesSuccess()) {
            // We got an error response...
            throw new Exception(response.toErrorResponse().toString());
        }

        AccessTokenResponse successResponse = response.toSuccessResponse();
        if (successResponse.getTokens() instanceof OIDCTokens) {
            OIDCTokens oidcTokens = successResponse.getTokens().toOIDCTokens();
            this.kindeToken = com.kinde.token.AccessToken.init(oidcTokens.getAccessToken().getValue(), true);
            return Arrays.asList(
                    com.kinde.token.IDToken.init(oidcTokens.getIDTokenString(), true),
                    this.kindeToken,
                    com.kinde.token.RefreshToken.init(oidcTokens.getRefreshToken().getValue(), true));
        } else {
            this.kindeToken = com.kinde.token.AccessToken.init(successResponse.getTokens().getAccessToken().getValue(), true);
            return Arrays.asList(kindeToken);
        }
    }

    @Override
    @SneakyThrows
    public UserInfo retrieveUserInfo() {
        if (this.kindeToken == null) {
            retrieveTokens();
        }
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
