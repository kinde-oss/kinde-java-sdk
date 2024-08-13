package com.kinde.servlet;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.authorization.AuthorizationType;

public class KindeSingleton {

    private static KindeSingleton instance;
    private KindeClient kindeClient;

    private KindeSingleton() {
        this.kindeClient = KindeClientBuilder
                .builder()
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
