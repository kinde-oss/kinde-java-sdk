package com.kinde.token;

public class RefreshToken implements KindeToken {

    private String token;

    private RefreshToken(String token) {
        this.token = token;
    }

    @Override
    public boolean valid() {
        return true;
    }

    public static KindeToken init(String token) {
        return new RefreshToken(token);
    }

}
