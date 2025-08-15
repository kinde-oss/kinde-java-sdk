package com.kinde.token;

import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;


public class BaseToken implements KindeToken {

    private final String token;
    private final boolean valid;
    private final SignedJWT signedJWT;

    @SneakyThrows
    protected BaseToken(String token, boolean valid) {
        this.token = token;
        this.valid = valid;
        SignedJWT parsedJwt = JwtValidator.isJwt(this.token);
        if (parsedJwt != null) {
            signedJWT = parsedJwt;
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
        if (this.signedJWT == null) {
            return null; // Return null for invalid tokens instead of throwing NPE
        }
        return this.signedJWT.getJWTClaimsSet().getClaim(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getPermissions() {
        // Prefer standard claim; fallback to Hasura-compatible claim if missing
        List<String> permissions = (List<String>) getClaim("permissions");
        if (permissions != null) {
            return permissions;
        }
        return (List<String>) getClaim("x-hasura-permissions");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getRoles() {
        // Prefer standard claim; fallback to Hasura-compatible claim if missing
        List<String> roles = (List<String>) getClaim("roles");
        if (roles != null) {
            return roles;
        }
        return (List<String>) getClaim("x-hasura-roles");
    }

    @Override
    public String getStringFlag(String key) {
        Object value = getFlagValue(key);
        return value instanceof String ? (String) value : null;
    }

    @Override
    public Integer getIntegerFlag(String key) {
        Object value = getFlagValue(key);
        if (value instanceof Number) {
            long lv = ((Number) value).longValue();
            if (lv > Integer.MAX_VALUE || lv < Integer.MIN_VALUE) {
                return null; // avoid silent overflow
            }
            return ((Number) value).intValue();
        }
        return null;
    }

    @Override
    public Boolean getBooleanFlag(String key) {
        Object value = getFlagValue(key);
        return value instanceof Boolean ? (Boolean) value : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getFlagClaims() {
        // Prefer standard claim; fallback to Hasura-compatible claim if missing
        Map<String, Object> flags = (Map<String, Object>) getClaim("feature_flags");
        if (flags != null) {
            return flags;
        }
        return (Map<String, Object>) getClaim("x-hasura-feature-flags");
    }

    /**
     * Extracts the concrete value for a feature flag key from the token claims.
     * The token stores flags as an object map where each key maps to a structure
     * containing fields like 'v' (value) and 't' (type). This method returns the
     * 'v' field when present; otherwise returns the raw entry.
     */
    @SuppressWarnings("unchecked")
    private Object getFlagValue(String key) {
        Map<String, Object> claims = getFlagClaims();
        if (claims == null) {
            return null;
        }
        Object entry = claims.get(key);
        if (entry instanceof Map) {
            Object v = ((Map<String, Object>) entry).get("v");
            return v;
        }
        return entry;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getFlags() {
        Map<String, Object> claims = getFlagClaims();
        if (claims == null) {
            return null;
        }
        // Convert from { key -> { v: value, t: type } } to { key -> value }
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, Object> e : claims.entrySet()) {
            Object value = e.getValue();
            if (value instanceof Map) {
                Object v = ((Map<String, Object>) value).get("v");
                result.put(e.getKey(), v);
            } else {
                result.put(e.getKey(), value);
            }
        }
        return result;
    }
}
