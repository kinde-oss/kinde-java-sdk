package com.kinde.token;

import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.util.List;

public class BaseToken implements KindeToken {

    private String token;
    private boolean valid;
    private SignedJWT signedJWT;

    @SneakyThrows
    protected BaseToken(String token, boolean valid) {
        this.token = token;
        this.valid = valid;
        signedJWT = SignedJWT.parse(this.token);
    }

    @Override
    public boolean valid() {
        return valid;
    }

    public String token() {
        return this.token;
    }

    @Override
    @SneakyThrows
    public String getUser() {
        return this.signedJWT.getJWTClaimsSet().getSubject();
    }

    @Override
    public List<String> getOrganisations() {
        return (List<String>)this.getClaim("org_codes");
    }

    @SneakyThrows
    public Object getClaim(String key) {
        return this.signedJWT.getJWTClaimsSet().getClaim(key);
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
