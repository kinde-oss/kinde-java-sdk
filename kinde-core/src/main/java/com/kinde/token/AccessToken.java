package com.kinde.token;

public class AccessToken extends BaseToken {

    private AccessToken(String token,boolean valid) {
        super(token,valid);
    }

    public static AccessToken init(String token,boolean valid) {
        return new AccessToken(token,valid);
    }
}
