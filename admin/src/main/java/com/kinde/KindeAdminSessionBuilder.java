package com.kinde;

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


}
