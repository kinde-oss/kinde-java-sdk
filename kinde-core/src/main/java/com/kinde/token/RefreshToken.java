package com.kinde.token;

public class RefreshToken extends BaseToken {

    private RefreshToken(String token, boolean valid) {
        super(token,valid);
    }

    public static RefreshToken init(String token,boolean valid) {
        return new RefreshToken(token,valid);
    }
}
