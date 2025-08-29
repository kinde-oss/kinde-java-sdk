package com.kinde.token;

import com.google.inject.Inject;
import com.kinde.accounts.KindeAccountsClient;
import com.kinde.accounts.dto.PermissionDto;
import com.kinde.accounts.dto.RoleDto;
import com.kinde.accounts.dto.FeatureFlagDto;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class BaseToken implements KindeToken {

    private static final Logger log = LoggerFactory.getLogger(BaseToken.class);

    private final String token;
    private final boolean valid;
    private final SignedJWT signedJWT;
    private final KindeAccountsClient accountsClient;

    @SneakyThrows
    protected BaseToken(String token, boolean valid) {
        this.token = token;
        this.valid = valid;
        this.accountsClient = null; // No API fallback in this constructor
        SignedJWT parsedJwt = JwtValidator.isJwt(this.token);
        if (parsedJwt != null) {
            signedJWT = parsedJwt;
        } else {
            signedJWT = null;
        }
    }

    /**
     * Constructor with KindeAccountsClient for hard check functionality.
     * 
     * @param token The JWT token string
     * @param valid Whether the token is valid
     * @param accountsClient The KindeAccountsClient for API fallback
     */
    @SneakyThrows
    protected BaseToken(String token, boolean valid, KindeAccountsClient accountsClient) {
        this.token = token;
        this.valid = valid;
        this.accountsClient = accountsClient;
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
        if (roles == null) {
            roles = (List<String>) getClaim("x-hasura-roles");
        }
        return roles != null ? roles : Collections.emptyList();
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
            return Collections.emptyMap();
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

    // ========== Hard Check Methods Implementation ==========

    @Override
    public boolean hasPermission(String permissionKey) {
        if (permissionKey == null || permissionKey.isBlank()) {
            log.warn("Permission key was null/blank");
            return false;
        }
        
        List<String> tokenPermissions = getTokenPermissionsSafe();
        if (!tokenPermissions.isEmpty()) {
            boolean has = tokenPermissions.contains(permissionKey);
            log.debug("Permission '{}' found in token: {}", permissionKey, has);
            if (has) {
                return true;
            }
            // Permission not found in token, fall back to API if available
        }
        
        if (accountsClient == null) {
            log.debug("Permission '{}' not found in token and no accounts client available for API fallback", permissionKey);
            return false;
        }
        
        log.debug("Permission '{}' not found in token, falling back to API", permissionKey);
        try {
            List<PermissionDto> permissions = accountsClient.getPermissions();
            return permissions.stream()
                .anyMatch(p -> permissionKey.equals(p.getKey()) || permissionKey.equals(p.getName()));
        } catch (Exception e) {
            log.error("Error checking permission '{}'", permissionKey, e);
            return false;
        }
    }

    @Override
    public boolean hasAnyPermission(List<String> permissionKeys) {
        if (permissionKeys == null || permissionKeys.isEmpty()) {
            log.debug("No permission keys provided for hasAnyPermission; returning false");
            return false;
        }
        
        List<String> tokenPermissions = getTokenPermissionsSafe();
        if (!tokenPermissions.isEmpty()) {
            boolean any = permissionKeys.stream().anyMatch(tokenPermissions::contains);
            log.debug("Any permission check in token: {}", any);
            if (any) {
                return true;
            }
            // No permissions found in token, fall back to API if available
        }
        
        if (accountsClient == null) {
            log.debug("No permissions found in token and no accounts client available for API fallback");
            return false;
        }
        
        log.debug("No permissions found in token, falling back to API for any permission check");
        try {
            List<PermissionDto> permissions = accountsClient.getPermissions();
            return permissions.stream()
                .anyMatch(p -> permissionKeys.contains(p.getKey()) || permissionKeys.contains(p.getName()));
        } catch (Exception e) {
            log.error("Error checking any permission", e);
            return false;
        }
    }

    @Override
    public boolean hasAllPermissions(List<String> permissionKeys) {
        if (permissionKeys == null || permissionKeys.isEmpty()) {
            log.debug("No permission keys provided for hasAllPermissions; returning true");
            return true;
        }
        
        List<String> tokenPermissions = getTokenPermissionsSafe();
        if (!tokenPermissions.isEmpty()) {
            boolean all = permissionKeys.stream().allMatch(tokenPermissions::contains);
            log.debug("All permissions check in token: {}", all);
            if (all) {
                return true;
            }
            // Not all permissions found in token, fall back to API if available
        }
        
        if (accountsClient == null) {
            log.debug("Not all permissions found in token and no accounts client available for API fallback");
            return false;
        }
        
        log.debug("Not all permissions found in token, falling back to API for all permissions check");
        try {
            List<PermissionDto> permissions = accountsClient.getPermissions();
            List<String> userPermissions = permissions.stream()
                .flatMap(p -> Stream.of(p.getKey(), p.getName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            return permissionKeys.stream().allMatch(userPermissions::contains);
        } catch (Exception e) {
            log.error("Error checking all permissions", e);
            return false;
        }
    }

    @Override
    public boolean hasRole(String roleKey) {
        if (roleKey == null || roleKey.isBlank()) {
            log.warn("Role key was null/blank");
            return false;
        }
        
        try {
            List<String> tokenRoles = getTokenRoles();
            if (!tokenRoles.isEmpty()) {
                boolean has = tokenRoles.contains(roleKey);
                log.debug("Role '{}' found in token: {}", roleKey, has);
                if (has) {
                    return true;
                }
                // Role not found in token, fall back to API if available
            }
        } catch (Exception e) {
            log.debug("Error getting roles from token, falling back to API: {}", e.getMessage());
        }
        
        if (accountsClient == null) {
            log.debug("Role '{}' not found in token and no accounts client available for API fallback", roleKey);
            return false;
        }
        
        log.debug("Role '{}' not found in token, falling back to API", roleKey);
        try {
            List<RoleDto> roles = accountsClient.getRoles();
            return roles.stream()
                .anyMatch(r -> roleKey.equals(r.getKey()) || roleKey.equals(r.getName()));
        } catch (Exception e) {
            log.error("Error checking role '{}'", roleKey, e);
            return false;
        }
    }

    @Override
    public boolean hasAnyRole(List<String> roleKeys) {
        if (roleKeys == null || roleKeys.isEmpty()) {
            log.debug("No role keys provided for hasAnyRole; returning false");
            return false;
        }
        
        try {
            List<String> tokenRoles = getTokenRoles();
            if (!tokenRoles.isEmpty()) {
                boolean any = roleKeys.stream().anyMatch(tokenRoles::contains);
                log.debug("Any role check in token: {}", any);
                if (any) {
                    return true;
                }
                // No roles found in token, fall back to API if available
            }
        } catch (Exception e) {
            log.debug("Error getting roles from token, falling back to API: {}", e.getMessage());
        }
        
        if (accountsClient == null) {
            log.debug("No roles found in token and no accounts client available for API fallback");
            return false;
        }
        
        log.debug("No roles found in token, falling back to API for any role check");
        try {
            List<RoleDto> roles = accountsClient.getRoles();
            return roles.stream()
                .anyMatch(r -> roleKeys.contains(r.getKey()) || roleKeys.contains(r.getName()));
        } catch (Exception e) {
            log.error("Error checking any role", e);
            return false;
        }
    }

    @Override
    public boolean hasAllRoles(List<String> roleKeys) {
        if (roleKeys == null || roleKeys.isEmpty()) {
            log.debug("No role keys provided for hasAllRoles; returning true");
            return true;
        }
        
        try {
            List<String> tokenRoles = getTokenRoles();
            if (!tokenRoles.isEmpty()) {
                boolean all = roleKeys.stream().allMatch(tokenRoles::contains);
                log.debug("All roles check in token: {}", all);
                if (all) {
                    return true;
                }
                // Not all roles found in token, fall back to API if available
            }
        } catch (Exception e) {
            log.debug("Error getting roles from token, falling back to API: {}", e.getMessage());
        }
        
        if (accountsClient == null) {
            log.debug("Not all roles found in token and no accounts client available for API fallback");
            return false;
        }
        
        log.debug("Not all roles found in token, falling back to API for all roles check");
        try {
            List<RoleDto> roles = accountsClient.getRoles();
            List<String> userRoles = roles.stream()
                .flatMap(r -> Stream.of(r.getKey(), r.getName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            return roleKeys.stream().allMatch(userRoles::contains);
        } catch (Exception e) {
            log.error("Error checking all roles", e);
            return false;
        }
    }

    @Override
    public boolean isFeatureFlagEnabled(String flagKey) {
        if (flagKey == null || flagKey.isBlank()) {
            log.warn("Feature flag key was null/blank");
            return false;
        }
        
        try {
            // First, try to get feature flag from token
            Boolean tokenFlag = getBooleanFlag(flagKey);
            
            if (tokenFlag != null) {
                log.debug("Feature flag '{}' found in token: {}", flagKey, tokenFlag);
                return tokenFlag;
            }
        } catch (Exception e) {
            log.debug("Error getting feature flag from token, falling back to API: {}", e.getMessage());
        }
        
        if (accountsClient == null) {
            log.debug("Feature flag '{}' not in token and no accounts client available for API fallback", flagKey);
            return false;
        }
        
        // Fall back to API call
        log.debug("Feature flag '{}' not in token, falling back to API", flagKey);
        try {
            FeatureFlagDto response = accountsClient.getFeatureFlag(flagKey);
            if (response != null && response.getValue() != null) {
                return Boolean.TRUE.equals(Boolean.valueOf(response.getValue()));
            }
            return false;
        } catch (Exception e) {
            log.error("Error checking feature flag '{}'", flagKey, e);
            return false;
        }
    }

    @Override
    public Object getFeatureFlagValue(String flagKey) {
        if (flagKey == null || flagKey.isBlank()) {
            log.warn("Feature flag key was null/blank");
            return null;
        }
        
        try {
            // First, try to get feature flag from token
            Map<String, Object> tokenFlags = getFlags();
            
            if (tokenFlags != null && tokenFlags.containsKey(flagKey)) {
                Object value = tokenFlags.get(flagKey);
                log.debug("Feature flag '{}' found in token: {}", flagKey, value);
                return value;
            }
        } catch (Exception e) {
            log.debug("Error getting feature flags from token, falling back to API: {}", e.getMessage());
        }
        
        if (accountsClient == null) {
            log.debug("Feature flag '{}' not in token and no accounts client available for API fallback", flagKey);
            return null;
        }
        
        // Fall back to API call
        log.debug("Feature flag '{}' not in token, falling back to API", flagKey);
        try {
            FeatureFlagDto response = accountsClient.getFeatureFlag(flagKey);
            if (response != null && response.getValue() != null) {
                return (Object) response.getValue();
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting feature flag '{}'", flagKey, e);
            return null;
        }
    }

    @Override
    public boolean hasAll(List<String> permissions, List<String> roles, List<String> featureFlags) {
        try {
            // Check permissions
            boolean permissionsOk = (permissions == null || permissions.isEmpty()) || hasAllPermissions(permissions);
            if (!permissionsOk) {
                return false;
            }
            
            // Check roles
            boolean rolesOk = (roles == null || roles.isEmpty()) || hasAllRoles(roles);
            if (!rolesOk) {
                return false;
            }
            
            // Check feature flags
            if (featureFlags != null && !featureFlags.isEmpty()) {
                for (String flag : featureFlags) {
                    if (!isFeatureFlagEnabled(flag)) {
                        return false;
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error in comprehensive check (hasAll)", e);
            return false;
        }
    }

    @Override
    public boolean hasAny(List<String> permissions, List<String> roles, List<String> featureFlags) {
        try {
            // Check permissions
            boolean permissionsOk = (permissions == null || permissions.isEmpty()) || hasAnyPermission(permissions);
            if (!permissionsOk) {
                return false;
            }
            
            // Check roles
            boolean rolesOk = (roles == null || roles.isEmpty()) || hasAnyRole(roles);
            if (!rolesOk) {
                return false;
            }
            
            // Check feature flags
            if (featureFlags != null && !featureFlags.isEmpty()) {
                boolean anyFlagEnabled = false;
                for (String flag : featureFlags) {
                    if (isFeatureFlagEnabled(flag)) {
                        anyFlagEnabled = true;
                        break;
                    }
                }
                if (!anyFlagEnabled) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error in comprehensive check (hasAny)", e);
            return false;
        }
    }

    // ========== Helper Methods ==========

    /**
     * Gets roles from the token using the typed accessor.
     * This method is now deprecated in favor of using token.getRoles() directly.
     * 
     * @return List of role strings, or empty list if no roles are found
     * @deprecated Use token.getRoles() instead
     */
    private List<String> getTokenRoles() {
        List<String> roles = (token != null) ? getRoles() : null;
        return roles != null ? roles : Collections.emptyList();
    }

    private List<String> getTokenPermissionsSafe() {
        try {
            List<String> perms = (token != null) ? getPermissions() : null;
            return perms != null ? perms : Collections.emptyList();
        } catch (Exception e) {
            log.debug("Error getting permissions from token, treating as empty: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
