package com.kinde.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthWrappersTest {

    private Auth auth;

    @BeforeEach
    void setUp() {
        auth = new Auth();
    }

    @Test
    void testAuthInstanceCreation() {
        // Test that Auth instance can be created
        assertNotNull(auth);
    }

    @Test
    void testClaimsWrapperCreation() {
        // Test that Claims wrapper can be created
        Claims claims = auth.claims();
        assertNotNull(claims);
        assertTrue(claims instanceof Claims);
    }

    @Test
    void testPermissionsWrapperCreation() {
        // Test that Permissions wrapper can be created
        Permissions permissions = auth.permissions();
        assertNotNull(permissions);
        assertTrue(permissions instanceof Permissions);
    }

    @Test
    void testRolesWrapperCreation() {
        // Test that Roles wrapper can be created
        Roles roles = auth.roles();
        assertNotNull(roles);
        assertTrue(roles instanceof Roles);
    }

    @Test
    void testFeatureFlagsWrapperCreation() {
        // Test that FeatureFlags wrapper can be created
        FeatureFlags featureFlags = auth.featureFlags();
        assertNotNull(featureFlags);
        assertTrue(featureFlags instanceof FeatureFlags);
    }

    @Test
    void testEntitlementsWrapperCreation() {
        // Test that Entitlements wrapper can be created
        Entitlements entitlements = auth.entitlements();
        assertNotNull(entitlements);
        assertTrue(entitlements instanceof Entitlements);
    }

    @Test
    void testWrapperInstancesAreDifferent() {
        // Test that each wrapper is a different instance
        Claims claims1 = auth.claims();
        Claims claims2 = auth.claims();
        Permissions permissions = auth.permissions();
        Roles roles = auth.roles();
        FeatureFlags featureFlags = auth.featureFlags();
        Entitlements entitlements = auth.entitlements();

        // Each wrapper should be a different instance
        assertNotSame(claims1, permissions);
        assertNotSame(claims1, roles);
        assertNotSame(claims1, featureFlags);
        assertNotSame(claims1, entitlements);
        assertNotSame(permissions, roles);
        assertNotSame(permissions, featureFlags);
        assertNotSame(permissions, entitlements);
        assertNotSame(roles, featureFlags);
        assertNotSame(roles, entitlements);
        assertNotSame(featureFlags, entitlements);
    }

    @Test
    void testWrapperInstancesAreReused() {
        // Test that the same wrapper instance is returned on multiple calls
        Claims claims1 = auth.claims();
        Claims claims2 = auth.claims();
        Permissions permissions1 = auth.permissions();
        Permissions permissions2 = auth.permissions();
        Roles roles1 = auth.roles();
        Roles roles2 = auth.roles();
        FeatureFlags featureFlags1 = auth.featureFlags();
        FeatureFlags featureFlags2 = auth.featureFlags();
        Entitlements entitlements1 = auth.entitlements();
        Entitlements entitlements2 = auth.entitlements();

        // Same wrapper should be returned (singleton behavior)
        assertSame(claims1, claims2);
        assertSame(permissions1, permissions2);
        assertSame(roles1, roles2);
        assertSame(featureFlags1, featureFlags2);
        assertSame(entitlements1, entitlements2);
    }

    @Test
    void testClaimsMethodsExist() {
        // Test that Claims methods exist and can be called
        Claims claims = auth.claims();
        
        // These methods should exist and not throw exceptions
        assertDoesNotThrow(() -> {
            claims.getClaim("test");
            claims.getAllClaims();
        });
    }

    @Test
    void testPermissionsMethodsExist() {
        // Test that Permissions methods exist and can be called
        Permissions permissions = auth.permissions();
        
        // These methods should exist and not throw exceptions
        assertDoesNotThrow(() -> {
            permissions.getPermission("test");
            permissions.getPermissions();
            permissions.hasPermission("test");
            permissions.hasAnyPermission(java.util.Arrays.asList("test"));
            permissions.hasAllPermissions(java.util.Arrays.asList("test"));
        });
    }

    @Test
    void testRolesMethodsExist() {
        // Test that Roles methods exist and can be called
        Roles roles = auth.roles();
        
        // These methods should exist and not throw exceptions
        assertDoesNotThrow(() -> {
            roles.getRole("test");
            roles.getRoles();
            roles.hasRole("test");
            roles.hasAnyRole(java.util.Arrays.asList("test"));
            roles.hasAllRoles(java.util.Arrays.asList("test"));
        });
    }

    @Test
    void testFeatureFlagsMethodsExist() {
        // Test that FeatureFlags methods exist and can be called
        FeatureFlags featureFlags = auth.featureFlags();
        
        // These methods should exist and not throw exceptions
        assertDoesNotThrow(() -> {
            featureFlags.getFeatureFlag("test");
            featureFlags.getFeatureFlags();
            featureFlags.getFeatureFlagString("test");
            featureFlags.getFeatureFlagInteger("test");
            featureFlags.isFeatureFlagEnabled("test");
        });
    }

    @Test
    void testEntitlementsMethodsExist() {
        // Test that Entitlements methods exist and can be called
        Entitlements entitlements = auth.entitlements();
        
        // These methods should exist and not throw exceptions
        assertDoesNotThrow(() -> {
            entitlements.getAllEntitlements();
            entitlements.getEntitlement("test");
            entitlements.hasEntitlement("test");
            entitlements.hasAnyEntitlement(java.util.Arrays.asList("test"));
            entitlements.hasAllEntitlements(java.util.Arrays.asList("test"));
        });
    }

    @Test
    void testBaseAuthFunctionality() {
        // Test that BaseAuth functionality works
        TestBaseAuth testAuth = new TestBaseAuth();
        assertNotNull(testAuth);
        
        // Test that logger is available
        assertNotNull(testAuth.logger);
    }

    // Concrete implementation for testing BaseAuth
    private static class TestBaseAuth extends BaseAuth {
        // No additional implementation needed for testing
    }
}
