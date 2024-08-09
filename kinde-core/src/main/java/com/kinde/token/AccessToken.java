package com.kinde.token;

public class AccessToken implements KindeToken {

    private String token;
    private boolean valid;

    private AccessToken(String token,boolean valid) {
        this.token = token;
        this.valid = valid;
    }

    @Override
    public boolean valid() {
        return valid;
    }

    public String token() {
        return this.token;
    }

    public static KindeToken init(String token,boolean valid) {
        return new AccessToken(token,valid);
    }
}
