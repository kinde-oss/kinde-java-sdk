package com.kinde.accounts;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Client for accessing Kinde Accounts API functionality.
 * This client provides methods to query the current user's permissions, roles, entitlements, and feature flags.
 */
public class KindeAccountsClient {
    
    private final DefaultApi apiClient;
    private final KindeClientSession session;
    
    /**
     * Creates a new KindeAccountsClient using the provided KindeClient.
     * 
     * @param kindeClient The KindeClient instance to use for authentication
     */
    public KindeAccountsClient(KindeClient kindeClient) {
        if (kindeClient == null) {
            throw new IllegalArgumentException("KindeClient cannot be null");
        }
        this.session = kindeClient.clientSession();
        this.apiClient = new DefaultApi();
        // Configure the API client with the session's domain and access token
        configureApiClient();
    }
    
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
        if (session != null) {
            String domain = session.getDomain();
            if (domain != null && !domain.isEmpty()) {
                // Ensure scheme
                if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
                    domain = "https://" + domain;
                }
                // Trim trailing slash
                if (domain.endsWith("/")) {
                    domain = domain.substring(0, domain.length() - 1);
                }
                apiClient.getApiClient().setBasePath(domain + "/account_api/v1");
            }

            // Set the access token for authentication
            String accessToken = session.getAccessToken();
            if (accessToken != null && !accessToken.isEmpty()) {
                apiClient.getApiClient().setBearerToken(accessToken);
            }
        }
    }

    /**
     * Refreshes the bearer token from the session, if present.
     */
    private void refreshAuth() {
        if (session != null) {
            String accessToken = session.getAccessToken();
            if (accessToken != null && !accessToken.isEmpty()) {
                apiClient.getApiClient().setBearerToken(accessToken);
            }
        }
    }
    
    /**
     * Gets all entitlements for the current user's organization.
     * 
     * @return A CompletableFuture containing the entitlements response
     */
    public CompletableFuture<EntitlementsResponse> getEntitlements() {
        refreshAuth();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getEntitlements();
            } catch (ApiException ae) {
                throw new RuntimeException("Failed to get entitlements (status " + ae.getCode() + ")", ae);
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
        if (key == null || key.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("Entitlement key cannot be null or empty"));
        }
        refreshAuth();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getEntitlement(key);
            } catch (ApiException ae) {
                throw new RuntimeException("Failed to get entitlement: " + key + " (status " + ae.getCode() + ")", ae);
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
        refreshAuth();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getPermissions();
            } catch (ApiException ae) {
                throw new RuntimeException("Failed to get permissions (status " + ae.getCode() + ")", ae);
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
        if (key == null || key.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("Permission key cannot be null or empty"));
        }
        refreshAuth();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getPermission(key);
            } catch (ApiException ae) {
                throw new RuntimeException("Failed to get permission: " + key + " (status " + ae.getCode() + ")", ae);
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
        refreshAuth();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getRoles();
            } catch (ApiException ae) {
                throw new RuntimeException("Failed to get roles (status " + ae.getCode() + ")", ae);
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
        refreshAuth();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getFeatureFlags();
            } catch (ApiException ae) {
                throw new RuntimeException("Failed to get feature flags (status " + ae.getCode() + ")", ae);
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
        if (key == null || key.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("Feature flag key cannot be null or empty"));
        }
        refreshAuth();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getFeatureFlag(key);
            } catch (ApiException ae) {
                throw new RuntimeException("Failed to get feature flag: " + key + " (status " + ae.getCode() + ")", ae);
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
        refreshAuth();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getUserOrganizations();
            } catch (ApiException ae) {
                throw new RuntimeException("Failed to get user organizations (status " + ae.getCode() + ")", ae);
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
        refreshAuth();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getUserProfile();
            } catch (ApiException ae) {
                throw new RuntimeException("Failed to get user profile (status " + ae.getCode() + ")", ae);
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
        refreshAuth();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getCurrentOrganization();
            } catch (ApiException ae) {
                throw new RuntimeException("Failed to get current organization (status " + ae.getCode() + ")", ae);
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
        if (permissionKey == null || permissionKey.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("Permission key cannot be null or empty"));
        }
        return getPermissions().thenApply(response -> {
            if (response.getData() != null) {
                return response.getData().stream()
                    .anyMatch(p -> permissionKey.equals(p.getId())
                                 || permissionKey.equals(p.getName()));
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
        if (permissionKeys == null || permissionKeys.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        return getPermissions().thenApply(response -> {
            if (response.getData() != null) {
                return response.getData().stream()
                    .anyMatch(p -> permissionKeys.contains(p.getId()) || permissionKeys.contains(p.getName()));
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
        if (permissionKeys == null || permissionKeys.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        return getPermissions().thenApply(response -> {
            if (response.getData() != null) {
                List<String> userPermissions = response.getData().stream()
                    .flatMap(p -> Stream.of(p.getId(), p.getName()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
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
        if (roleKey == null || roleKey.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("Role key cannot be null or empty"));
        }
        return getRoles().thenApply(response -> {
            if (response.getData() != null) {
                return response.getData().stream()
                    .anyMatch(r -> roleKey.equals(r.getId()) || roleKey.equals(r.getName()));
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
        if (roleKeys == null || roleKeys.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        return getRoles().thenApply(response -> {
            if (response.getData() != null) {
                return response.getData().stream()
                    .anyMatch(r -> roleKeys.contains(r.getId()) || roleKeys.contains(r.getName()));
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
        if (roleKeys == null || roleKeys.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        return getRoles().thenApply(response -> {
            if (response.getData() != null) {
                List<String> userRoles = response.getData().stream()
                    .flatMap(r -> Stream.of(r.getId(), r.getName()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
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
        if (flagKey == null || flagKey.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("Feature flag key cannot be null or empty"));
        }
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
        if (flagKey == null || flagKey.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("Feature flag key cannot be null or empty"));
        }
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
        if (flagKey == null || flagKey.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("Feature flag key cannot be null or empty"));
        }
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
        if (flagKey == null || flagKey.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("Feature flag key cannot be null or empty"));
        }
        return getFeatureFlagValue(flagKey).thenApply(value -> {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            if (value instanceof String) {
                try {
                    return Integer.valueOf((String) value);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
            return null;
        });
    }
    
    /**
     * Gets the value of a specific feature flag as a Boolean.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return A CompletableFuture containing the feature flag value as Boolean, or null if not found or not a Boolean
     */
    public CompletableFuture<Boolean> getFeatureFlagValueAsBoolean(String flagKey) {
        if (flagKey == null || flagKey.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("Feature flag key cannot be null or empty"));
        }
        return getFeatureFlagValue(flagKey).thenApply(value -> 
            value instanceof Boolean ? (Boolean) value : null);
    }
} 