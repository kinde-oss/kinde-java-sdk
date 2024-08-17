package com.kinde.admin;

import com.kinde.KindeAdminSession;
import com.kinde.KindeClient;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeToken;
import org.openapitools.client.ApiClient;
import org.openapitools.client.auth.Authentication;
import org.openapitools.client.auth.HttpBearerAuth;

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
            return new ApiClient();
        }
        List<KindeToken> tokens = kindeClient.clientSession().retrieveTokens();
        if (!tokens.stream().allMatch(token->token instanceof AccessToken)) {
            throw new IllegalStateException("Invalid session type.");
        }
        AccessToken accessToken = (AccessToken) tokens.stream().filter(token->token instanceof AccessToken).findFirst().get();
        HttpBearerAuth httpBearerAuth = new HttpBearerAuth("bearer");
        httpBearerAuth.setBearerToken(accessToken.token());
        Map<String, Authentication> authMap = new HashMap<>();
        authMap.put("kindeBearerAuth",httpBearerAuth);
        ApiClient apiClient = new ApiClient(authMap);
        apiClient.setBasePath(kindeClient.kindeConfig().domain());
        return apiClient;
    }
}
