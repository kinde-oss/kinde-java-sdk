package com.kinde;

import com.kinde.authorization.AuthorizationType;

public class KindeSingleton {

    private static final String CLIENT_ID = "replace";
    private static final String CLIENT_SECRET = "replace";
    private static final String DOMAIN = "replace";
    private static final String REDIRECT_URI = "replace";


    private static KindeSingleton instance;
    private KindeClient kindeClient;

    private KindeSingleton() {
        this.kindeClient = KindeClientBuilder
                .builder()
                .domain(DOMAIN)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(AuthorizationType.CODE)
                .build();
    }

    public static synchronized KindeSingleton getInstance() {
        if (instance == null) {
            instance = new KindeSingleton();
        }
        return instance;
    }

    public KindeClient getKindeClient() {
        return this.kindeClient;
    }
}
