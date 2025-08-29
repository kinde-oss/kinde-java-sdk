package com.kinde.token;


import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface KindeToken {

    boolean valid();

    String token();

    String getUser();

    List<String> getOrganisations();

    Object getClaim(String key);

    List<String> getPermissions();

    /**
     * Gets the roles from the token.
     * This method checks both "roles" and "x-hasura-roles" claims and returns the first non-null result.
     * 
     * @return List of role strings, never null (empty list if no roles are found)
     */
    default List<String> getRoles() {
        return Collections.emptyList();
    }

    String getStringFlag(String key);

    Integer getIntegerFlag(String key);

    Boolean getBooleanFlag(String key);

    /**
     * Returns all flags available on the token as a simple key-value map.
     * Implementations should expose parsed values. Never null.
     *
     * @return a non-null map of flags (empty if none)
     */
    default Map<String, Object> getFlags() {
        return Collections.emptyMap();
    }

    // ========== Hard Check Methods (API Fallback) ==========

    /**
     * Checks if the user has a specific permission, falling back to API if not in token.
     * 
     * @param permissionKey The permission key to check
     * @return true if the user has the permission, false otherwise
     */
    default boolean hasPermission(String permissionKey) {
        return false;
    }

    /**
     * Checks if the user has any of the specified permissions, falling back to API if not in token.
     * 
     * @param permissionKeys The permission keys to check
     * @return true if the user has any of the permissions, false otherwise
     */
    default boolean hasAnyPermission(List<String> permissionKeys) {
        return false;
    }

    /**
     * Checks if the user has all of the specified permissions, falling back to API if not in token.
     * 
     * @param permissionKeys The permission keys to check
     * @return true if the user has all of the permissions, false otherwise
     */
    default boolean hasAllPermissions(List<String> permissionKeys) {
        return false;
    }

    /**
     * Checks if the user has a specific role, falling back to API if not in token.
     * 
     * @param roleKey The role key to check
     * @return true if the user has the role, false otherwise
     */
    default boolean hasRole(String roleKey) {
        return false;
    }

    /**
     * Checks if the user has any of the specified roles, falling back to API if not in token.
     * 
     * @param roleKeys The role keys to check
     * @return true if the user has any of the roles, false otherwise
     */
    default boolean hasAnyRole(List<String> roleKeys) {
        return false;
    }

    /**
     * Checks if the user has all of the specified roles, falling back to API if not in token.
     * 
     * @param roleKeys The role keys to check
     * @return true if the user has all of the roles, false otherwise
     */
    default boolean hasAllRoles(List<String> roleKeys) {
        return false;
    }

    /**
     * Checks if a feature flag is enabled, falling back to API if not in token.
     * 
     * @param flagKey The feature flag key to check
     * @return true if the feature flag is enabled, false otherwise
     */
    default boolean isFeatureFlagEnabled(String flagKey) {
        return false;
    }

    /**
     * Gets the value of a feature flag, falling back to API if not in token.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return the feature flag value, or null if not found
     */
    default Object getFeatureFlagValue(String flagKey) {
        return null;
    }

    /**
     * Comprehensive check that combines multiple permission, role, and feature flag checks.
     * All checks must pass for the method to return true.
     * 
     * @param permissions List of permissions to check (optional)
     * @param roles List of roles to check (optional)
     * @param featureFlags List of feature flags to check (optional)
     * @return true if all checks pass, false otherwise
     */
    default boolean hasAll(List<String> permissions, List<String> roles, List<String> featureFlags) {
        return false;
    }

    /**
     * Comprehensive check that combines multiple permission, role, and feature flag checks.
     * At least one check from each category must pass for the method to return true.
     * 
     * @param permissions List of permissions to check (optional)
     * @param roles List of roles to check (optional)
     * @param featureFlags List of feature flags to check (optional)
     * @return true if at least one check from each category passes, false otherwise
     */
    default boolean hasAny(List<String> permissions, List<String> roles, List<String> featureFlags) {
        return false;
    }
}
