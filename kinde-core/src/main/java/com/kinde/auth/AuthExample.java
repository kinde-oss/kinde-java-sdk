package com.kinde.auth;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * Example demonstrating how to use the Kinde Auth wrapper classes.
 * This follows the same pattern as the Python SDK examples.
 */
public class AuthExample {
    
    public static void main(String[] args) {
        // Create the main Auth client
        Auth auth = new Auth();
        
        // Example 1: Working with Claims
        System.out.println("=== Claims Example ===");
        Claims claims = auth.claims();
        
        // Get a specific claim
        Map<String, Object> userClaim = claims.getClaim("sub");
        System.out.println("User claim: " + userClaim);
        
        // Get all claims
        Map<String, Object> allClaims = claims.getAllClaims();
        System.out.println("All claims: " + allClaims);
        
        // Example 2: Working with Permissions
        System.out.println("\n=== Permissions Example ===");
        Permissions permissions = auth.permissions();
        
        // Check if user has a specific permission
        boolean hasCreatePermission = permissions.hasPermission("create:todos");
        System.out.println("Has create:todos permission: " + hasCreatePermission);
        
        // Check if user has any of multiple permissions
        List<String> permissionKeys = Arrays.asList("create:todos", "read:todos", "update:todos");
        boolean hasAnyPermission = permissions.hasAnyPermission(permissionKeys);
        System.out.println("Has any of the permissions: " + hasAnyPermission);
        
        // Check if user has all permissions
        boolean hasAllPermissions = permissions.hasAllPermissions(permissionKeys);
        System.out.println("Has all permissions: " + hasAllPermissions);
        
        // Get all permissions
        Map<String, Object> allPermissions = permissions.getPermissions();
        System.out.println("All permissions: " + allPermissions);
        
        // Example 3: Working with Feature Flags
        System.out.println("\n=== Feature Flags Example ===");
        FeatureFlags featureFlags = auth.featureFlags();
        
        // Check if a feature flag is enabled
        boolean isFeatureEnabled = featureFlags.isFeatureFlagEnabled("new_ui");
        System.out.println("New UI feature enabled: " + isFeatureEnabled);
        
        // Get feature flag value
        Object flagValue = featureFlags.getFeatureFlagValue("max_items");
        System.out.println("Max items flag value: " + flagValue);
        
        // Get typed feature flag values
        String stringFlag = featureFlags.getFeatureFlagString("theme");
        Integer intFlag = featureFlags.getFeatureFlagInteger("max_items");
        System.out.println("Theme flag: " + stringFlag);
        System.out.println("Max items flag: " + intFlag);
        
        // Get all feature flags
        Map<String, FeatureFlags.FeatureFlag> allFlags = featureFlags.getFeatureFlags();
        System.out.println("All feature flags: " + allFlags);
        
        // Example 4: Working with Roles
        System.out.println("\n=== Roles Example ===");
        Roles roles = auth.roles();
        
        // Check if user has a specific role
        boolean isAdmin = roles.hasRole("admin");
        System.out.println("Is admin: " + isAdmin);
        
        // Check if user has any of multiple roles
        List<String> roleKeys = Arrays.asList("admin", "moderator", "user");
        boolean hasAnyRole = roles.hasAnyRole(roleKeys);
        System.out.println("Has any of the roles: " + hasAnyRole);
        
        // Check if user has all roles
        boolean hasAllRoles = roles.hasAllRoles(roleKeys);
        System.out.println("Has all roles: " + hasAllRoles);
        
        // Get all roles
        Map<String, Object> allRoles = roles.getRoles();
        System.out.println("All roles: " + allRoles);
        
        // Example 5: Working with Entitlements
        System.out.println("\n=== Entitlements Example ===");
        Entitlements entitlements = auth.entitlements();
        
        // Check if user has a specific entitlement
        boolean hasPremium = entitlements.hasEntitlement("premium_features");
        System.out.println("Has premium features: " + hasPremium);
        
        // Check if user has any of multiple entitlements
        List<String> entitlementKeys = Arrays.asList("premium_features", "advanced_analytics", "api_access");
        boolean hasAnyEntitlement = entitlements.hasAnyEntitlement(entitlementKeys);
        System.out.println("Has any of the entitlements: " + hasAnyEntitlement);
        
        // Check if user has all entitlements
        boolean hasAllEntitlements = entitlements.hasAllEntitlements(entitlementKeys);
        System.out.println("Has all entitlements: " + hasAllEntitlements);
        
        // Get all entitlements
        List<Map<String, Object>> allEntitlements = entitlements.getAllEntitlements();
        System.out.println("All entitlements: " + allEntitlements);
        
        // Example 6: Complex Access Control
        System.out.println("\n=== Complex Access Control Example ===");
        
        // Check if user can access a premium feature
        boolean canAccessPremium = permissions.hasPermission("access:premium") && 
                                  featureFlags.isFeatureFlagEnabled("premium_features") &&
                                  entitlements.hasEntitlement("premium_features");
        System.out.println("Can access premium features: " + canAccessPremium);
        
        // Check if user is an admin or has specific permissions
        boolean isAuthorized = roles.hasRole("admin") || 
                              permissions.hasAnyPermission(Arrays.asList("manage:users", "manage:system"));
        System.out.println("Is authorized for management: " + isAuthorized);
    }
}
