package com.kinde.token;

import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.util.Set;

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

    @SneakyThrows
    public Object getClaim(String key) {
        SignedJWT signedJWT = SignedJWT.parse(this.token);
        return signedJWT.getJWTClaimsSet().getClaim(key);
    }

    public Set<String> getPermissions() {
        return (Set<String>) getClaim("permissions");
    }
}
