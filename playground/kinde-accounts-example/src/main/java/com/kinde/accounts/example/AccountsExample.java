package com.kinde.accounts.example;

import com.kinde.accounts.KindeAccountsClient;
import com.kinde.accounts.KindeAccountsClientBuilder;
import com.kinde.core.KindeClient;
import com.kinde.core.KindeClientBuilder;
import com.kinde.accounts.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Example demonstrating how to use the Kinde Accounts API.
 * This example shows how to query entitlements, permissions, roles, and feature flags.
 */
public class AccountsExample {
    
    public static void main(String[] args) {
        // Configuration - replace with your actual values
        String domain = "your-domain.kinde.com";
        String clientId = "your-client-id";
        String clientSecret = "your-client-secret";
        String redirectUrl = "http://localhost:8080/callback";
        
        try {
            // Create KindeClient
            KindeClient kindeClient = new KindeClientBuilder()
                .withDomain(domain)
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withRedirectUrl(redirectUrl)
                .build();
            
            // Create KindeAccountsClient
            KindeAccountsClient accountsClient = new KindeAccountsClientBuilder()
                .withKindeClient(kindeClient)
                .build();
            
            // Run examples
            demonstrateEntitlements(accountsClient);
            demonstratePermissions(accountsClient);
            demonstrateRoles(accountsClient);
            demonstrateFeatureFlags(accountsClient);
            demonstrateUserInfo(accountsClient);
            
        } catch (Exception e) {
            System.err.println("Error running accounts example: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates how to work with entitlements.
     */
    private static void demonstrateEntitlements(KindeAccountsClient accountsClient) {
        System.out.println("\n=== Entitlements Example ===");
        
        try {
            // Get all entitlements
            CompletableFuture<EntitlementsResponse> entitlementsFuture = accountsClient.getEntitlements();
            EntitlementsResponse entitlements = entitlementsFuture.get();
            
            System.out.println("Organization Code: " + entitlements.getData().getOrgCode());
            System.out.println("Number of entitlements: " + entitlements.getData().getEntitlements().size());
            
            // Display entitlements
            for (Entitlement entitlement : entitlements.getData().getEntitlements()) {
                System.out.println("  - " + entitlement.getFeatureName() + " (" + entitlement.getFeatureKey() + ")");
                System.out.println("    Price: " + entitlement.getPriceName() + ", Unit Amount: " + entitlement.getUnitAmount());
                System.out.println("    Limits: " + entitlement.getEntitlementLimitMin() + " - " + entitlement.getEntitlementLimitMax());
            }
            
            // Get specific entitlement if available
            if (!entitlements.getData().getEntitlements().isEmpty()) {
                String firstEntitlementKey = entitlements.getData().getEntitlements().get(0).getFeatureKey();
                CompletableFuture<EntitlementResponse> specificEntitlementFuture = accountsClient.getEntitlement(firstEntitlementKey);
                EntitlementResponse specificEntitlement = specificEntitlementFuture.get();
                
                System.out.println("Specific entitlement: " + specificEntitlement.getData().getEntitlement().getFeatureName());
            }
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error getting entitlements: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates how to work with permissions.
     */
    private static void demonstratePermissions(KindeAccountsClient accountsClient) {
        System.out.println("\n=== Permissions Example ===");
        
        try {
            // Get all permissions
            CompletableFuture<PermissionsResponse> permissionsFuture = accountsClient.getPermissions();
            PermissionsResponse permissions = permissionsFuture.get();
            
            System.out.println("Number of permissions: " + permissions.getData().size());
            
            // Display permissions
            for (Permission permission : permissions.getData()) {
                System.out.println("  - " + permission.getName() + " (" + permission.getId() + ")");
                if (permission.getDescription() != null) {
                    System.out.println("    Description: " + permission.getDescription());
                }
            }
            
            // Check specific permissions
            List<String> permissionKeys = Arrays.asList("read:users", "write:users", "admin:access");
            
            for (String permissionKey : permissionKeys) {
                CompletableFuture<Boolean> hasPermissionFuture = accountsClient.hasPermission(permissionKey);
                boolean hasPermission = hasPermissionFuture.get();
                System.out.println("Has permission '" + permissionKey + "': " + hasPermission);
            }
            
            // Check if user has any of the permissions
            CompletableFuture<Boolean> hasAnyPermissionFuture = accountsClient.hasAnyPermission(permissionKeys);
            boolean hasAnyPermission = hasAnyPermissionFuture.get();
            System.out.println("Has any of the permissions: " + hasAnyPermission);
            
            // Check if user has all permissions
            CompletableFuture<Boolean> hasAllPermissionsFuture = accountsClient.hasAllPermissions(permissionKeys);
            boolean hasAllPermissions = hasAllPermissionsFuture.get();
            System.out.println("Has all permissions: " + hasAllPermissions);
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error getting permissions: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates how to work with roles.
     */
    private static void demonstrateRoles(KindeAccountsClient accountsClient) {
        System.out.println("\n=== Roles Example ===");
        
        try {
            // Get all roles
            CompletableFuture<RolesResponse> rolesFuture = accountsClient.getRoles();
            RolesResponse roles = rolesFuture.get();
            
            System.out.println("Number of roles: " + roles.getData().size());
            
            // Display roles
            for (Role role : roles.getData()) {
                System.out.println("  - " + role.getName() + " (" + role.getId() + ")");
                if (role.getDescription() != null) {
                    System.out.println("    Description: " + role.getDescription());
                }
            }
            
            // Check specific roles
            List<String> roleKeys = Arrays.asList("admin", "user", "moderator");
            
            for (String roleKey : roleKeys) {
                CompletableFuture<Boolean> hasRoleFuture = accountsClient.hasRole(roleKey);
                boolean hasRole = hasRoleFuture.get();
                System.out.println("Has role '" + roleKey + "': " + hasRole);
            }
            
            // Check if user has any of the roles
            CompletableFuture<Boolean> hasAnyRoleFuture = accountsClient.hasAnyRole(roleKeys);
            boolean hasAnyRole = hasAnyRoleFuture.get();
            System.out.println("Has any of the roles: " + hasAnyRole);
            
            // Check if user has all roles
            CompletableFuture<Boolean> hasAllRolesFuture = accountsClient.hasAllRoles(roleKeys);
            boolean hasAllRoles = hasAllRolesFuture.get();
            System.out.println("Has all roles: " + hasAllRoles);
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error getting roles: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates how to work with feature flags.
     */
    private static void demonstrateFeatureFlags(KindeAccountsClient accountsClient) {
        System.out.println("\n=== Feature Flags Example ===");
        
        try {
            // Get all feature flags
            CompletableFuture<FeatureFlagsResponse> flagsFuture = accountsClient.getFeatureFlags();
            FeatureFlagsResponse flags = flagsFuture.get();
            
            System.out.println("Number of feature flags: " + flags.getData().getFeatureFlags().size());
            
            // Display feature flags
            for (FeatureFlag flag : flags.getData().getFeatureFlags()) {
                System.out.println("  - " + flag.getName() + " (" + flag.getKey() + ")");
                System.out.println("    Type: " + flag.getType() + ", Value: " + flag.getValue());
            }
            
            // Check specific feature flags
            List<String> flagKeys = Arrays.asList("new-ui", "beta-features", "dark-mode");
            
            for (String flagKey : flagKeys) {
                // Get flag value
                CompletableFuture<Object> flagValueFuture = accountsClient.getFeatureFlagValue(flagKey);
                Object flagValue = flagValueFuture.get();
                System.out.println("Feature flag '" + flagKey + "' value: " + flagValue);
                
                // Check if flag is enabled (for boolean flags)
                CompletableFuture<Boolean> isEnabledFuture = accountsClient.isFeatureFlagEnabled(flagKey);
                Boolean isEnabled = isEnabledFuture.get();
                System.out.println("Feature flag '" + flagKey + "' enabled: " + isEnabled);
            }
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error getting feature flags: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates how to get user and organization information.
     */
    private static void demonstrateUserInfo(KindeAccountsClient accountsClient) {
        System.out.println("\n=== User Information Example ===");
        
        try {
            // Get user profile
            CompletableFuture<UserProfileResponse> profileFuture = accountsClient.getUserProfile();
            UserProfileResponse profile = profileFuture.get();
            
            System.out.println("User Profile:");
            System.out.println("  ID: " + profile.getData().getId());
            System.out.println("  Email: " + profile.getData().getEmail());
            System.out.println("  Name: " + profile.getData().getGivenName() + " " + profile.getData().getFamilyName());
            if (profile.getData().getPicture() != null) {
                System.out.println("  Picture: " + profile.getData().getPicture());
            }
            
            // Get user organizations
            CompletableFuture<UserOrganizationsResponse> orgsFuture = accountsClient.getUserOrganizations();
            UserOrganizationsResponse organizations = orgsFuture.get();
            
            System.out.println("User Organizations:");
            System.out.println("  Number of organizations: " + organizations.getData().size());
            for (Organization org : organizations.getData()) {
                System.out.println("  - " + org.getName() + " (" + org.getCode() + ")");
            }
            
            // Get current organization
            CompletableFuture<CurrentOrganizationResponse> currentOrgFuture = accountsClient.getCurrentOrganization();
            CurrentOrganizationResponse currentOrg = currentOrgFuture.get();
            
            System.out.println("Current Organization:");
            System.out.println("  Name: " + currentOrg.getData().getName());
            System.out.println("  Code: " + currentOrg.getData().getCode());
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error getting user information: " + e.getMessage());
        }
    }
} 