package com.kinde.token;

public class AccessToken implements KindeToken {

    private String token;

    private AccessToken(String token) {
        this.token = token;
    }

    @Override
    public boolean valid() {
        return true;
    }

    public static KindeToken init(String token) {
        return new AccessToken(token);
    }
}
