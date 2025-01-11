package com.kinde.admin;

import com.kinde.KindeAdminSession;
import com.kinde.KindeClient;
import com.kinde.token.KindeTokens;
import okhttp3.OkHttpClient;
import org.openapitools.client.ApiClient;


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
        KindeTokens kindeTokens = kindeClient.clientSession().retrieveTokens();
        if (kindeTokens.getAccessToken() == null) {
            throw new IllegalStateException("Invalid session type.");
        }
        OkHttpClient client = new OkHttpClient.Builder().build();
        ApiClient apiClient = new ApiClient(client);
        apiClient.setBearerToken(kindeTokens.getAccessToken().token());
        apiClient.setBasePath(kindeClient.kindeConfig().domain());
        return apiClient;
    }
}
