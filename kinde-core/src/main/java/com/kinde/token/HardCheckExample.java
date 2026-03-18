package com.kinde.token;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.KindeTokenFactory;
import com.kinde.accounts.KindeAccountsClient;

import java.util.Arrays;
import java.util.List;

/**
 * Example demonstrating the "hard check" functionality for permissions, roles, and feature flags.
 * This shows how the system falls back to API calls when information is not available in the token.
 * 
 * The hard check functionality is now integrated directly into the token classes (AccessToken, IDToken, BaseToken)
 * and no longer requires a separate KindeTokenChecker.
 */
public class HardCheckExample {
    
    public static void main(String[] args) {
        // Create a Kinde client
        KindeClient client = KindeClientBuilder.builder()
                .domain("https://koman.kinde.com")
                .clientId("a06a72f6df3642fe85e99e4084ddf866")
                .clientSecret("Ts3GjhZrDomohSMGDzhzjOgiK1SYXo7ZINzd73B6qywMBOoT8viHq")
                .redirectUri("http://localhost:8080/callback")
                .build();
        
        // Get a client session (M2M or user session)
        KindeClientSession session = client.clientSession();
        
        // Get the token from the session
        KindeToken token = session.retrieveTokens().getAccessToken();
        
        // Create a KindeAccountsClient for API fallback
        KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
        
        // Obtain the token factory from the client (properly wired with JWK store)
        KindeTokenFactory tokenFactory = client.tokenFactory();
        
        // Parse the token with hard check capabilities
        KindeToken tokenWithHardCheck = tokenFactory.parse(token.token(), accountsClient);
        
        // Now you can use the hard check methods directly on the token
        runHardCheckExamples(tokenWithHardCheck);
    }
    
    /**
     * Example: Check a single permission with API fallback.
     */
    private static void checkSinglePermission(KindeToken token) {
        System.out.println("=== Checking Single Permission ===");
        
        try {
            boolean hasPermission = token.hasPermission("read:users");
            
            if (hasPermission) {
                System.out.println("User has 'read:users' permission");
            } else {
                System.out.println("User does not have 'read:users' permission");
            }
        } catch (Exception e) {
            System.err.println("Error checking permission: " + e.getMessage());
        }
    }
    
    /**
     * Example: Check multiple permissions with API fallback.
     */
    private static void checkMultiplePermissions(KindeToken token) {
        System.out.println("=== Checking Multiple Permissions ===");
        
        List<String> permissions = Arrays.asList("read:users", "write:users", "delete:users", "admin");
        
        try {
            boolean hasAnyPermission = token.hasAnyPermission(permissions);
            
            if (hasAnyPermission) {
                System.out.println("User has at least one of the permissions: " + permissions);
            } else {
                System.out.println("User does not have any of the permissions: " + permissions);
            }
            
            boolean hasAllPermissions = token.hasAllPermissions(permissions);
            
            if (hasAllPermissions) {
                System.out.println("User has all permissions: " + permissions);
            } else {
                System.out.println("User does not have all permissions: " + permissions);
            }
        } catch (Exception e) {
            System.err.println("Error checking permissions: " + e.getMessage());
        }
    }
    
    /**
     * Example: Check roles with API fallback.
     */
    private static void checkRoles(KindeToken token) {
        System.out.println("=== Checking Roles ===");
        
        List<String> roles = Arrays.asList("admin", "moderator", "user");
        
        try {
            boolean hasAnyRole = token.hasAnyRole(roles);
            
            if (hasAnyRole) {
                System.out.println("User has at least one of the roles: " + roles);
            } else {
                System.out.println("User does not have any of the roles: " + roles);
            }
            
            boolean hasAllRoles = token.hasAllRoles(roles);
            
            if (hasAllRoles) {
                System.out.println("User has all roles: " + roles);
            } else {
                System.out.println("User does not have all roles: " + roles);
            }
        } catch (Exception e) {
            System.err.println("Error checking roles: " + e.getMessage());
        }
    }
    
    /**
     * Example: Check feature flags with API fallback.
     */
    private static void checkFeatureFlags(KindeToken token) {
        System.out.println("=== Checking Feature Flags ===");
        
        try {
            boolean isDarkModeEnabled = token.isFeatureFlagEnabled("dark_mode");
            
            if (isDarkModeEnabled) {
                System.out.println("Dark mode feature flag is enabled");
            } else {
                System.out.println("Dark mode feature flag is disabled");
            }
            
            Object betaFeaturesValue = token.getFeatureFlagValue("beta_features");
            
            if (betaFeaturesValue != null) {
                System.out.println("Beta features flag value: " + betaFeaturesValue);
            } else {
                System.out.println("Beta features flag not found or null");
            }
        } catch (Exception e) {
            System.err.println("Error checking feature flags: " + e.getMessage());
        }
    }
    
    /**
     * Example: Comprehensive checks combining permissions, roles, and feature flags.
     */
    private static void comprehensiveChecks(KindeToken token) {
        System.out.println("=== Comprehensive Checks ===");
        
        List<String> requiredPermissions = Arrays.asList("read:users", "write:users");
        List<String> requiredRoles = Arrays.asList("admin", "moderator");
        List<String> requiredFeatureFlags = Arrays.asList("advanced_analytics", "real_time_notifications");
        
        try {
            boolean hasAllRequirements = token.hasAll(
                    requiredPermissions, 
                    requiredRoles, 
                    requiredFeatureFlags
            );
            
            if (hasAllRequirements) {
                System.out.println("User has ALL requirements:");
                System.out.println("   - Permissions: " + requiredPermissions);
                System.out.println("   - Roles: " + requiredRoles);
                System.out.println("   - Feature Flags: " + requiredFeatureFlags);
            } else {
                System.out.println("User does not have ALL requirements");
            }
            
            boolean hasAnyRequirements = token.hasAny(
                    requiredPermissions, 
                    requiredRoles, 
                    requiredFeatureFlags
            );
            
            if (hasAnyRequirements) {
                System.out.println("User has at least one requirement from each category");
            } else {
                System.out.println("User does not have any requirements from each category");
            }
        } catch (Exception e) {
            System.err.println("Error in comprehensive checks: " + e.getMessage());
        }
    }
    
    /**
     * Run all hard check examples.
     */
    private static void runHardCheckExamples(KindeToken token) {
        checkSinglePermission(token);
        checkMultiplePermissions(token);
        checkRoles(token);
        checkFeatureFlags(token);
        comprehensiveChecks(token);
    }
    
    /**
     * Example: Real-world usage in a web application context.
     */
    public static class WebApplicationExample {
        
        private final KindeToken token;
        
        public WebApplicationExample(KindeToken token) {
            this.token = token;
        }
        
        /**
         * Check if user can access the admin dashboard.
         */
        public boolean canAccessAdminDashboard() {
            return token.hasAll(
                    Arrays.asList("read:admin", "write:admin"),
                    Arrays.asList("admin"),
                    Arrays.asList("admin_dashboard")
            );
        }
        
        /**
         * Check if user can perform user management operations.
         */
        public boolean canManageUsers() {
            return token.hasAll(
                    Arrays.asList("read:users", "write:users", "delete:users"),
                    Arrays.asList("admin", "moderator"),
                    Arrays.asList("user_management")
            );
        }
        
        /**
         * Check if user can view analytics.
         */
        public boolean canViewAnalytics() {
            return token.hasAny(
                    Arrays.asList("read:analytics", "read:reports"),
                    Arrays.asList("admin", "analyst"),
                    Arrays.asList("analytics", "advanced_analytics")
            );
        }
        
        /**
         * Check if user can access beta features.
         */
        public boolean canAccessBetaFeatures() {
            return token.isFeatureFlagEnabled("beta_features");
        }
    }
}
