package com.kinde.token;

import com.kinde.accounts.KindeAccountsClient;
import com.kinde.accounts.dto.PermissionDto;
import com.kinde.accounts.dto.RoleDto;
import com.kinde.accounts.dto.FeatureFlagDto;
import com.kinde.token.jwt.JwtGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class HardCheckTokenTest {

    @Mock
    private KindeAccountsClient mockAccountsClient;

    private KindeToken tokenWithHardCheck;
    private KindeToken tokenWithoutHardCheck;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a token with hard check functionality
        String tokenStr = JwtGenerator.generateAccessToken();
        tokenWithHardCheck = AccessToken.init(tokenStr, true, mockAccountsClient);
        
        // Create a token without hard check functionality
        tokenWithoutHardCheck = AccessToken.init(tokenStr, true);
    }

    @Test
    public void testHasPermission_WithTokenData() {
        // Test when permission is in token
        String tokenStr = JwtGenerator.generateAccessTokenWithPermissions(Arrays.asList("read:users", "write:users"));
        KindeToken token = AccessToken.init(tokenStr, true, mockAccountsClient);
        
        boolean result = token.hasPermission("read:users");
        assertTrue(result);
    }

    @Test
    public void testHasPermission_WithApiFallback() {
        // Test when permission is not in token but available via API
        PermissionDto permission = new PermissionDto();
        permission.setKey("read:users");
        permission.setName("Read Users");
        
        when(mockAccountsClient.getPermissions()).thenReturn(Arrays.asList(permission));
        
        boolean result = tokenWithHardCheck.hasPermission("read:users");
        assertTrue(result);
    }

    @Test
    public void testHasPermission_WithoutApiFallback() {
        // Test when permission is not in token and no API client available
        boolean result = tokenWithoutHardCheck.hasPermission("read:users");
        assertFalse(result);
    }

    @Test
    public void testHasAnyPermission_WithTokenData() {
        // Test when permissions are in token
        String tokenStr = JwtGenerator.generateAccessTokenWithPermissions(Arrays.asList("read:users", "write:users"));
        KindeToken token = AccessToken.init(tokenStr, true, mockAccountsClient);
        
        boolean result = token.hasAnyPermission(Arrays.asList("read:users", "delete:users"));
        assertTrue(result);
    }

    @Test
    public void testHasAllPermissions_WithTokenData() {
        // Test when all permissions are in token
        String tokenStr = JwtGenerator.generateAccessTokenWithPermissions(Arrays.asList("read:users", "write:users"));
        KindeToken token = AccessToken.init(tokenStr, true, mockAccountsClient);
        
        boolean result = token.hasAllPermissions(Arrays.asList("read:users", "write:users"));
        assertTrue(result);
    }

    @Test
    public void testHasRole_WithTokenData() {
        // Test when role is in token
        String tokenStr = JwtGenerator.generateAccessTokenWithRoles(Arrays.asList("admin", "user"));
        KindeToken token = AccessToken.init(tokenStr, true, mockAccountsClient);
        
        boolean result = token.hasRole("admin");
        assertTrue(result);
    }

    @Test
    public void testHasRole_WithApiFallback() {
        // Test when role is not in token but available via API
        RoleDto role = new RoleDto();
        role.setKey("admin");
        role.setName("Administrator");
        
        when(mockAccountsClient.getRoles()).thenReturn(Arrays.asList(role));
        
        boolean result = tokenWithHardCheck.hasRole("admin");
        assertTrue(result);
    }

    @Test
    public void testIsFeatureFlagEnabled_WithTokenData() {
        // Test when feature flag is in token
        String tokenStr = JwtGenerator.generateAccessTokenWithFeatureFlags("dark_mode", true);
        KindeToken token = AccessToken.init(tokenStr, true, mockAccountsClient);
        
        boolean result = token.isFeatureFlagEnabled("dark_mode");
        assertTrue(result);
    }

    @Test
    public void testIsFeatureFlagEnabled_WithApiFallback() {
        // Test when feature flag is not in token but available via API
        FeatureFlagDto flag = new FeatureFlagDto();
        flag.setKey("dark_mode");
        flag.setValue("true");
        
        when(mockAccountsClient.getFeatureFlag("dark_mode")).thenReturn(flag);
        
        boolean result = tokenWithHardCheck.isFeatureFlagEnabled("dark_mode");
        assertTrue(result);
    }

    @Test
    public void testGetFeatureFlagValue_WithTokenData() {
        // Test when feature flag value is in token
        String tokenStr = JwtGenerator.generateAccessTokenWithFeatureFlags("max_users", 100);
        KindeToken token = AccessToken.init(tokenStr, true, mockAccountsClient);
        
        Object result = token.getFeatureFlagValue("max_users");
        assertEquals(100L, result); // JSON numbers are parsed as Long
    }

    @Test
    public void testHasAll_ComprehensiveCheck() {
        // Test comprehensive check with all requirements
        String tokenStr = JwtGenerator.generateAccessTokenWithPermissionsAndRoles(
            Arrays.asList("read:users", "write:users"),
            Arrays.asList("admin")
        );
        KindeToken token = AccessToken.init(tokenStr, true, mockAccountsClient);
        
        // Mock feature flag API call
        FeatureFlagDto flag = new FeatureFlagDto();
        flag.setKey("advanced_analytics");
        flag.setValue("true");
        when(mockAccountsClient.getFeatureFlag("advanced_analytics")).thenReturn(flag);
        
        boolean result = token.hasAll(
            Arrays.asList("read:users", "write:users"),
            Arrays.asList("admin"),
            Arrays.asList("advanced_analytics")
        );
        assertTrue(result);
    }

    @Test
    public void testHasAny_ComprehensiveCheck() {
        // Test comprehensive check with any requirements
        String tokenStr = JwtGenerator.generateAccessTokenWithPermissionsAndRoles(
            Arrays.asList("read:users"),
            Arrays.asList("user")
        );
        KindeToken token = AccessToken.init(tokenStr, true, mockAccountsClient);
        
        // Mock feature flag API call
        FeatureFlagDto flag = new FeatureFlagDto();
        flag.setKey("beta_features");
        flag.setValue("true");
        when(mockAccountsClient.getFeatureFlag("beta_features")).thenReturn(flag);
        
        boolean result = token.hasAny(
            Arrays.asList("read:users", "write:users"),
            Arrays.asList("admin", "user"),
            Arrays.asList("beta_features", "advanced_analytics")
        );
        assertTrue(result);
    }

    @Test
    public void testNullAndEmptyInputs() {
        // Test null and empty inputs
        boolean nullPermission = tokenWithHardCheck.hasPermission(null);
        assertFalse(nullPermission);
        
        boolean emptyPermission = tokenWithHardCheck.hasPermission("");
        assertFalse(emptyPermission);
        
        boolean nullRole = tokenWithHardCheck.hasRole(null);
        assertFalse(nullRole);
        
        boolean emptyRole = tokenWithHardCheck.hasRole("");
        assertFalse(emptyRole);
        
        boolean nullFlag = tokenWithHardCheck.isFeatureFlagEnabled(null);
        assertFalse(nullFlag);
        
        boolean emptyFlag = tokenWithHardCheck.isFeatureFlagEnabled("");
        assertFalse(emptyFlag);
    }

    @Test
    public void testEmptyLists() {
        // Test empty lists
        boolean emptyPermissions = tokenWithHardCheck.hasAllPermissions(Arrays.asList());
        assertTrue(emptyPermissions);
        
        boolean emptyRoles = tokenWithHardCheck.hasAllRoles(Arrays.asList());
        assertTrue(emptyRoles);
        
        boolean nullPermissions = tokenWithHardCheck.hasAllPermissions(null);
        assertTrue(nullPermissions);
        
        boolean nullRoles = tokenWithHardCheck.hasAllRoles(null);
        assertTrue(nullRoles);
    }
}
