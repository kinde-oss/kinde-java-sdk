package com.kinde.token;

import com.kinde.KindeClientSession;
import com.kinde.accounts.KindeAccountsClient;
import org.openapitools.client.model.FeatureFlagResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Utility class for checking permissions, roles, and feature flags with API fallback.
 * This implements the "hard check" functionality that falls back to the API when
 * information is not available in the token.
 */
public class KindeTokenChecker {
    
    private static final Logger log = LoggerFactory.getLogger(KindeTokenChecker.class);
    
    private final KindeToken token;
    private final KindeAccountsClient accountsClient;
    
    /**
     * Creates a new KindeTokenChecker with the provided token and session.
     * 
     * @param token The KindeToken to check
     * @param session The KindeClientSession for API fallback
     */
    public KindeTokenChecker(KindeToken token, KindeClientSession session) {
        this.token = token;
        this.accountsClient = new KindeAccountsClient(session);
    }
    
    /**
     * Checks if the user has a specific permission, falling back to API if not in token.
     * 
     * @param permissionKey The permission key to check
     * @return A CompletableFuture containing true if the user has the permission, false otherwise
     */
    public CompletableFuture<Boolean> hasPermission(String permissionKey) {
        if (permissionKey == null || permissionKey.isBlank()) {
            log.warn("Permission key was null/blank");
            return CompletableFuture.completedFuture(false);
        }
        List<String> tokenPermissions = token.getPermissions();
        if (tokenPermissions != null && !tokenPermissions.isEmpty()) {
            boolean has = tokenPermissions.contains(permissionKey);
            log.debug("Permission '{}' found in token: {}", permissionKey, has);
            return CompletableFuture.completedFuture(has);
        }
        log.debug("No permissions in token, falling back to API for permission: {}", permissionKey);
        return accountsClient.hasPermission(permissionKey)
            .exceptionally(e -> {
                log.error("Error checking permission '{}'", permissionKey, e);
                return false;
            });
    }
    
    /**
     * Checks if the user has any of the specified permissions, falling back to API if not in token.
     * 
     * @param permissionKeys The permission keys to check
     * @return A CompletableFuture containing true if the user has any of the permissions, false otherwise
     */
    public CompletableFuture<Boolean> hasAnyPermission(List<String> permissionKeys) {
        if (permissionKeys == null || permissionKeys.isEmpty()) {
            log.debug("No permission keys provided for hasAnyPermission; returning false");
            return CompletableFuture.completedFuture(false);
        }
        List<String> tokenPermissions = token.getPermissions();
        if (tokenPermissions != null && !tokenPermissions.isEmpty()) {
            boolean any = permissionKeys.stream().anyMatch(tokenPermissions::contains);
            log.debug("Any permission check in token: {}", any);
            return CompletableFuture.completedFuture(any);
        }
        log.debug("No permissions in token, falling back to API for any permission check");
        return accountsClient.hasAnyPermission(permissionKeys)
            .exceptionally(e -> {
                log.error("Error checking any permission", e);
                return false;
            });
    }
    
    /**
     * Checks if the user has all of the specified permissions, falling back to API if not in token.
     * 
     * @param permissionKeys The permission keys to check
     * @return A CompletableFuture containing true if the user has all of the permissions, false otherwise
     */
    public CompletableFuture<Boolean> hasAllPermissions(List<String> permissionKeys) {
        if (permissionKeys == null || permissionKeys.isEmpty()) {
            log.debug("No permission keys provided for hasAllPermissions; returning true");
            return CompletableFuture.completedFuture(true);
        }
        List<String> tokenPermissions = token.getPermissions();
        if (tokenPermissions != null && !tokenPermissions.isEmpty()) {
            boolean all = permissionKeys.stream().allMatch(tokenPermissions::contains);
            log.debug("All permissions check in token: {}", all);
            return CompletableFuture.completedFuture(all);
        }
        log.debug("No permissions in token, falling back to API for all permissions check");
        return accountsClient.hasAllPermissions(permissionKeys)
            .exceptionally(e -> {
                log.error("Error checking all permissions", e);
                return false;
            });
    }
    
    /**
     * Checks if the user has a specific role, falling back to API if not in token.
     * 
     * @param roleKey The role key to check
     * @return A CompletableFuture containing true if the user has the role, false otherwise
     */
    public CompletableFuture<Boolean> hasRole(String roleKey) {
        if (roleKey == null || roleKey.isBlank()) {
            log.warn("Role key was null/blank");
            return CompletableFuture.completedFuture(false);
        }
        List<String> tokenRoles = getTokenRoles();
        if (!tokenRoles.isEmpty()) {
            boolean has = tokenRoles.contains(roleKey);
            log.debug("Role '{}' found in token: {}", roleKey, has);
            return CompletableFuture.completedFuture(has);
        }
        log.debug("No roles in token, falling back to API for role: {}", roleKey);
        return accountsClient.hasRole(roleKey)
            .exceptionally(e -> {
                log.error("Error checking role '{}'", roleKey, e);
                return false;
            });
    }
    
    /**
     * Checks if the user has any of the specified roles, falling back to API if not in token.
     * 
     * @param roleKeys The role keys to check
     * @return A CompletableFuture containing true if the user has any of the roles, false otherwise
     */
    public CompletableFuture<Boolean> hasAnyRole(List<String> roleKeys) {
        if (roleKeys == null || roleKeys.isEmpty()) {
            log.debug("No role keys provided for hasAnyRole; returning false");
            return CompletableFuture.completedFuture(false);
        }
        List<String> tokenRoles = getTokenRoles();
        if (!tokenRoles.isEmpty()) {
            boolean any = roleKeys.stream().anyMatch(tokenRoles::contains);
            log.debug("Any role check in token: {}", any);
            return CompletableFuture.completedFuture(any);
        }
        log.debug("No roles in token, falling back to API for any role check");
        return accountsClient.hasAnyRole(roleKeys)
            .exceptionally(e -> {
                log.error("Error checking any role", e);
                return false;
            });
    }
    
    /**
     * Checks if the user has all of the specified roles, falling back to API if not in token.
     * 
     * @param roleKeys The role keys to check
     * @return A CompletableFuture containing true if the user has all of the roles, false otherwise
     */
    public CompletableFuture<Boolean> hasAllRoles(List<String> roleKeys) {
        if (roleKeys == null || roleKeys.isEmpty()) {
            log.debug("No role keys provided for hasAllRoles; returning true");
            return CompletableFuture.completedFuture(true);
        }
        List<String> tokenRoles = getTokenRoles();
        if (!tokenRoles.isEmpty()) {
            boolean all = roleKeys.stream().allMatch(tokenRoles::contains);
            log.debug("All roles check in token: {}", all);
            return CompletableFuture.completedFuture(all);
        }
        log.debug("No roles in token, falling back to API for all roles check");
        return accountsClient.hasAllRoles(roleKeys)
            .exceptionally(e -> {
                log.error("Error checking all roles", e);
                return false;
            });
    }

    /**
     * Gets roles from the token using the typed accessor.
     * This method is now deprecated in favor of using token.getRoles() directly.
     * 
     * @return List of role strings, or empty list if no roles are found
     * @deprecated Use token.getRoles() instead
     */
    @Deprecated
    private List<String> getTokenRoles() {
        Object roles = token.getClaim("roles");
        if (roles instanceof java.util.List) {
            return (java.util.List<String>) roles;
        }
        Object hasuraAllowedRoles = token.getClaim("x-hasura-allowed-roles");
        if (hasuraAllowedRoles instanceof java.util.List) {
            return (java.util.List<String>) hasuraAllowedRoles;
        }
        Object hasuraRoles = token.getClaim("x-hasura-roles");
        if (hasuraRoles instanceof java.util.List) {
            return (java.util.List<String>) hasuraRoles;
        }
        return java.util.Collections.emptyList();
    }
    
    /**
     * Checks if a feature flag is enabled, falling back to API if not in token.
     * 
     * @param flagKey The feature flag key to check
     * @return A CompletableFuture containing true if the feature flag is enabled, false otherwise
     */
    public CompletableFuture<Boolean> isFeatureFlagEnabled(String flagKey) {
        if (flagKey == null || flagKey.isBlank()) {
            log.warn("Feature flag key was null/blank");
            return CompletableFuture.completedFuture(false);
        }
        // First, try to get feature flag from token
        Boolean tokenFlag = token.getBooleanFlag(flagKey);
        
        if (tokenFlag != null) {
            log.debug("Feature flag '{}' found in token: {}", flagKey, tokenFlag);
            return CompletableFuture.completedFuture(tokenFlag);
        }
        // Fall back to API call
        log.debug("Feature flag '{}' not in token, falling back to API", flagKey);
        return accountsClient.getFeatureFlag(flagKey)
                .thenApply(response -> {
                    if (response.getData() != null) {
                        return Boolean.TRUE.equals(response.getData().getValue());
                    }
                    return false;
                })
                .exceptionally(e -> {
                    log.error("Error checking feature flag '{}'", flagKey, e);
                    return false;
                });
    }
    
    /**
     * Gets the value of a feature flag, falling back to API if not in token.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return A CompletableFuture containing the feature flag value, or null if not found
     */
    public CompletableFuture<Object> getFeatureFlagValue(String flagKey) {
        if (flagKey == null || flagKey.isBlank()) {
            log.warn("Feature flag key was null/blank");
            return CompletableFuture.completedFuture(null);
        }
        // First, try to get feature flag from token
        Map<String, Object> tokenFlags = token.getFlags();
        
        if (tokenFlags != null && tokenFlags.containsKey(flagKey)) {
            Object value = tokenFlags.get(flagKey);
            log.debug("Feature flag '{}' found in token: {}", flagKey, value);
            return CompletableFuture.completedFuture(value);
        }
        // Fall back to API call
        log.debug("Feature flag '{}' not in token, falling back to API", flagKey);
        return accountsClient.getFeatureFlag(flagKey)
                .thenApply(response -> {
                    if (response.getData() != null) {
                        return (Object) response.getData().getValue();
                    }
                    return null;
                })
                .exceptionally(e -> {
                    log.error("Error getting feature flag '{}'", flagKey, e);
                    return null;
                });
    }
    
    /**
     * Comprehensive check that combines multiple permission, role, and feature flag checks.
     * All checks must pass for the method to return true.
     * 
     * @param permissions List of permissions to check (optional)
     * @param roles List of roles to check (optional)
     * @param featureFlags List of feature flags to check (optional)
     * @return A CompletableFuture containing true if all checks pass, false otherwise
     */
    public CompletableFuture<Boolean> hasAll(List<String> permissions, List<String> roles, List<String> featureFlags) {
        CompletableFuture<Boolean> pFut = (permissions == null || permissions.isEmpty())
            ? CompletableFuture.completedFuture(true)
            : hasAllPermissions(permissions);

        CompletableFuture<Boolean> rFut = (roles == null || roles.isEmpty())
            ? CompletableFuture.completedFuture(true)
            : hasAllRoles(roles);

        CompletableFuture<Boolean> fFut;
        if (featureFlags == null || featureFlags.isEmpty()) {
            fFut = CompletableFuture.completedFuture(true);
        } else {
            List<CompletableFuture<Boolean>> futures = featureFlags.stream()
                .map(this::isFeatureFlagEnabled)
                .toList();
            fFut = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().allMatch(f -> f.getNow(false)));
        }

        return pFut.thenCombine(rFut, (p, r) -> p && r)
                   .thenCombine(fFut, (pr, f) -> pr && f)
                   .exceptionally(e -> {
                       log.error("Error in comprehensive check (hasAll)", e);
                       return false;
                   });
    }
    
    /**
     * Comprehensive check that combines multiple permission, role, and feature flag checks.
     * At least one check from each category must pass for the method to return true.
     * 
     * @param permissions List of permissions to check (optional)
     * @param roles List of roles to check (optional)
     * @param featureFlags List of feature flags to check (optional)
     * @return A CompletableFuture containing true if at least one check from each category passes, false otherwise
     */
        public CompletableFuture<Boolean> hasAny(List<String> permissions, List<String> roles, List<String> featureFlags) {
        CompletableFuture<Boolean> pFut = (permissions == null || permissions.isEmpty())
            ? CompletableFuture.completedFuture(true)
            : hasAnyPermission(permissions);

        CompletableFuture<Boolean> rFut = (roles == null || roles.isEmpty())
            ? CompletableFuture.completedFuture(true)
            : hasAnyRole(roles);

        CompletableFuture<Boolean> fFut;
        if (featureFlags == null || featureFlags.isEmpty()) {
            fFut = CompletableFuture.completedFuture(true);
        } else {
            List<CompletableFuture<Boolean>> futures = featureFlags.stream()
                .map(this::isFeatureFlagEnabled)
                .toList();
            fFut = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().anyMatch(f -> f.getNow(false)));
        }

        return pFut.thenCombine(rFut, (p, r) -> p && r)
                   .thenCombine(fFut, (pr, f) -> pr && f)
                   .exceptionally(e -> {
                       log.error("Error in comprehensive check (hasAny)", e);
                       return false;
                   });
    }
}
