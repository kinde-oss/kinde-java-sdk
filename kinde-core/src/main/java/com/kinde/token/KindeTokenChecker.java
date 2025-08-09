package com.kinde.token;

import com.kinde.KindeClientSession;
import com.kinde.accounts.KindeAccountsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
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
        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, try to get permissions from token
                List<String> tokenPermissions = token.getPermissions();
                
                if (tokenPermissions != null && !tokenPermissions.isEmpty()) {
                    // Check if permission is in token
                    boolean hasPermission = tokenPermissions.contains(permissionKey);
                    log.debug("Permission '{}' found in token: {}", permissionKey, hasPermission);
                    return hasPermission;
                } else {
                    // Fall back to API call
                    log.debug("No permissions in token, falling back to API for permission: {}", permissionKey);
                    return accountsClient.hasPermission(permissionKey).join();
                }
            } catch (Exception e) {
                log.error("Error checking permission '{}': {}", permissionKey, e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Checks if the user has any of the specified permissions, falling back to API if not in token.
     * 
     * @param permissionKeys The permission keys to check
     * @return A CompletableFuture containing true if the user has any of the permissions, false otherwise
     */
    public CompletableFuture<Boolean> hasAnyPermission(List<String> permissionKeys) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, try to get permissions from token
                List<String> tokenPermissions = token.getPermissions();
                
                if (tokenPermissions != null && !tokenPermissions.isEmpty()) {
                    // Check if any permission is in token
                    boolean hasAnyPermission = permissionKeys.stream()
                            .anyMatch(tokenPermissions::contains);
                    log.debug("Any permission check in token: {}", hasAnyPermission);
                    return hasAnyPermission;
                } else {
                    // Fall back to API call
                    log.debug("No permissions in token, falling back to API for any permission check");
                    return accountsClient.hasAnyPermission(permissionKeys).join();
                }
            } catch (Exception e) {
                log.error("Error checking any permission: {}", e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Checks if the user has all of the specified permissions, falling back to API if not in token.
     * 
     * @param permissionKeys The permission keys to check
     * @return A CompletableFuture containing true if the user has all of the permissions, false otherwise
     */
    public CompletableFuture<Boolean> hasAllPermissions(List<String> permissionKeys) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, try to get permissions from token
                List<String> tokenPermissions = token.getPermissions();
                
                if (tokenPermissions != null && !tokenPermissions.isEmpty()) {
                    // Check if all permissions are in token
                    boolean hasAllPermissions = permissionKeys.stream()
                            .allMatch(tokenPermissions::contains);
                    log.debug("All permissions check in token: {}", hasAllPermissions);
                    return hasAllPermissions;
                } else {
                    // Fall back to API call
                    log.debug("No permissions in token, falling back to API for all permissions check");
                    return accountsClient.hasAllPermissions(permissionKeys).join();
                }
            } catch (Exception e) {
                log.error("Error checking all permissions: {}", e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Checks if the user has a specific role, falling back to API if not in token.
     * 
     * @param roleKey The role key to check
     * @return A CompletableFuture containing true if the user has the role, false otherwise
     */
    public CompletableFuture<Boolean> hasRole(String roleKey) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, try to get roles from token
                List<String> tokenRoles = getTokenRoles();
                
                if (tokenRoles != null && !tokenRoles.isEmpty()) {
                    // Check if role is in token
                    boolean hasRole = tokenRoles.contains(roleKey);
                    log.debug("Role '{}' found in token: {}", roleKey, hasRole);
                    return hasRole;
                } else {
                    // Fall back to API call
                    log.debug("No roles in token, falling back to API for role: {}", roleKey);
                    return accountsClient.hasRole(roleKey).join();
                }
            } catch (Exception e) {
                log.error("Error checking role '{}': {}", roleKey, e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Checks if the user has any of the specified roles, falling back to API if not in token.
     * 
     * @param roleKeys The role keys to check
     * @return A CompletableFuture containing true if the user has any of the roles, false otherwise
     */
    public CompletableFuture<Boolean> hasAnyRole(List<String> roleKeys) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, try to get roles from token
                List<String> tokenRoles = getTokenRoles();
                
                if (tokenRoles != null && !tokenRoles.isEmpty()) {
                    // Check if any role is in token
                    boolean hasAnyRole = roleKeys.stream()
                            .anyMatch(tokenRoles::contains);
                    log.debug("Any role check in token: {}", hasAnyRole);
                    return hasAnyRole;
                } else {
                    // Fall back to API call
                    log.debug("No roles in token, falling back to API for any role check");
                    return accountsClient.hasAnyRole(roleKeys).join();
                }
            } catch (Exception e) {
                log.error("Error checking any role: {}", e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Checks if the user has all of the specified roles, falling back to API if not in token.
     * 
     * @param roleKeys The role keys to check
     * @return A CompletableFuture containing true if the user has all of the roles, false otherwise
     */
    public CompletableFuture<Boolean> hasAllRoles(List<String> roleKeys) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, try to get roles from token
                List<String> tokenRoles = getTokenRoles();
                
                if (tokenRoles != null && !tokenRoles.isEmpty()) {
                    // Check if all roles are in token
                    boolean hasAllRoles = roleKeys.stream()
                            .allMatch(tokenRoles::contains);
                    log.debug("All roles check in token: {}", hasAllRoles);
                    return hasAllRoles;
                } else {
                    // Fall back to API call
                    log.debug("No roles in token, falling back to API for all roles check");
                    return accountsClient.hasAllRoles(roleKeys).join();
                }
            } catch (Exception e) {
                log.error("Error checking all roles: {}", e.getMessage());
                return false;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private List<String> getTokenRoles() {
        Object roles = token.getClaim("roles");
        if (roles instanceof java.util.List) {
            return (java.util.List<String>) roles;
        }
        Object hasuraRoles = token.getClaim("x-hasura-roles");
        if (hasuraRoles instanceof java.util.List) {
            return (java.util.List<String>) hasuraRoles;
        }
        return null;
    }
    
    /**
     * Checks if a feature flag is enabled, falling back to API if not in token.
     * 
     * @param flagKey The feature flag key to check
     * @return A CompletableFuture containing true if the feature flag is enabled, false otherwise
     */
    public CompletableFuture<Boolean> isFeatureFlagEnabled(String flagKey) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, try to get feature flag from token
                Boolean tokenFlag = token.getBooleanFlag(flagKey);
                
                if (tokenFlag != null) {
                    log.debug("Feature flag '{}' found in token: {}", flagKey, tokenFlag);
                    return tokenFlag;
                } else {
                    // Fall back to API call
                    log.debug("Feature flag '{}' not in token, falling back to API", flagKey);
                    return accountsClient.getFeatureFlag(flagKey)
                            .thenApply(response -> {
                                if (response.getData() != null) {
                                    return Boolean.TRUE.equals(response.getData().getValue());
                                }
                                return false;
                            })
                            .join();
                }
            } catch (Exception e) {
                log.error("Error checking feature flag '{}': {}", flagKey, e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Gets the value of a feature flag, falling back to API if not in token.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return A CompletableFuture containing the feature flag value, or null if not found
     */
    public CompletableFuture<Object> getFeatureFlagValue(String flagKey) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, try to get feature flag from token
                Map<String, Object> tokenFlags = token.getFlags();
                
                if (tokenFlags != null && tokenFlags.containsKey(flagKey)) {
                    Object value = tokenFlags.get(flagKey);
                    log.debug("Feature flag '{}' found in token: {}", flagKey, value);
                    return value;
                } else {
                    // Fall back to API call
                    log.debug("Feature flag '{}' not in token, falling back to API", flagKey);
                    return accountsClient.getFeatureFlag(flagKey)
                            .thenApply(response -> {
                                if (response.getData() != null) {
                                    return response.getData().getValue();
                                }
                                return null;
                            })
                            .join();
                }
            } catch (Exception e) {
                log.error("Error getting feature flag '{}': {}", flagKey, e.getMessage());
                return null;
            }
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
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean allChecksPass = true;
                
                // Check permissions if provided
                if (permissions != null && !permissions.isEmpty()) {
                    boolean permissionsCheck = hasAllPermissions(permissions).join();
                    if (!permissionsCheck) {
                        log.debug("Permission check failed for: {}", permissions);
                        allChecksPass = false;
                    }
                }
                
                // Check roles if provided
                if (roles != null && !roles.isEmpty()) {
                    boolean rolesCheck = hasAllRoles(roles).join();
                    if (!rolesCheck) {
                        log.debug("Role check failed for: {}", roles);
                        allChecksPass = false;
                    }
                }
                
                // Check feature flags if provided
                if (featureFlags != null && !featureFlags.isEmpty()) {
                    for (String flag : featureFlags) {
                        boolean flagCheck = isFeatureFlagEnabled(flag).join();
                        if (!flagCheck) {
                            log.debug("Feature flag check failed for: {}", flag);
                            allChecksPass = false;
                            break;
                        }
                    }
                }
                
                return allChecksPass;
            } catch (Exception e) {
                log.error("Error in comprehensive check: {}", e.getMessage());
                return false;
            }
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
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean permissionsCheck = true;
                boolean rolesCheck = true;
                boolean featureFlagsCheck = true;
                
                // Check permissions if provided
                if (permissions != null && !permissions.isEmpty()) {
                    permissionsCheck = hasAnyPermission(permissions).join();
                }
                
                // Check roles if provided
                if (roles != null && !roles.isEmpty()) {
                    rolesCheck = hasAnyRole(roles).join();
                }
                
                // Check feature flags if provided
                if (featureFlags != null && !featureFlags.isEmpty()) {
                    featureFlagsCheck = false;
                    for (String flag : featureFlags) {
                        if (isFeatureFlagEnabled(flag).join()) {
                            featureFlagsCheck = true;
                            break;
                        }
                    }
                }
                
                return permissionsCheck && rolesCheck && featureFlagsCheck;
            } catch (Exception e) {
                log.error("Error in comprehensive check: {}", e.getMessage());
                return false;
            }
        });
    }
}
