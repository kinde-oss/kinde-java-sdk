package com.kinde;

import com.kinde.admin.KindeAdminSessionImpl;

public class KindeAdminSessionBuilder {

    private KindeClient kindeClient;

    private KindeAdminSessionBuilder() {

    }

    public static KindeAdminSessionBuilder builder() {
        return new KindeAdminSessionBuilder();
    }

    public KindeAdminSessionBuilder client(KindeClient kindeClient) {
        this.kindeClient = kindeClient;
        return this;
    }

    public KindeAdminSession build() {
        return new KindeAdminSessionImpl(kindeClient);
    }

}
