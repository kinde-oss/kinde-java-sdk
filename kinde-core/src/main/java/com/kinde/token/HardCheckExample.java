package com.kinde.token;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.KindeTokenFactory;
import com.kinde.accounts.KindeAccountsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private static final Logger log = LoggerFactory.getLogger(HardCheckExample.class);
    
    public static void main(String[] args) {
        // Create a Kinde client
        KindeClient client = KindeClientBuilder.builder()
                .domain("https://your-domain.kinde.com")
                .clientId("your-client-id")
                .clientSecret("your-client-secret")
                .redirectUri("http://localhost:8080/callback")
                .build();
        
        // Get a client session (M2M or user session)
        KindeClientSession session = client.clientSession();
        
        // Get the token from the session
        KindeToken token = session.retrieveTokens().getAccessToken();
        
        // Create a KindeAccountsClient for API fallback
        KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
        
        // Create a token factory
        KindeTokenFactory tokenFactory = new KindeTokenFactoryImpl(null); // JWK store would be injected in real usage
        
        // Parse the token with hard check capabilities
        KindeToken tokenWithHardCheck = tokenFactory.parse(token.token(), accountsClient);
        
        // Now you can use the hard check methods directly on the token
        runHardCheckExamples(tokenWithHardCheck);
    }
    
    /**
     * Example: Check a single permission with API fallback.
     */
    private static void checkSinglePermission(KindeToken token) {
        log.info("=== Checking Single Permission ===");
        
        try {
            boolean hasPermission = token.hasPermission("read:users");
            
            if (hasPermission) {
                log.info("✅ User has 'read:users' permission");
            } else {
                log.info("❌ User does not have 'read:users' permission");
            }
        } catch (Exception e) {
            log.error("Error checking permission: {}", e.getMessage());
        }
    }
    
    /**
     * Example: Check multiple permissions with API fallback.
     */
    private static void checkMultiplePermissions(KindeToken token) {
        log.info("=== Checking Multiple Permissions ===");
        
        List<String> permissions = Arrays.asList("read:users", "write:users", "delete:users");
        
        try {
            // Check if user has any of the permissions
            boolean hasAnyPermission = token.hasAnyPermission(permissions);
            
            if (hasAnyPermission) {
                log.info("✅ User has at least one of the permissions: {}", permissions);
            } else {
                log.info("❌ User does not have any of the permissions: {}", permissions);
            }
            
            // Check if user has all of the permissions
            boolean hasAllPermissions = token.hasAllPermissions(permissions);
            
            if (hasAllPermissions) {
                log.info("✅ User has all permissions: {}", permissions);
            } else {
                log.info("❌ User does not have all permissions: {}", permissions);
            }
        } catch (Exception e) {
            log.error("Error checking permissions: {}", e.getMessage());
        }
    }
    
    /**
     * Example: Check roles with API fallback.
     */
    private static void checkRoles(KindeToken token) {
        log.info("=== Checking Roles ===");
        
        List<String> roles = Arrays.asList("admin", "moderator", "user");
        
        try {
            // Check if user has any of the roles
            boolean hasAnyRole = token.hasAnyRole(roles);
            
            if (hasAnyRole) {
                log.info("✅ User has at least one of the roles: {}", roles);
            } else {
                log.info("❌ User does not have any of the roles: {}", roles);
            }
            
            // Check if user has all of the roles
            boolean hasAllRoles = token.hasAllRoles(roles);
            
            if (hasAllRoles) {
                log.info("✅ User has all roles: {}", roles);
            } else {
                log.info("❌ User does not have all roles: {}", roles);
            }
        } catch (Exception e) {
            log.error("Error checking roles: {}", e.getMessage());
        }
    }
    
    /**
     * Example: Check feature flags with API fallback.
     */
    private static void checkFeatureFlags(KindeToken token) {
        log.info("=== Checking Feature Flags ===");
        
        try {
            // Check if a feature flag is enabled
            boolean isDarkModeEnabled = token.isFeatureFlagEnabled("dark_mode");
            
            if (isDarkModeEnabled) {
                log.info("✅ Dark mode feature flag is enabled");
            } else {
                log.info("❌ Dark mode feature flag is disabled");
            }
            
            // Get the value of a feature flag
            Object betaFeaturesValue = token.getFeatureFlagValue("beta_features");
            
            if (betaFeaturesValue != null) {
                log.info("✅ Beta features flag value: {}", betaFeaturesValue);
            } else {
                log.info("❌ Beta features flag not found or null");
            }
        } catch (Exception e) {
            log.error("Error checking feature flags: {}", e.getMessage());
        }
    }
    
    /**
     * Example: Comprehensive checks combining permissions, roles, and feature flags.
     */
    private static void comprehensiveChecks(KindeToken token) {
        log.info("=== Comprehensive Checks ===");
        
        List<String> requiredPermissions = Arrays.asList("read:users", "write:users");
        List<String> requiredRoles = Arrays.asList("admin", "moderator");
        List<String> requiredFeatureFlags = Arrays.asList("advanced_analytics", "real_time_notifications");
        
        try {
            // Check if user has ALL requirements (permissions AND roles AND feature flags)
            boolean hasAllRequirements = token.hasAll(
                    requiredPermissions, 
                    requiredRoles, 
                    requiredFeatureFlags
            );
            
            if (hasAllRequirements) {
                log.info("✅ User has ALL requirements:");
                log.info("   - Permissions: {}", requiredPermissions);
                log.info("   - Roles: {}", requiredRoles);
                log.info("   - Feature Flags: {}", requiredFeatureFlags);
            } else {
                log.info("❌ User does not have ALL requirements");
            }
            
            // Check if user has ANY requirements (permissions OR roles OR feature flags)
            boolean hasAnyRequirements = token.hasAny(
                    requiredPermissions, 
                    requiredRoles, 
                    requiredFeatureFlags
            );
            
            if (hasAnyRequirements) {
                log.info("✅ User has at least one requirement from each category");
            } else {
                log.info("❌ User does not have any requirements from each category");
            }
        } catch (Exception e) {
            log.error("Error in comprehensive checks: {}", e.getMessage());
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
