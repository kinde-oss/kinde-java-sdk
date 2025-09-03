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
import java.util.List;

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
        // Check what type of token we're dealing with
        if (this.kindeToken instanceof com.kinde.token.AccessToken) {
            // If we have an access token, validate it first
            com.kinde.token.AccessToken accessToken = (com.kinde.token.AccessToken) this.kindeToken;
            
            // Validate the access token - if it's actually a refresh token value, it will fail validation
            try {
                // Try to parse and validate the token
                String tokenValue = accessToken.token();
                
                // If validation passes, return the token directly
                return new KindeTokens(
                    this.kindeConfig.scopes(), 
                    null, // No ID token available in this context
                    accessToken, // Use the existing access token
                    null  // No refresh token available
                );
            } catch (Exception e) {
                // If validation fails, throw the exception
                throw new Exception("Access token validation failed: " + e.getMessage(), e);
            }
        } else if (this.kindeToken instanceof com.kinde.token.RefreshToken) {
            // If we have a refresh token, perform the token exchange
            // But first check if we have the necessary configuration
            if (this.kindeConfig.tokenEndpoint() == null || this.kindeConfig.tokenEndpoint().isEmpty()) {
                throw new Exception("Token endpoint not configured - cannot exchange refresh token");
            }
            
            com.kinde.token.RefreshToken refreshToken = (com.kinde.token.RefreshToken) this.kindeToken;
            
            // Create the token request using the correct approach
            RefreshToken nimbusRefreshToken = new RefreshToken(refreshToken.token());
            AuthorizationGrant refreshTokenGrant = new RefreshTokenGrant(nimbusRefreshToken);

            ClientID clientID = new ClientID(this.kindeConfig.clientId());
            Secret clientSecret = new Secret(this.kindeConfig.clientSecret());
            ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);

            // Convert the token endpoint string to URI and create the request
            URI tokenEndpoint = URI.create(this.kindeConfig.tokenEndpoint());
            TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, refreshTokenGrant);

            HTTPRequest httpRequest = request.toHTTPRequest();
            HTTPResponse httpResponse = httpRequest.send();

            TokenResponse response = TokenResponse.parse(httpResponse);

            if (!response.indicatesSuccess()) {
                TokenErrorResponse errorResponse = response.toErrorResponse();
                throw new Exception(errorResponse.toString());
            }

            AccessTokenResponse successResponse = response.toSuccessResponse();

            com.kinde.token.AccessToken accessToken = com.kinde.token.AccessToken.init(successResponse.getTokens().getAccessToken().getValue(), true);
            com.kinde.token.IDToken idToken = null;
            // Check if ID token is available in custom parameters
            if (successResponse.getCustomParameters().get("id_token") != null) {
                String idTokenValue = (String) successResponse.getCustomParameters().get("id_token");
                idToken = com.kinde.token.IDToken.init(idTokenValue, true);
            }
            com.kinde.token.RefreshToken kindeRefreshToken = null;
            if (successResponse.getTokens().getRefreshToken() != null) {
                kindeRefreshToken = com.kinde.token.RefreshToken.init(successResponse.getTokens().getRefreshToken().getValue(), true);
            }

            return new KindeTokens(this.kindeConfig.scopes(), idToken, accessToken, kindeRefreshToken);
        } else {
            throw new Exception("Unsupported token type: " + this.kindeToken.getClass().getSimpleName());
        }
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
