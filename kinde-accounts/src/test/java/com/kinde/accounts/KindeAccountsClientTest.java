package com.kinde.accounts;

import com.kinde.accounts.api.DefaultApi;
import com.kinde.accounts.model.*;
import com.kinde.core.KindeClient;
import com.kinde.core.session.KindeClientSession;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KindeAccountsClientTest {

    @Mock
    private KindeClient mockKindeClient;
    
    @Mock
    private KindeClientSession mockSession;
    
    @Mock
    private DefaultApi mockApiClient;
    
    private KindeAccountsClient client;
    
    @BeforeEach
    void setUp() {
        // Setup mock session
        when(mockSession.getDomain()).thenReturn("https://test.kinde.com");
        when(mockSession.getAccessToken()).thenReturn("test-access-token");
        
        // Setup mock KindeClient
        when(mockKindeClient.getSession()).thenReturn(mockSession);
        
        // Create client with session
        client = new KindeAccountsClient(mockSession);
    }
    
    @Test
    void testConstructorWithKindeClient() {
        KindeAccountsClient clientWithKindeClient = new KindeAccountsClient(mockKindeClient);
        assertNotNull(clientWithKindeClient);
    }
    
    @Test
    void testConstructorWithSession() {
        KindeAccountsClient clientWithSession = new KindeAccountsClient(mockSession);
        assertNotNull(clientWithSession);
    }
    
    @Test
    void testGetEntitlements() throws ExecutionException, InterruptedException {
        // Create mock response
        EntitlementsResponse mockResponse = new EntitlementsResponse();
        mockResponse.setOrgCode("test-org");
        mockResponse.setPlans(Arrays.asList());
        mockResponse.setEntitlements(Arrays.asList());
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getEntitlements()).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<EntitlementsResponse> future = client.getEntitlements();
            EntitlementsResponse result = future.get();
            
            assertNotNull(result);
            assertEquals("test-org", result.getOrgCode());
        }
    }
    
    @Test
    void testGetEntitlement() throws ExecutionException, InterruptedException {
        // Create mock response
        EntitlementResponse mockResponse = new EntitlementResponse();
        Entitlement entitlement = new Entitlement();
        entitlement.setKey("test-entitlement");
        entitlement.setValue(true);
        mockResponse.setData(entitlement);
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getEntitlement("test-key")).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<EntitlementResponse> future = client.getEntitlement("test-key");
            EntitlementResponse result = future.get();
            
            assertNotNull(result);
            assertEquals("test-entitlement", result.getData().getKey());
        }
    }
    
    @Test
    void testGetPermissions() throws ExecutionException, InterruptedException {
        // Create mock response
        PermissionsResponse mockResponse = new PermissionsResponse();
        Metadata metadata = new Metadata();
        metadata.setHasMore(false);
        mockResponse.setMetadata(metadata);
        mockResponse.setData(Arrays.asList());
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getPermissions()).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<PermissionsResponse> future = client.getPermissions();
            PermissionsResponse result = future.get();
            
            assertNotNull(result);
            assertFalse(result.getMetadata().getHasMore());
        }
    }
    
    @Test
    void testGetPermission() throws ExecutionException, InterruptedException {
        // Create mock response
        PermissionResponse mockResponse = new PermissionResponse();
        Permission permission = new Permission();
        permission.setKey("test-permission");
        permission.setName("Test Permission");
        mockResponse.setData(permission);
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getPermission("test-key")).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<PermissionResponse> future = client.getPermission("test-key");
            PermissionResponse result = future.get();
            
            assertNotNull(result);
            assertEquals("test-permission", result.getData().getKey());
        }
    }
    
    @Test
    void testGetRoles() throws ExecutionException, InterruptedException {
        // Create mock response
        RolesResponse mockResponse = new RolesResponse();
        Metadata metadata = new Metadata();
        metadata.setHasMore(false);
        mockResponse.setMetadata(metadata);
        mockResponse.setData(Arrays.asList());
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getRoles()).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<RolesResponse> future = client.getRoles();
            RolesResponse result = future.get();
            
            assertNotNull(result);
            assertFalse(result.getMetadata().getHasMore());
        }
    }
    
    @Test
    void testGetFeatureFlags() throws ExecutionException, InterruptedException {
        // Create mock response
        FeatureFlagsResponse mockResponse = new FeatureFlagsResponse();
        Metadata metadata = new Metadata();
        metadata.setHasMore(false);
        mockResponse.setMetadata(metadata);
        mockResponse.setData(new FeatureFlagsData());
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getFeatureFlags()).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<FeatureFlagsResponse> future = client.getFeatureFlags();
            FeatureFlagsResponse result = future.get();
            
            assertNotNull(result);
            assertFalse(result.getMetadata().getHasMore());
        }
    }
    
    @Test
    void testGetFeatureFlag() throws ExecutionException, InterruptedException {
        // Create mock response
        FeatureFlagResponse mockResponse = new FeatureFlagResponse();
        FeatureFlag flag = new FeatureFlag();
        flag.setKey("test-flag");
        flag.setValue(true);
        mockResponse.setData(flag);
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getFeatureFlag("test-key")).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<FeatureFlagResponse> future = client.getFeatureFlag("test-key");
            FeatureFlagResponse result = future.get();
            
            assertNotNull(result);
            assertEquals("test-flag", result.getData().getKey());
        }
    }
    
    @Test
    void testGetUserOrganizations() throws ExecutionException, InterruptedException {
        // Create mock response
        UserOrganizationsResponse mockResponse = new UserOrganizationsResponse();
        Metadata metadata = new Metadata();
        metadata.setHasMore(false);
        mockResponse.setMetadata(metadata);
        mockResponse.setData(Arrays.asList());
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getUserOrganizations()).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<UserOrganizationsResponse> future = client.getUserOrganizations();
            UserOrganizationsResponse result = future.get();
            
            assertNotNull(result);
            assertFalse(result.getMetadata().getHasMore());
        }
    }
    
    @Test
    void testGetUserProfile() throws ExecutionException, InterruptedException {
        // Create mock response
        UserProfileResponse mockResponse = new UserProfileResponse();
        UserProfile profile = new UserProfile();
        profile.setId("user-123");
        profile.setEmail("test@example.com");
        mockResponse.setData(profile);
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getUserProfile()).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<UserProfileResponse> future = client.getUserProfile();
            UserProfileResponse result = future.get();
            
            assertNotNull(result);
            assertEquals("user-123", result.getData().getId());
        }
    }
    
    @Test
    void testGetCurrentOrganization() throws ExecutionException, InterruptedException {
        // Create mock response
        CurrentOrganizationResponse mockResponse = new CurrentOrganizationResponse();
        Organization org = new Organization();
        org.setCode("test-org");
        org.setName("Test Organization");
        mockResponse.setData(org);
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getCurrentOrganization()).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<CurrentOrganizationResponse> future = client.getCurrentOrganization();
            CurrentOrganizationResponse result = future.get();
            
            assertNotNull(result);
            assertEquals("test-org", result.getData().getCode());
        }
    }
    
    @Test
    void testHasPermission_True() throws ExecutionException, InterruptedException {
        // Create mock response with permission
        PermissionsResponse mockResponse = new PermissionsResponse();
        Permission permission = new Permission();
        permission.setKey("test-permission");
        mockResponse.setData(Arrays.asList(permission));
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getPermissions()).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<Boolean> future = client.hasPermission("test-permission");
            Boolean result = future.get();
            
            assertTrue(result);
        }
    }
    
    @Test
    void testHasPermission_False() throws ExecutionException, InterruptedException {
        // Create mock response without permission
        PermissionsResponse mockResponse = new PermissionsResponse();
        Permission permission = new Permission();
        permission.setKey("other-permission");
        mockResponse.setData(Arrays.asList(permission));
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getPermissions()).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<Boolean> future = client.hasPermission("test-permission");
            Boolean result = future.get();
            
            assertFalse(result);
        }
    }
    
    @Test
    void testHasAnyPermission_True() throws ExecutionException, InterruptedException {
        // Create mock response with one of the permissions
        PermissionsResponse mockResponse = new PermissionsResponse();
        Permission permission = new Permission();
        permission.setKey("permission-2");
        mockResponse.setData(Arrays.asList(permission));
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getPermissions()).thenReturn(mockResponse);
            
            // Test the method
            List<String> permissions = Arrays.asList("permission-1", "permission-2", "permission-3");
            CompletableFuture<Boolean> future = client.hasAnyPermission(permissions);
            Boolean result = future.get();
            
            assertTrue(result);
        }
    }
    
    @Test
    void testHasAllPermissions_True() throws ExecutionException, InterruptedException {
        // Create mock response with all permissions
        PermissionsResponse mockResponse = new PermissionsResponse();
        Permission perm1 = new Permission();
        perm1.setKey("permission-1");
        Permission perm2 = new Permission();
        perm2.setKey("permission-2");
        mockResponse.setData(Arrays.asList(perm1, perm2));
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getPermissions()).thenReturn(mockResponse);
            
            // Test the method
            List<String> permissions = Arrays.asList("permission-1", "permission-2");
            CompletableFuture<Boolean> future = client.hasAllPermissions(permissions);
            Boolean result = future.get();
            
            assertTrue(result);
        }
    }
    
    @Test
    void testHasAllPermissions_False() throws ExecutionException, InterruptedException {
        // Create mock response with only one permission
        PermissionsResponse mockResponse = new PermissionsResponse();
        Permission perm1 = new Permission();
        perm1.setKey("permission-1");
        mockResponse.setData(Arrays.asList(perm1));
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getPermissions()).thenReturn(mockResponse);
            
            // Test the method
            List<String> permissions = Arrays.asList("permission-1", "permission-2");
            CompletableFuture<Boolean> future = client.hasAllPermissions(permissions);
            Boolean result = future.get();
            
            assertFalse(result);
        }
    }
    
    @Test
    void testHasRole_True() throws ExecutionException, InterruptedException {
        // Create mock response with role
        RolesResponse mockResponse = new RolesResponse();
        Role role = new Role();
        role.setKey("test-role");
        mockResponse.setData(Arrays.asList(role));
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getRoles()).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<Boolean> future = client.hasRole("test-role");
            Boolean result = future.get();
            
            assertTrue(result);
        }
    }
    
    @Test
    void testHasAnyRole_True() throws ExecutionException, InterruptedException {
        // Create mock response with one of the roles
        RolesResponse mockResponse = new RolesResponse();
        Role role = new Role();
        role.setKey("role-2");
        mockResponse.setData(Arrays.asList(role));
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getRoles()).thenReturn(mockResponse);
            
            // Test the method
            List<String> roles = Arrays.asList("role-1", "role-2", "role-3");
            CompletableFuture<Boolean> future = client.hasAnyRole(roles);
            Boolean result = future.get();
            
            assertTrue(result);
        }
    }
    
    @Test
    void testHasAllRoles_True() throws ExecutionException, InterruptedException {
        // Create mock response with all roles
        RolesResponse mockResponse = new RolesResponse();
        Role role1 = new Role();
        role1.setKey("role-1");
        Role role2 = new Role();
        role2.setKey("role-2");
        mockResponse.setData(Arrays.asList(role1, role2));
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getRoles()).thenReturn(mockResponse);
            
            // Test the method
            List<String> roles = Arrays.asList("role-1", "role-2");
            CompletableFuture<Boolean> future = client.hasAllRoles(roles);
            Boolean result = future.get();
            
            assertTrue(result);
        }
    }
    
    @Test
    void testGetFeatureFlagValue() throws ExecutionException, InterruptedException {
        // Create mock response
        FeatureFlagResponse mockResponse = new FeatureFlagResponse();
        FeatureFlag flag = new FeatureFlag();
        flag.setKey("test-flag");
        flag.setValue("test-value");
        mockResponse.setData(flag);
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getFeatureFlag("test-flag")).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<Object> future = client.getFeatureFlagValue("test-flag");
            Object result = future.get();
            
            assertEquals("test-value", result);
        }
    }
    
    @Test
    void testIsFeatureFlagEnabled_True() throws ExecutionException, InterruptedException {
        // Create mock response with enabled flag
        FeatureFlagResponse mockResponse = new FeatureFlagResponse();
        FeatureFlag flag = new FeatureFlag();
        flag.setKey("test-flag");
        flag.setValue(true);
        mockResponse.setData(flag);
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getFeatureFlag("test-flag")).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<Boolean> future = client.isFeatureFlagEnabled("test-flag");
            Boolean result = future.get();
            
            assertTrue(result);
        }
    }
    
    @Test
    void testIsFeatureFlagEnabled_False() throws ExecutionException, InterruptedException {
        // Create mock response with disabled flag
        FeatureFlagResponse mockResponse = new FeatureFlagResponse();
        FeatureFlag flag = new FeatureFlag();
        flag.setKey("test-flag");
        flag.setValue(false);
        mockResponse.setData(flag);
        
        // Mock the API call
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getFeatureFlag("test-flag")).thenReturn(mockResponse);
            
            // Test the method
            CompletableFuture<Boolean> future = client.isFeatureFlagEnabled("test-flag");
            Boolean result = future.get();
            
            assertFalse(result);
        }
    }
    
    @Test
    void testApiExceptionHandling() {
        // Mock the API call to throw an exception
        try (MockedStatic<DefaultApi> mockedApi = mockStatic(DefaultApi.class)) {
            mockedApi.when(DefaultApi::new).thenReturn(mockApiClient);
            when(mockApiClient.getEntitlements()).thenThrow(new RuntimeException("API Error"));
            
            // Test that the exception is wrapped properly
            CompletableFuture<EntitlementsResponse> future = client.getEntitlements();
            
            ExecutionException exception = assertThrows(ExecutionException.class, () -> future.get());
            assertTrue(exception.getCause() instanceof RuntimeException);
            assertEquals("Failed to get entitlements", exception.getCause().getMessage());
        }
    }
} 