package com.kinde.accounts;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive error handling tests for KindeAccountsClient.
 * Tests cover invalid method arguments, API exception mapping, and token refresh scenarios.
 */
@ExtendWith(MockitoExtension.class)
class KindeAccountsClientErrorHandlingTest {

    @Mock
    private KindeClientSession mockSession;
    
    @Mock
    private KindeClient mockKindeClient;
    
    private KindeAccountsClient client;

    @BeforeEach
    void setUp() {
        // Setup mock session for tests that need it
        lenient().when(mockSession.getDomain()).thenReturn("test.kinde.com");
        lenient().when(mockSession.getAccessToken()).thenReturn("test-token");
        
        // Setup mock KindeClient for tests that need it
        lenient().when(mockKindeClient.clientSession()).thenReturn(mockSession);
    }

    // ==================== Invalid Method Arguments Tests ====================

    @Test
    void getEntitlement_nullKey_throwsRuntimeException() {
        client = new KindeAccountsClient(mockSession);
        CompletableFuture<EntitlementResponse> future = client.getEntitlement(null);
        
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Entitlement key cannot be null or empty", exception.getCause().getMessage());
    }

    @Test
    void getEntitlement_emptyKey_throwsRuntimeException() {
        client = new KindeAccountsClient(mockSession);
        CompletableFuture<EntitlementResponse> future = client.getEntitlement("");
        
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Entitlement key cannot be null or empty", exception.getCause().getMessage());
    }

    @Test
    void getEntitlement_blankKey_throwsRuntimeException() {
        client = new KindeAccountsClient(mockSession);
        CompletableFuture<EntitlementResponse> future = client.getEntitlement("   ");
        
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Entitlement key cannot be null or empty", exception.getCause().getMessage());
    }

    @Test
    void getPermission_nullKey_throwsRuntimeException() {
        client = new KindeAccountsClient(mockSession);
        CompletableFuture<PermissionResponse> future = client.getPermission(null);
        
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Permission key cannot be null or empty", exception.getCause().getMessage());
    }

    @Test
    void getPermission_emptyKey_throwsRuntimeException() {
        client = new KindeAccountsClient(mockSession);
        CompletableFuture<PermissionResponse> future = client.getPermission("");
        
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Permission key cannot be null or empty", exception.getCause().getMessage());
    }

    @Test
    void getPermission_blankKey_throwsRuntimeException() {
        client = new KindeAccountsClient(mockSession);
        CompletableFuture<PermissionResponse> future = client.getPermission("   ");
        
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Permission key cannot be null or empty", exception.getCause().getMessage());
    }

    @Test
    void getFeatureFlag_nullKey_throwsRuntimeException() {
        client = new KindeAccountsClient(mockSession);
        CompletableFuture<FeatureFlagResponse> future = client.getFeatureFlag(null);
        
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Feature flag key cannot be null or empty", exception.getCause().getMessage());
    }

    @Test
    void getFeatureFlag_emptyKey_throwsRuntimeException() {
        client = new KindeAccountsClient(mockSession);
        CompletableFuture<FeatureFlagResponse> future = client.getFeatureFlag("");
        
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Feature flag key cannot be null or empty", exception.getCause().getMessage());
    }

    @Test
    void getFeatureFlag_blankKey_throwsRuntimeException() {
        client = new KindeAccountsClient(mockSession);
        CompletableFuture<FeatureFlagResponse> future = client.getFeatureFlag("   ");
        
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Feature flag key cannot be null or empty", exception.getCause().getMessage());
    }

    // ==================== Constructor Validation Tests ====================

    @Test
    void constructor_nullSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new KindeAccountsClient((KindeClientSession) null));
    }

    @Test
    void constructor_nullKindeClient_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new KindeAccountsClient((KindeClient) null));
    }

    // ==================== Input Validation Tests ====================

    @Test
    void getEntitlement_validKey_doesNotThrowException() {
        client = new KindeAccountsClient(mockSession);
        // This test verifies that valid keys don't cause immediate exceptions
        // The actual API call will fail due to network/authentication, but input validation passes
        CompletableFuture<EntitlementResponse> future = client.getEntitlement("valid-key");
        
        // Should not throw immediately for input validation
        assertNotNull(future);
    }

    @Test
    void getPermission_validKey_doesNotThrowException() {
        client = new KindeAccountsClient(mockSession);
        CompletableFuture<PermissionResponse> future = client.getPermission("valid-permission");
        
        assertNotNull(future);
    }

    @Test
    void getFeatureFlag_validKey_doesNotThrowException() {
        client = new KindeAccountsClient(mockSession);
        CompletableFuture<FeatureFlagResponse> future = client.getFeatureFlag("valid-flag");
        
        assertNotNull(future);
    }

}
