package com.kinde.accounts;

import com.kinde.KindeClientSession;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Client for accessing Kinde Accounts API functionality.
 * This client provides methods to query the current user's permissions, roles, entitlements, and feature flags.
 */
public class KindeAccountsClient {
    
    private final DefaultApi apiClient;
    private final KindeClientSession session;
    
    /**
     * Creates a new KindeAccountsClient using the provided session.
     * 
     * @param session The KindeClientSession instance to use for authentication
     */
    public KindeAccountsClient(KindeClientSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        this.session = session;
        this.apiClient = new DefaultApi();
        // Configure the API client with the session's domain and access token
        configureApiClient();
    }
    
    private void configureApiClient() {
        // Set the base path to the session's domain
        String domain = session.getDomain();
        if (domain != null && !domain.isEmpty()) {
            apiClient.getApiClient().setBasePath(domain + "/account_api/v1");
        }
        
        // Set the access token for authentication
        String accessToken = session.getAccessToken();
        if (accessToken != null && !accessToken.isEmpty()) {
            apiClient.getApiClient().setBearerToken(accessToken);
        }
    }
    
    /**
     * Gets all entitlements for the current user's organization.
     * 
     * @return A CompletableFuture containing the entitlements response
     */
    public CompletableFuture<EntitlementsResponse> getEntitlements() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getEntitlements();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get entitlements", e);
            }
        });
    }
    
    /**
     * Gets a specific entitlement by key.
     * 
     * @param key The entitlement key to retrieve
     * @return A CompletableFuture containing the entitlement response
     */
    public CompletableFuture<EntitlementResponse> getEntitlement(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getEntitlement(key);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get entitlement: " + key, e);
            }
        });
    }
    
    /**
     * Gets all permissions for the current user.
     * 
     * @return A CompletableFuture containing the permissions response
     */
    public CompletableFuture<PermissionsResponse> getPermissions() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getPermissions();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get permissions", e);
            }
        });
    }
    
    /**
     * Gets a specific permission by key.
     * 
     * @param key The permission key to retrieve
     * @return A CompletableFuture containing the permission response
     */
    public CompletableFuture<PermissionResponse> getPermission(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getPermission(key);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get permission: " + key, e);
            }
        });
    }
    
    /**
     * Gets all roles for the current user.
     * 
     * @return A CompletableFuture containing the roles response
     */
    public CompletableFuture<RolesResponse> getRoles() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getRoles();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get roles", e);
            }
        });
    }
    
    /**
     * Gets all feature flags for the current user.
     * 
     * @return A CompletableFuture containing the feature flags response
     */
    public CompletableFuture<FeatureFlagsResponse> getFeatureFlags() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getFeatureFlags();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get feature flags", e);
            }
        });
    }
    
    /**
     * Gets a specific feature flag by key.
     * 
     * @param key The feature flag key to retrieve
     * @return A CompletableFuture containing the feature flag response
     */
    public CompletableFuture<FeatureFlagResponse> getFeatureFlag(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getFeatureFlag(key);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get feature flag: " + key, e);
            }
        });
    }
    
    /**
     * Gets all organizations for the current user.
     * 
     * @return A CompletableFuture containing the user organizations response
     */
    public CompletableFuture<UserOrganizationsResponse> getUserOrganizations() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getUserOrganizations();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get user organizations", e);
            }
        });
    }
    
    /**
     * Gets the current user's profile information.
     * 
     * @return A CompletableFuture containing the user profile response
     */
    public CompletableFuture<UserProfileResponse> getUserProfile() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getUserProfile();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get user profile", e);
            }
        });
    }
    
    /**
     * Gets the current organization information.
     * 
     * @return A CompletableFuture containing the current organization response
     */
    public CompletableFuture<CurrentOrganizationResponse> getCurrentOrganization() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getCurrentOrganization();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get current organization", e);
            }
        });
    }
    
    /**
     * Checks if the current user has a specific permission.
     * 
     * @param permissionKey The permission key to check
     * @return A CompletableFuture containing true if the user has the permission, false otherwise
     */
    public CompletableFuture<Boolean> hasPermission(String permissionKey) {
        return getPermissions().thenApply(response -> {
            if (response.getData() != null) {
                return response.getData().stream()
                    .anyMatch(permission -> permissionKey.equals(permission.getName()));
            }
            return false;
        });
    }
    
    /**
     * Checks if the current user has any of the specified permissions.
     * 
     * @param permissionKeys The permission keys to check
     * @return A CompletableFuture containing true if the user has any of the permissions, false otherwise
     */
    public CompletableFuture<Boolean> hasAnyPermission(List<String> permissionKeys) {
        return getPermissions().thenApply(response -> {
            if (response.getData() != null) {
                return response.getData().stream()
                    .anyMatch(permission -> permissionKeys.contains(permission.getName()));
            }
            return false;
        });
    }
    
    /**
     * Checks if the current user has all of the specified permissions.
     * 
     * @param permissionKeys The permission keys to check
     * @return A CompletableFuture containing true if the user has all of the permissions, false otherwise
     */
    public CompletableFuture<Boolean> hasAllPermissions(List<String> permissionKeys) {
        return getPermissions().thenApply(response -> {
            if (response.getData() != null) {
                List<String> userPermissions = response.getData().stream()
                    .map(Permission::getName)
                    .toList();
                return permissionKeys.stream().allMatch(userPermissions::contains);
            }
            return false;
        });
    }
    
    /**
     * Checks if the current user has a specific role.
     * 
     * @param roleKey The role key to check
     * @return A CompletableFuture containing true if the user has the role, false otherwise
     */
    public CompletableFuture<Boolean> hasRole(String roleKey) {
        return getRoles().thenApply(response -> {
            if (response.getData() != null) {
                return response.getData().stream()
                    .anyMatch(role -> roleKey.equals(role.getName()));
            }
            return false;
        });
    }
    
    /**
     * Checks if the current user has any of the specified roles.
     * 
     * @param roleKeys The role keys to check
     * @return A CompletableFuture containing true if the user has any of the roles, false otherwise
     */
    public CompletableFuture<Boolean> hasAnyRole(List<String> roleKeys) {
        return getRoles().thenApply(response -> {
            if (response.getData() != null) {
                return response.getData().stream()
                    .anyMatch(role -> roleKeys.contains(role.getName()));
            }
            return false;
        });
    }
    
    /**
     * Checks if the current user has all of the specified roles.
     * 
     * @param roleKeys The role keys to check
     * @return A CompletableFuture containing true if the user has all of the roles, false otherwise
     */
    public CompletableFuture<Boolean> hasAllRoles(List<String> roleKeys) {
        return getRoles().thenApply(response -> {
            if (response.getData() != null) {
                List<String> userRoles = response.getData().stream()
                    .map(Role::getName)
                    .toList();
                return roleKeys.stream().allMatch(userRoles::contains);
            }
            return false;
        });
    }
    
    /**
     * Gets the value of a specific feature flag.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return A CompletableFuture containing the feature flag value, or null if not found
     */
    public CompletableFuture<Object> getFeatureFlagValue(String flagKey) {
        return getFeatureFlags().thenApply(response -> {
            if (response.getData() != null && response.getData().getFeatureFlags() != null) {
                return response.getData().getFeatureFlags().stream()
                    .filter(flag -> flagKey.equals(flag.getKey()))
                    .findFirst()
                    .map(FeatureFlag::getValue)
                    .orElse(null);
            }
            return null;
        });
    }
    
    /**
     * Checks if a specific feature flag is enabled (has a boolean value of true).
     * 
     * @param flagKey The feature flag key to check
     * @return A CompletableFuture containing true if the feature flag is enabled, false otherwise
     */
    public CompletableFuture<Boolean> isFeatureFlagEnabled(String flagKey) {
        return getFeatureFlagValue(flagKey).thenApply(value -> {
            return value instanceof Boolean && (Boolean) value;
        });
    }
    
    /**
     * Gets the value of a specific feature flag as a String.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return A CompletableFuture containing the feature flag value as String, or null if not found or not a String
     */
    public CompletableFuture<String> getFeatureFlagValueAsString(String flagKey) {
        return getFeatureFlagValue(flagKey).thenApply(value -> 
            value instanceof String ? (String) value : null);
    }
    
    /**
     * Gets the value of a specific feature flag as an Integer.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return A CompletableFuture containing the feature flag value as Integer, or null if not found or not an Integer
     */
    public CompletableFuture<Integer> getFeatureFlagValueAsInteger(String flagKey) {
        return getFeatureFlagValue(flagKey).thenApply(value -> 
            value instanceof Integer ? (Integer) value : null);
    }
    
    /**
     * Gets the value of a specific feature flag as a Boolean.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return A CompletableFuture containing the feature flag value as Boolean, or null if not found or not a Boolean
     */
    public CompletableFuture<Boolean> getFeatureFlagValueAsBoolean(String flagKey) {
        return getFeatureFlagValue(flagKey).thenApply(value -> 
            value instanceof Boolean ? (Boolean) value : null);
    }
} 