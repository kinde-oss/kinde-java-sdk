package com.kinde.servlet;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.authorization.AuthorizationType;

public class KindeSingleton {

    private static KindeSingleton instance;
    private KindeClientBuilder kindeClientBuilder;

    private KindeSingleton() {
        this.kindeClientBuilder = KindeClientBuilder
                .builder()
                .grantType(AuthorizationType.CODE)
                .addScope("openid");
    }

    public static synchronized KindeSingleton getInstance() {
        if (instance == null) {
            instance = new KindeSingleton();
        }
        return instance;
    }

    public KindeClient getKindeClient() {
        return this.kindeClientBuilder.build();
    }
    public KindeClientBuilder getKindeClientBuilder() {
        return this.kindeClientBuilder;
    }
}
