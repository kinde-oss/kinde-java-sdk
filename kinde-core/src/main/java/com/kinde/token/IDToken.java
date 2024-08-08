package com.kinde.token;

public class IDToken implements KindeToken {

    private String token;

    private IDToken(String token) {
        this.token = token;
    }

    @Override
    public boolean valid() {
        return true;
    }

    public static KindeToken init(String token) {
        return new IDToken(token);
    }

}
