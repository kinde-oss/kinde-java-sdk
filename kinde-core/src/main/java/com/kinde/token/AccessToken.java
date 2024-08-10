package com.kinde.token;

import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.util.List;

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

    @SneakyThrows
    public Object getClaim(String key) {
        SignedJWT signedJWT = SignedJWT.parse(this.token);
        return signedJWT.getJWTClaimsSet().getClaim(key);
    }

    public List<String> getPermissions() {
        return (List<String>) getClaim("permissions");
    }
}
