package com.kinde.token;

import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;

import static com.kinde.token.JwtValidator.isJwt;

public class BaseToken implements KindeToken {

    private final String token;
    private final boolean valid;
    private final SignedJWT signedJWT;

    @SneakyThrows
    protected BaseToken(String token, boolean valid) {
        this.token = token;
        this.valid = valid;
        if (isJwt(this.token)) {
            signedJWT = SignedJWT.parse(this.token);
        } else {
            signedJWT = null;
        }
    }

    @Override
    public boolean valid() {
        return valid;
    }

    @Override
    public String token() {
        return this.token;
    }

    @Override
    @SneakyThrows
    public String getUser() {
        return this.signedJWT.getJWTClaimsSet().getSubject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getOrganisations() {
        return (List<String>)this.getClaim("org_codes");
    }

    @SneakyThrows
    @Override
    public Object getClaim(String key) {
        return this.signedJWT.getJWTClaimsSet().getClaim(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getPermissions() {
        return (List<String>) getClaim("permissions");
    }

    @Override
    public String getStringFlag(String key) {
        return (String)getFlagClaims().get(key);
    }

    @Override
    public Integer getIntegerFlag(String key) {
        return getFlagClaims().get(key) != null ? ((Long)getFlagClaims().get(key)).intValue() : null;
    }

    @Override
    public Boolean getBooleanFlag(String key) {
        return (Boolean) getFlagClaims().get(key);
    }

    @SuppressWarnings("unchecked")
    private Map<String,Object> getFlagClaims() {
        return ((Map<String,Object>)getClaim("feature_flags"));
    }

    @SuppressWarnings("unchecked")
    public Map<String,Object> getFlags() {
        return (Map<String,Object>) getClaim("feature_flags");
    }
}
