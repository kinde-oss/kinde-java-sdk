package com.kinde.accounts.example;

import com.kinde.accounts.KindeAccountsClient;
import com.kinde.accounts.KindeAccountsClientBuilder;
import org.openapitools.client.model.*;
import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountsExampleTest {

    @Mock
    private KindeClient mockKindeClient;
    
    @Mock
    private KindeClientSession mockSession;
    
    @Mock
    private KindeAccountsClient mockAccountsClient;
    
    private AccountsExample accountsExample;
    
    @BeforeEach
    void setUp() {
        accountsExample = new AccountsExample();
    }
    
    @Test
    void testKindeAccountsClientBuilder() {
        // Test the builder pattern
        KindeAccountsClient client = new KindeAccountsClientBuilder()
                .withKindeClient(mockKindeClient)
                .build();
        
        assertNotNull(client);
    }
    
    @Test
    void testKindeAccountsClientBuilderWithSession() {
        // Test the builder pattern with session
        KindeAccountsClient client = new KindeAccountsClientBuilder()
                .withSession(mockSession)
                .build();
        
        assertNotNull(client);
    }
    
    @Test
    void testBuilderWithBothKindeClientAndSession() {
        // Test that KindeClient takes precedence when both are provided
        KindeAccountsClient client = new KindeAccountsClientBuilder()
                .withKindeClient(mockKindeClient)
                .withSession(mockSession)
                .build();
        
        assertNotNull(client);
    }
    
    @Test
    void testBuilderWithNeitherKindeClientNorSession() {
        // Test that an exception is thrown when neither is provided
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            builder.build();
        });
        
        assertEquals("Either KindeClient or KindeClientSession must be provided", exception.getMessage());
    }
    
    @Test
    void testGetEntitlementsExample() throws ExecutionException, InterruptedException {
        // Create mock response
        EntitlementsResponse mockResponse = new EntitlementsResponse();
        mockResponse.setOrgCode("test-org");
        
        Plan plan1 = new Plan();
        plan1.setId("plan-1");
        plan1.setName("Basic Plan");
        
        Plan plan2 = new Plan();
        plan2.setId("plan-2");
        plan2.setName("Pro Plan");
        
        Entitlement entitlement1 = new Entitlement();
        entitlement1.setKey("feature_a");
        entitlement1.setValue(true);
        
        Entitlement entitlement2 = new Entitlement();
        entitlement2.setKey("feature_b");
        entitlement2.setValue(false);
        
        mockResponse.setPlans(Arrays.asList(plan1, plan2));
        mockResponse.setEntitlements(Arrays.asList(entitlement1, entitlement2));
        
        // Mock the client
        when(mockAccountsClient.getEntitlements()).thenReturn(CompletableFuture.completedFuture(mockResponse));
        
        // Test the method
        EntitlementsResponse result = mockAccountsClient.getEntitlements().get();
        
        assertNotNull(result);
        assertEquals("test-org", result.getOrgCode());
        assertEquals(2, result.getPlans().size());
        assertEquals(2, result.getEntitlements().size());
    }
    
    @Test
    void testGetPermissionsExample() throws ExecutionException, InterruptedException {
        // Create mock response
        PermissionsResponse mockResponse = new PermissionsResponse();
        Metadata metadata = new Metadata();
        metadata.setHasMore(false);
        mockResponse.setMetadata(metadata);
        
        Permission permission1 = new Permission();
        permission1.setKey("read:users");
        permission1.setName("Read Users");
        
        Permission permission2 = new Permission();
        permission2.setKey("write:users");
        permission2.setName("Write Users");
        
        mockResponse.setData(Arrays.asList(permission1, permission2));
        
        // Mock the client
        when(mockAccountsClient.getPermissions()).thenReturn(CompletableFuture.completedFuture(mockResponse));
        
        // Test the method
        PermissionsResponse result = mockAccountsClient.getPermissions().get();
        
        assertNotNull(result);
        assertFalse(result.getMetadata().getHasMore());
        assertEquals(2, result.getData().size());
    }
    
    @Test
    void testGetRolesExample() throws ExecutionException, InterruptedException {
        // Create mock response
        RolesResponse mockResponse = new RolesResponse();
        Metadata metadata = new Metadata();
        metadata.setHasMore(false);
        mockResponse.setMetadata(metadata);
        
        Role role1 = new Role();
        role1.setKey("admin");
        role1.setName("Administrator");
        
        Role role2 = new Role();
        role2.setKey("user");
        role2.setName("User");
        
        mockResponse.setData(Arrays.asList(role1, role2));
        
        // Mock the client
        when(mockAccountsClient.getRoles()).thenReturn(CompletableFuture.completedFuture(mockResponse));
        
        // Test the method
        RolesResponse result = mockAccountsClient.getRoles().get();
        
        assertNotNull(result);
        assertFalse(result.getMetadata().getHasMore());
        assertEquals(2, result.getData().size());
    }
    
    @Test
    void testGetFeatureFlagsExample() throws ExecutionException, InterruptedException {
        // Create mock response
        FeatureFlagsResponse mockResponse = new FeatureFlagsResponse();
        Metadata metadata = new Metadata();
        metadata.setHasMore(false);
        mockResponse.setMetadata(metadata);
        
        FeatureFlagsData flagsData = new FeatureFlagsData();
        FeatureFlag flag1 = new FeatureFlag();
        flag1.setKey("new_ui");
        flag1.setValue(true);
        
        FeatureFlag flag2 = new FeatureFlag();
        flag2.setKey("beta_features");
        flag2.setValue(false);
        
        flagsData.setFeatureFlags(Arrays.asList(flag1, flag2));
        mockResponse.setData(flagsData);
        
        // Mock the client
        when(mockAccountsClient.getFeatureFlags()).thenReturn(CompletableFuture.completedFuture(mockResponse));
        
        // Test the method
        FeatureFlagsResponse result = mockAccountsClient.getFeatureFlags().get();
        
        assertNotNull(result);
        assertFalse(result.getMetadata().getHasMore());
        assertEquals(2, result.getData().getFeatureFlags().size());
    }
    
    @Test
    void testHasPermissionExample() throws ExecutionException, InterruptedException {
        // Mock the client to return true for hasPermission
        when(mockAccountsClient.hasPermission("read:users")).thenReturn(CompletableFuture.completedFuture(true));
        when(mockAccountsClient.hasPermission("write:users")).thenReturn(CompletableFuture.completedFuture(false));
        
        // Test the methods
        Boolean hasReadPermission = mockAccountsClient.hasPermission("read:users").get();
        Boolean hasWritePermission = mockAccountsClient.hasPermission("write:users").get();
        
        assertTrue(hasReadPermission);
        assertFalse(hasWritePermission);
    }
    
    @Test
    void testHasAnyPermissionExample() throws ExecutionException, InterruptedException {
        List<String> permissions = Arrays.asList("read:users", "write:users", "delete:users");
        
        // Mock the client to return true for hasAnyPermission
        when(mockAccountsClient.hasAnyPermission(permissions)).thenReturn(CompletableFuture.completedFuture(true));
        
        // Test the method
        Boolean hasAnyPermission = mockAccountsClient.hasAnyPermission(permissions).get();
        
        assertTrue(hasAnyPermission);
    }
    
    @Test
    void testHasAllPermissionsExample() throws ExecutionException, InterruptedException {
        List<String> permissions = Arrays.asList("read:users", "write:users");
        
        // Mock the client to return true for hasAllPermissions
        when(mockAccountsClient.hasAllPermissions(permissions)).thenReturn(CompletableFuture.completedFuture(true));
        
        // Test the method
        Boolean hasAllPermissions = mockAccountsClient.hasAllPermissions(permissions).get();
        
        assertTrue(hasAllPermissions);
    }
    
    @Test
    void testHasRoleExample() throws ExecutionException, InterruptedException {
        // Mock the client to return true for hasRole
        when(mockAccountsClient.hasRole("admin")).thenReturn(CompletableFuture.completedFuture(true));
        when(mockAccountsClient.hasRole("user")).thenReturn(CompletableFuture.completedFuture(false));
        
        // Test the methods
        Boolean hasAdminRole = mockAccountsClient.hasRole("admin").get();
        Boolean hasUserRole = mockAccountsClient.hasRole("user").get();
        
        assertTrue(hasAdminRole);
        assertFalse(hasUserRole);
    }
    
    @Test
    void testHasAnyRoleExample() throws ExecutionException, InterruptedException {
        List<String> roles = Arrays.asList("admin", "moderator", "user");
        
        // Mock the client to return true for hasAnyRole
        when(mockAccountsClient.hasAnyRole(roles)).thenReturn(CompletableFuture.completedFuture(true));
        
        // Test the method
        Boolean hasAnyRole = mockAccountsClient.hasAnyRole(roles).get();
        
        assertTrue(hasAnyRole);
    }
    
    @Test
    void testHasAllRolesExample() throws ExecutionException, InterruptedException {
        List<String> roles = Arrays.asList("admin", "moderator");
        
        // Mock the client to return true for hasAllRoles
        when(mockAccountsClient.hasAllRoles(roles)).thenReturn(CompletableFuture.completedFuture(true));
        
        // Test the method
        Boolean hasAllRoles = mockAccountsClient.hasAllRoles(roles).get();
        
        assertTrue(hasAllRoles);
    }
    
    @Test
    void testGetFeatureFlagValueExample() throws ExecutionException, InterruptedException {
        // Mock the client to return a feature flag value
        when(mockAccountsClient.getFeatureFlagValue("new_ui")).thenReturn(CompletableFuture.completedFuture(true));
        when(mockAccountsClient.getFeatureFlagValue("max_users")).thenReturn(CompletableFuture.completedFuture(100));
        
        // Test the methods
        Object newUiValue = mockAccountsClient.getFeatureFlagValue("new_ui").get();
        Object maxUsersValue = mockAccountsClient.getFeatureFlagValue("max_users").get();
        
        assertEquals(true, newUiValue);
        assertEquals(100, maxUsersValue);
    }
    
    @Test
    void testIsFeatureFlagEnabledExample() throws ExecutionException, InterruptedException {
        // Mock the client to return boolean values for feature flags
        when(mockAccountsClient.isFeatureFlagEnabled("new_ui")).thenReturn(CompletableFuture.completedFuture(true));
        when(mockAccountsClient.isFeatureFlagEnabled("beta_features")).thenReturn(CompletableFuture.completedFuture(false));
        
        // Test the methods
        Boolean newUiEnabled = mockAccountsClient.isFeatureFlagEnabled("new_ui").get();
        Boolean betaFeaturesEnabled = mockAccountsClient.isFeatureFlagEnabled("beta_features").get();
        
        assertTrue(newUiEnabled);
        assertFalse(betaFeaturesEnabled);
    }
    
    @Test
    void testGetUserProfileExample() throws ExecutionException, InterruptedException {
        // Create mock response
        UserProfileResponse mockResponse = new UserProfileResponse();
        UserProfile profile = new UserProfile();
        profile.setId("user-123");
        profile.setEmail("test@example.com");
        profile.setGivenName("John");
        profile.setFamilyName("Doe");
        mockResponse.setData(profile);
        
        // Mock the client
        when(mockAccountsClient.getUserProfile()).thenReturn(CompletableFuture.completedFuture(mockResponse));
        
        // Test the method
        UserProfileResponse result = mockAccountsClient.getUserProfile().get();
        
        assertNotNull(result);
        assertEquals("user-123", result.getData().getId());
        assertEquals("test@example.com", result.getData().getEmail());
        assertEquals("John", result.getData().getGivenName());
        assertEquals("Doe", result.getData().getFamilyName());
    }
    
    @Test
    void testGetCurrentOrganizationExample() throws ExecutionException, InterruptedException {
        // Create mock response
        CurrentOrganizationResponse mockResponse = new CurrentOrganizationResponse();
        Organization org = new Organization();
        org.setCode("test-org");
        org.setName("Test Organization");
        mockResponse.setData(org);
        
        // Mock the client
        when(mockAccountsClient.getCurrentOrganization()).thenReturn(CompletableFuture.completedFuture(mockResponse));
        
        // Test the method
        CurrentOrganizationResponse result = mockAccountsClient.getCurrentOrganization().get();
        
        assertNotNull(result);
        assertEquals("test-org", result.getData().getCode());
        assertEquals("Test Organization", result.getData().getName());
    }
    
    @Test
    void testGetUserOrganizationsExample() throws ExecutionException, InterruptedException {
        // Create mock response
        UserOrganizationsResponse mockResponse = new UserOrganizationsResponse();
        Metadata metadata = new Metadata();
        metadata.setHasMore(false);
        mockResponse.setMetadata(metadata);
        
        Organization org1 = new Organization();
        org1.setCode("org-1");
        org1.setName("Organization 1");
        
        Organization org2 = new Organization();
        org2.setCode("org-2");
        org2.setName("Organization 2");
        
        mockResponse.setData(Arrays.asList(org1, org2));
        
        // Mock the client
        when(mockAccountsClient.getUserOrganizations()).thenReturn(CompletableFuture.completedFuture(mockResponse));
        
        // Test the method
        UserOrganizationsResponse result = mockAccountsClient.getUserOrganizations().get();
        
        assertNotNull(result);
        assertFalse(result.getMetadata().getHasMore());
        assertEquals(2, result.getData().size());
    }
} 