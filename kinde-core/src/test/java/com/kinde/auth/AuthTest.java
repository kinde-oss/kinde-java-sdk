package com.kinde.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthTest {

    private Auth auth;

    @BeforeEach
    void setUp() {
        auth = new Auth();
    }

    @Test
    void testClaims_ReturnsClaimsInstance() {
        // Test
        Claims claims = auth.claims();

        // Verify
        assertNotNull(claims);
        assertTrue(claims instanceof Claims);
    }

    @Test
    void testPermissions_ReturnsPermissionsInstance() {
        // Test
        Permissions permissions = auth.permissions();

        // Verify
        assertNotNull(permissions);
        assertTrue(permissions instanceof Permissions);
    }

    @Test
    void testRoles_ReturnsRolesInstance() {
        // Test
        Roles roles = auth.roles();

        // Verify
        assertNotNull(roles);
        assertTrue(roles instanceof Roles);
    }

    @Test
    void testFeatureFlags_ReturnsFeatureFlagsInstance() {
        // Test
        FeatureFlags featureFlags = auth.featureFlags();

        // Verify
        assertNotNull(featureFlags);
        assertTrue(featureFlags instanceof FeatureFlags);
    }

    @Test
    void testEntitlements_ReturnsEntitlementsInstance() {
        // Test
        Entitlements entitlements = auth.entitlements();

        // Verify
        assertNotNull(entitlements);
        assertTrue(entitlements instanceof Entitlements);
    }

    @Test
    void testMultipleCallsReturnSameInstances() {
        // Test
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

        // Verify - should return the same instances (singleton behavior)
        assertSame(claims1, claims2);
        assertSame(permissions1, permissions2);
        assertSame(roles1, roles2);
        assertSame(featureFlags1, featureFlags2);
        assertSame(entitlements1, entitlements2);
    }

    @Test
    void testAllWrappersAreDifferentInstances() {
        // Test
        Claims claims = auth.claims();
        Permissions permissions = auth.permissions();
        Roles roles = auth.roles();
        FeatureFlags featureFlags = auth.featureFlags();
        Entitlements entitlements = auth.entitlements();

        // Verify - each wrapper should be a different instance
        assertNotSame(claims, permissions);
        assertNotSame(claims, roles);
        assertNotSame(claims, featureFlags);
        assertNotSame(claims, entitlements);
        assertNotSame(permissions, roles);
        assertNotSame(permissions, featureFlags);
        assertNotSame(permissions, entitlements);
        assertNotSame(roles, featureFlags);
        assertNotSame(roles, entitlements);
        assertNotSame(featureFlags, entitlements);
    }
}
