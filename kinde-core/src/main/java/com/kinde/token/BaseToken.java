package com.kinde.token;

import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.util.List;

public class BaseToken implements KindeToken {

    private String token;
    private boolean valid;

    protected BaseToken(String token, boolean valid) {
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

    @Override
    public String getUser() {
        return "";
    }

    @Override
    public String getOrganisation() {
        return "";
    }

    @SneakyThrows
    public Object getClaim(String key) {
        SignedJWT signedJWT = SignedJWT.parse(this.token);
        return signedJWT.getJWTClaimsSet().getClaim(key);
    }

    public List<String> getPermissions() {
        return (List<String>) getClaim("permissions");
    }

    @Override
    public String getStringFlag(String key) {
        return "";
    }

    @Override
    public Integer getIntegerFlag(String key) {
        return 0;
    }

    @Override
    public Boolean getBooleanFlag(String key) {
        return null;
    }

    List<String> getFlags() {
        return (List<String>) getClaim("feature_flags");
    }
}
