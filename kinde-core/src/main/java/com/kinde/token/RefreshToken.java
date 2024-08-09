package com.kinde.token;

public class RefreshToken implements KindeToken {

    private String token;
    private boolean valid;

    private RefreshToken(String token, boolean valid) {
        this.token = token;
        this.valid = valid;
    }

    @Override
    public boolean valid() {
        return this.valid;
    }

    public String token() {
        return this.token;
    }

    public static KindeToken init(String token,boolean valid) {
        return new RefreshToken(token,valid);
    }

}
