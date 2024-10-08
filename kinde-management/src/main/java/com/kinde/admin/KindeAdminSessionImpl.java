package com.kinde.admin;

import com.kinde.KindeAdminSession;
import com.kinde.KindeClient;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeToken;
import com.kinde.token.KindeTokens;
import org.openapitools.client.ApiClient;
import org.openapitools.client.auth.Authentication;
import org.openapitools.client.auth.HttpBearerAuth;
import org.openapitools.client.auth.OAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KindeAdminSessionImpl implements KindeAdminSession {
    private KindeClient kindeClient;

    public KindeAdminSessionImpl(KindeClient kindeClient) {
        this.kindeClient = kindeClient;
    }


    @Override
    public ApiClient initClient() {
        if (kindeClient == null) {
            Map<String, Authentication> authMap = new HashMap<>();
            authMap.put("ManagementAPI",new OAuth(
                    "https://kinde.com","http://kinde.com/oauth2/token"));
            return new ApiClient(authMap);
        }
        KindeTokens kindeTokens = kindeClient.clientSession().retrieveTokens();
        if (kindeTokens.getAccessToken() == null) {
            throw new IllegalStateException("Invalid session type.");
        }
        AccessToken accessToken = kindeTokens.getAccessToken();
        HttpBearerAuth httpBearerAuth = new HttpBearerAuth("bearer");
        httpBearerAuth.setBearerToken(accessToken.token());
        Map<String, Authentication> authMap = new HashMap<>();
        authMap.put("ManagementAPI",new OAuth(
                kindeClient.kindeConfig().domain(),kindeClient.oidcMetaData().getOpMetadata().getTokenEndpointURI().toString()));
        ApiClient apiClient = new ApiClient(authMap);
        apiClient.setBasePath(kindeClient.kindeConfig().domain());
        apiClient.setAccessToken(accessToken.token());
        return apiClient;
    }
}
