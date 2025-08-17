package com.kinde.token;

import com.kinde.KindeClientSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Basic integration tests for KindeTokenChecker functionality.
 * These tests focus on the core hard check logic with simplified mocking.
 */
class KindeTokenCheckerIntegrationTest {

    private KindeToken mockToken;
    private KindeClientSession mockSession;
    private KindeTokenChecker checker;

    @BeforeEach
    void setUp() {
        // Setup the checker with mocked dependencies
        mockToken = mock(KindeToken.class);
        mockSession = mock(KindeClientSession.class);
        checker = new KindeTokenChecker(mockToken, mockSession);
    }

    // ==================== BASIC FUNCTIONALITY TESTS ====================

    @Test
    void testHasPermission_WithTokenData_ReturnsTrue() {
        // Given: Token contains the required permission
        List<String> tokenPermissions = Arrays.asList("read:users", "write:users", "delete:users");
        when(mockToken.getPermissions()).thenReturn(tokenPermissions);

        // When: Check for permission that exists in token
        CompletableFuture<Boolean> future = checker.hasPermission("read:users");
        boolean result = future.join();

        // Then: Should return true
        assertTrue(result);
    }

    @Test
    void testHasPermission_WithTokenData_ReturnsFalse() {
        // Given: Token contains permissions but not the required one
        List<String> tokenPermissions = Arrays.asList("read:users", "write:users");
        when(mockToken.getPermissions()).thenReturn(tokenPermissions);

        // When: Check for permission that doesn't exist in token
        CompletableFuture<Boolean> future = checker.hasPermission("delete:users");
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    @Test
    void testHasPermission_WithEmptyTokenPermissions() {
        // Given: Token has empty permissions list
        when(mockToken.getPermissions()).thenReturn(Collections.emptyList());

        // When: Check for permission
        CompletableFuture<Boolean> future = checker.hasPermission("read:users");
        boolean result = future.join();

        // Then: Should return false (no permissions in token)
        assertFalse(result);
    }

    @Test
    void testHasPermission_WithNullTokenPermissions() {
        // Given: Token has null permissions
        when(mockToken.getPermissions()).thenReturn(null);

        // When: Check for permission
        CompletableFuture<Boolean> future = checker.hasPermission("read:users");
        boolean result = future.join();

        // Then: Should return false (null permissions in token)
        assertFalse(result);
    }

    @Test
    void testHasAnyPermission_WithTokenData_ReturnsTrue() {
        // Given: Token contains one of the required permissions
        List<String> tokenPermissions = Arrays.asList("read:users", "write:users");
        when(mockToken.getPermissions()).thenReturn(tokenPermissions);

        // When: Check for any of multiple permissions
        CompletableFuture<Boolean> future = checker.hasAnyPermission(Arrays.asList("read:users", "admin:users"));
        boolean result = future.join();

        // Then: Should return true
        assertTrue(result);
    }

    @Test
    void testHasAnyPermission_WithTokenData_ReturnsFalse() {
        // Given: Token contains permissions but none of the required ones
        List<String> tokenPermissions = Arrays.asList("read:users", "write:users");
        when(mockToken.getPermissions()).thenReturn(tokenPermissions);

        // When: Check for any of multiple permissions
        CompletableFuture<Boolean> future = checker.hasAnyPermission(Arrays.asList("admin:users", "delete:users"));
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    @Test
    void testHasAllPermissions_WithTokenData_ReturnsTrue() {
        // Given: Token contains all required permissions
        List<String> tokenPermissions = Arrays.asList("read:users", "write:users", "delete:users");
        when(mockToken.getPermissions()).thenReturn(tokenPermissions);

        // When: Check for all permissions
        CompletableFuture<Boolean> future = checker.hasAllPermissions(Arrays.asList("read:users", "write:users"));
        boolean result = future.join();

        // Then: Should return true
        assertTrue(result);
    }

    @Test
    void testHasAllPermissions_WithTokenData_ReturnsFalse() {
        // Given: Token contains some but not all required permissions
        List<String> tokenPermissions = Arrays.asList("read:users", "write:users");
        when(mockToken.getPermissions()).thenReturn(tokenPermissions);

        // When: Check for all permissions
        CompletableFuture<Boolean> future = checker.hasAllPermissions(Arrays.asList("read:users", "write:users", "delete:users"));
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    // ==================== ROLE CHECK TESTS ====================

    @Test
    void testHasRole_WithTokenData_ReturnsTrue() {
        // Given: Token contains the required role
        List<String> tokenRoles = Arrays.asList("admin", "user", "moderator");
        when(mockToken.getRoles()).thenReturn(tokenRoles);

        // When: Check for role that exists in token
        CompletableFuture<Boolean> future = checker.hasRole("admin");
        boolean result = future.join();

        // Then: Should return true
        assertTrue(result);
    }

    @Test
    void testHasRole_WithTokenData_ReturnsFalse() {
        // Given: Token contains roles but not the required one
        List<String> tokenRoles = Arrays.asList("user", "moderator");
        when(mockToken.getRoles()).thenReturn(tokenRoles);

        // When: Check for role that doesn't exist in token
        CompletableFuture<Boolean> future = checker.hasRole("admin");
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    @Test
    void testHasAnyRole_WithTokenData_ReturnsTrue() {
        // Given: Token contains one of the required roles
        List<String> tokenRoles = Arrays.asList("admin", "user");
        when(mockToken.getRoles()).thenReturn(tokenRoles);

        // When: Check for any of multiple roles
        CompletableFuture<Boolean> future = checker.hasAnyRole(Arrays.asList("admin", "superuser"));
        boolean result = future.join();

        // Then: Should return true
        assertTrue(result);
    }

    @Test
    void testHasAllRoles_WithTokenData_ReturnsTrue() {
        // Given: Token contains all required roles
        List<String> tokenRoles = Arrays.asList("admin", "user", "moderator");
        when(mockToken.getRoles()).thenReturn(tokenRoles);

        // When: Check for all roles
        CompletableFuture<Boolean> future = checker.hasAllRoles(Arrays.asList("admin", "user"));
        boolean result = future.join();

        // Then: Should return true
        assertTrue(result);
    }

    @Test
    void testHasRole_WithFallbackToHasuraRoles_ReturnsTrue() {
        // Given: Token has Hasura-compatible roles
        List<String> hasuraRoles = Arrays.asList("admin", "user", "moderator");
        when(mockToken.getRoles()).thenReturn(hasuraRoles);

        // When: Check for role that exists in Hasura roles
        CompletableFuture<Boolean> future = checker.hasRole("admin");
        boolean result = future.join();

        // Then: Should return true
        assertTrue(result);
    }

    @Test
    void testHasRole_WithNoRoles_ReturnsFalse() {
        // Given: Token contains no roles
        when(mockToken.getRoles()).thenReturn(null);

        // When: Check for any role
        CompletableFuture<Boolean> future = checker.hasRole("admin");
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    // ==================== FEATURE FLAG TESTS ====================

    @Test
    void testIsFeatureFlagEnabled_WithTokenData_ReturnsTrue() {
        // Given: Token contains the feature flag with boolean value
        when(mockToken.getBooleanFlag("new_ui")).thenReturn(true);
        when(mockToken.getBooleanFlag("beta_features")).thenReturn(false);

        // When: Check for enabled feature flag
        CompletableFuture<Boolean> future = checker.isFeatureFlagEnabled("new_ui");
        boolean result = future.join();

        // Then: Should return true
        assertTrue(result);
    }

    @Test
    void testIsFeatureFlagEnabled_WithTokenData_ReturnsFalse() {
        // Given: Token contains the feature flag with boolean value
        when(mockToken.getBooleanFlag("new_ui")).thenReturn(true);
        when(mockToken.getBooleanFlag("beta_features")).thenReturn(false);

        // When: Check for disabled feature flag
        CompletableFuture<Boolean> future = checker.isFeatureFlagEnabled("beta_features");
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    @Test
    void testGetFeatureFlagValue_WithTokenData_ReturnsValue() {
        // Given: Token contains the feature flag with string value
        Map<String, Object> tokenFlags = new HashMap<>();
        tokenFlags.put("theme", "dark");
        tokenFlags.put("max_items", 100);
        when(mockToken.getFlags()).thenReturn(tokenFlags);

        // When: Get feature flag value
        CompletableFuture<Object> future = checker.getFeatureFlagValue("theme");
        Object result = future.join();

        // Then: Should return value
        assertEquals("dark", result);
    }

    @Test
    void testGetFeatureFlagValue_WithTokenData_ReturnsNull() {
        // Given: Token contains feature flags but not the requested one
        Map<String, Object> tokenFlags = new HashMap<>();
        tokenFlags.put("theme", "dark");
        when(mockToken.getFlags()).thenReturn(tokenFlags);

        // When: Get feature flag value that doesn't exist
        CompletableFuture<Object> future = checker.getFeatureFlagValue("nonexistent_flag");
        Object result = future.join();

        // Then: Should return null
        assertNull(result);
    }

    // ==================== COMPREHENSIVE CHECK TESTS ====================

    @Test
    void testHasAll_WithAllRequirementsMet_ReturnsTrue() {
        // Given: Token contains all required permissions, roles, and feature flags
        when(mockToken.getPermissions()).thenReturn(Arrays.asList("read:users", "write:users"));
        when(mockToken.getRoles()).thenReturn(Arrays.asList("admin", "user"));
        when(mockToken.getBooleanFlag("new_ui")).thenReturn(true);

        // When: Check for all requirements
        CompletableFuture<Boolean> future = checker.hasAll(
                Arrays.asList("read:users", "write:users"),
                Arrays.asList("admin", "user"),
                Arrays.asList("new_ui")
        );
        boolean result = future.join();

        // Then: Should return true
        assertTrue(result);
    }

    @Test
    void testHasAll_WithSomeRequirementsMissing_ReturnsFalse() {
        // Given: Token contains some but not all required permissions
        when(mockToken.getPermissions()).thenReturn(Arrays.asList("read:users"));
        when(mockToken.getClaim("roles")).thenReturn(Arrays.asList("admin"));
        Map<String, Object> tokenFlags = new HashMap<>();
        tokenFlags.put("new_ui", true);
        when(mockToken.getFlags()).thenReturn(tokenFlags);

        // When: Check for all requirements (missing write:users permission)
        CompletableFuture<Boolean> future = checker.hasAll(
                Arrays.asList("read:users", "write:users"),
                Arrays.asList("admin"),
                Arrays.asList("new_ui")
        );
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    @Test
    void testHasAny_WithSomeRequirementsMet_ReturnsTrue() {
        // Given: Token contains some of the required permissions, roles, and feature flags
        when(mockToken.getPermissions()).thenReturn(Arrays.asList("read:users"));
        when(mockToken.getRoles()).thenReturn(Arrays.asList("admin"));
        when(mockToken.getBooleanFlag("new_ui")).thenReturn(true);
        when(mockToken.getBooleanFlag("beta_features")).thenReturn(false);

        // When: Check for any requirements
        CompletableFuture<Boolean> future = checker.hasAny(
                Arrays.asList("read:users", "write:users"),
                Arrays.asList("admin", "superuser"),
                Arrays.asList("new_ui", "beta_features")
        );
        boolean result = future.join();

        // Then: Should return true
        assertTrue(result);
    }

    @Test
    void testHasAny_WithNoRequirementsMet_ReturnsFalse() {
        // Given: Token contains none of the required permissions, roles, or feature flags
        when(mockToken.getPermissions()).thenReturn(Arrays.asList("other:permission"));
        when(mockToken.getRoles()).thenReturn(Arrays.asList("other:role"));
        Map<String, Object> tokenFlags = new HashMap<>();
        tokenFlags.put("other_flag", true);
        when(mockToken.getFlags()).thenReturn(tokenFlags);

        // When: Check for any requirements
        CompletableFuture<Boolean> future = checker.hasAny(
                Arrays.asList("read:users", "write:users"),
                Arrays.asList("admin", "superuser"),
                Arrays.asList("new_ui", "beta_features")
        );
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    void testEdgeCase_EmptyPermissionKeys() {
        // Given: Empty permission keys list
        when(mockToken.getPermissions()).thenReturn(Arrays.asList("read:users"));

        // When: Check for empty permissions list
        CompletableFuture<Boolean> future = checker.hasAnyPermission(Collections.emptyList());
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    @Test
    void testEdgeCase_EmptyRoleKeys() {
        // Given: Empty role keys list
        when(mockToken.getRoles()).thenReturn(Arrays.asList("admin"));

        // When: Check for empty roles list
        CompletableFuture<Boolean> future = checker.hasAnyRole(Collections.emptyList());
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    @Test
    void testEdgeCase_NullPermissionKeys() {
        // Given: Null permission keys list
        when(mockToken.getPermissions()).thenReturn(Arrays.asList("read:users"));

        // When: Check for null permissions list
        CompletableFuture<Boolean> future = checker.hasAnyPermission(null);
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    @Test
    void testEdgeCase_NullRoleKeys() {
        // Given: Null role keys list
        when(mockToken.getRoles()).thenReturn(Arrays.asList("admin"));

        // When: Check for null roles list
        CompletableFuture<Boolean> future = checker.hasAnyRole(null);
        boolean result = future.join();

        // Then: Should return false
        assertFalse(result);
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    void testErrorHandling_WhenTokenThrowsException() {
        // Given: Token throws exception when accessing permissions
        when(mockToken.getPermissions()).thenThrow(new RuntimeException("Invalid token"));

        // When: Check for permission
        CompletableFuture<Boolean> future = checker.hasPermission("read:users");
        boolean result = future.join();

        // Then: Should return false due to token error (falls back to API)
        assertFalse(result);
    }

    @Test
    void testErrorHandling_WhenTokenThrowsExceptionForRoles() {
        // Given: Token throws exception when accessing roles
        when(mockToken.getRoles()).thenThrow(new RuntimeException("Invalid token"));

        // When: Check for role
        CompletableFuture<Boolean> future = checker.hasRole("admin");
        boolean result = future.join();

        // Then: Should return false due to token error (falls back to API)
        assertFalse(result);
    }

    @Test
    void testErrorHandling_WhenTokenThrowsExceptionForFlags() {
        // Given: Token throws exception when accessing boolean flag
        when(mockToken.getBooleanFlag("new_ui")).thenThrow(new RuntimeException("Invalid token"));

        // When: Check for feature flag
        CompletableFuture<Boolean> future = checker.isFeatureFlagEnabled("new_ui");
        boolean result = future.join();

        // Then: Should return false due to token error (falls back to API)
        assertFalse(result);
    }

    @Test
    void testErrorHandling_WhenTokenThrowsExceptionForFeatureFlagValue() {
        // Given: Token throws exception when accessing flags
        when(mockToken.getFlags()).thenThrow(new RuntimeException("Invalid token"));

        // When: Get feature flag value
        CompletableFuture<Object> future = checker.getFeatureFlagValue("new_ui");
        Object result = future.join();

        // Then: Should return null due to token error (falls back to API)
        assertNull(result);
    }
}
