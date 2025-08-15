package com.kinde.token;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Example demonstrating the "hard check" functionality for permissions, roles, and feature flags.
 * This shows how the system falls back to API calls when information is not available in the token.
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
        
        // Create a token checker with hard check functionality
        KindeTokenChecker checker = KindeTokenCheckerBuilder.builder()
                .token(token)
                .session(session)
                .build();
        
        // Kick off all checks and wait for completion
        CompletableFuture<Void> f1 = checkSinglePermission(checker);
        CompletableFuture<Void> f2 = checkMultiplePermissions(checker);
        CompletableFuture<Void> f3 = checkRoles(checker);
        CompletableFuture<Void> f4 = checkFeatureFlags(checker);
        CompletableFuture<Void> f5 = comprehensiveChecks(checker);
        CompletableFuture.allOf(f1, f2, f3, f4, f5).join();
    }
    
    /**
     * Example: Check a single permission with API fallback.
     */
    private static CompletableFuture<Void> checkSinglePermission(KindeTokenChecker checker) {
        log.info("=== Checking Single Permission ===");
        
        CompletableFuture<Boolean> hasPermission = checker.hasPermission("read:users");
        
        return hasPermission.thenAccept(hasAccess -> {
            if (hasAccess) {
                log.info("✅ User has 'read:users' permission");
            } else {
                log.info("❌ User does not have 'read:users' permission");
            }
        }).exceptionally(throwable -> {
            log.error("Error checking permission: {}", throwable.getMessage());
            return null;
        });
    }
    
    /**
     * Example: Check multiple permissions with API fallback.
     */
    private static CompletableFuture<Void> checkMultiplePermissions(KindeTokenChecker checker) {
        log.info("=== Checking Multiple Permissions ===");
        
        List<String> permissions = Arrays.asList("read:users", "write:users", "delete:users");
        
        // Check if user has any of the permissions
        CompletableFuture<Boolean> hasAnyPermission = checker.hasAnyPermission(permissions);
        CompletableFuture<Void> anyPermissionFuture = hasAnyPermission.thenAccept(hasAccess -> {
            if (hasAccess) {
                log.info("✅ User has at least one of the permissions: {}", permissions);
            } else {
                log.info("❌ User does not have any of the permissions: {}", permissions);
            }
        });
        
        // Check if user has all of the permissions
        CompletableFuture<Boolean> hasAllPermissions = checker.hasAllPermissions(permissions);
        CompletableFuture<Void> allPermissionsFuture = hasAllPermissions.thenAccept(hasAccess -> {
            if (hasAccess) {
                log.info("✅ User has all permissions: {}", permissions);
            } else {
                log.info("❌ User does not have all permissions: {}", permissions);
            }
        });
        
        return CompletableFuture.allOf(anyPermissionFuture, allPermissionsFuture);
    }
    
    /**
     * Example: Check roles with API fallback.
     */
    private static CompletableFuture<Void> checkRoles(KindeTokenChecker checker) {
        log.info("=== Checking Roles ===");
        
        List<String> roles = Arrays.asList("admin", "moderator", "user");
        
        // Check if user has any of the roles
        CompletableFuture<Boolean> hasAnyRole = checker.hasAnyRole(roles);
        CompletableFuture<Void> anyRoleFuture = hasAnyRole.thenAccept(hasRole -> {
            if (hasRole) {
                log.info("✅ User has at least one of the roles: {}", roles);
            } else {
                log.info("❌ User does not have any of the roles: {}", roles);
            }
        });
        
        // Check if user has all of the roles
        CompletableFuture<Boolean> hasAllRoles = checker.hasAllRoles(roles);
        CompletableFuture<Void> allRolesFuture = hasAllRoles.thenAccept(hasRole -> {
            if (hasRole) {
                log.info("✅ User has all roles: {}", roles);
            } else {
                log.info("❌ User does not have all roles: {}", roles);
            }
        });
        
        return CompletableFuture.allOf(anyRoleFuture, allRolesFuture);
    }
    
    /**
     * Example: Check feature flags with API fallback.
     */
    private static CompletableFuture<Void> checkFeatureFlags(KindeTokenChecker checker) {
        log.info("=== Checking Feature Flags ===");
        
        // Check if a feature flag is enabled
        CompletableFuture<Boolean> isDarkModeEnabled = checker.isFeatureFlagEnabled("dark_mode");
        CompletableFuture<Void> darkModeFuture = isDarkModeEnabled.thenAccept(enabled -> {
            if (enabled) {
                log.info("✅ Dark mode feature flag is enabled");
            } else {
                log.info("❌ Dark mode feature flag is disabled");
            }
        });
        
        // Get the value of a feature flag
        CompletableFuture<Object> betaFeaturesValue = checker.getFeatureFlagValue("beta_features");
        CompletableFuture<Void> betaFeaturesFuture = betaFeaturesValue.thenAccept(value -> {
            if (value != null) {
                log.info("✅ Beta features flag value: {}", value);
            } else {
                log.info("❌ Beta features flag not found or null");
            }
        });
        
        return CompletableFuture.allOf(darkModeFuture, betaFeaturesFuture);
    }
    
    /**
     * Example: Comprehensive checks combining permissions, roles, and feature flags.
     */
    private static CompletableFuture<Void> comprehensiveChecks(KindeTokenChecker checker) {
        log.info("=== Comprehensive Checks ===");
        
        List<String> requiredPermissions = Arrays.asList("read:users", "write:users");
        List<String> requiredRoles = Arrays.asList("admin", "moderator");
        List<String> requiredFeatureFlags = Arrays.asList("advanced_analytics", "real_time_notifications");
        
        // Check if user has ALL requirements (permissions AND roles AND feature flags)
        CompletableFuture<Boolean> hasAllRequirements = checker.hasAll(
                requiredPermissions, 
                requiredRoles, 
                requiredFeatureFlags
        );
        
        CompletableFuture<Void> allRequirementsFuture = hasAllRequirements.thenAccept(hasAccess -> {
            if (hasAccess) {
                log.info("✅ User has ALL requirements:");
                log.info("   - Permissions: {}", requiredPermissions);
                log.info("   - Roles: {}", requiredRoles);
                log.info("   - Feature Flags: {}", requiredFeatureFlags);
            } else {
                log.info("❌ User does not have ALL requirements");
            }
        });
        
        // Check if user has ANY requirements (permissions OR roles OR feature flags)
        CompletableFuture<Boolean> hasAnyRequirements = checker.hasAny(
                requiredPermissions, 
                requiredRoles, 
                requiredFeatureFlags
        );
        
        CompletableFuture<Void> anyRequirementsFuture = hasAnyRequirements.thenAccept(hasAccess -> {
            if (hasAccess) {
                log.info("✅ User has at least one requirement from each category");
            } else {
                log.info("❌ User does not have any requirements from each category");
            }
        });
        
        return CompletableFuture.allOf(allRequirementsFuture, anyRequirementsFuture);
    }
    
    /**
     * Example: Real-world usage in a web application context.
     */
    public static class WebApplicationExample {
        
        private final KindeTokenChecker checker;
        
        public WebApplicationExample(KindeToken token, KindeClientSession session) {
            this.checker = KindeTokenCheckerBuilder.builder()
                    .token(token)
                    .session(session)
                    .build();
        }
        
        /**
         * Check if user can access the admin dashboard.
         */
        public CompletableFuture<Boolean> canAccessAdminDashboard() {
            return checker.hasAll(
                    Arrays.asList("read:admin", "write:admin"),
                    Arrays.asList("admin"),
                    Arrays.asList("admin_dashboard")
            );
        }
        
        /**
         * Check if user can perform user management operations.
         */
        public CompletableFuture<Boolean> canManageUsers() {
            return checker.hasAll(
                    Arrays.asList("read:users", "write:users", "delete:users"),
                    Arrays.asList("admin", "moderator"),
                    Arrays.asList("user_management")
            );
        }
        
        /**
         * Check if user can view analytics.
         */
        public CompletableFuture<Boolean> canViewAnalytics() {
            return checker.hasAny(
                    Arrays.asList("read:analytics", "read:reports"),
                    Arrays.asList("admin", "analyst"),
                    Arrays.asList("analytics", "advanced_analytics")
            );
        }
        
        /**
         * Check if user can access beta features.
         */
        public CompletableFuture<Boolean> canAccessBetaFeatures() {
            return checker.isFeatureFlagEnabled("beta_features");
        }
    }
}
