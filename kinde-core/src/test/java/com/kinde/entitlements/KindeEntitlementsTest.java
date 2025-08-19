package com.kinde.entitlements;

import com.kinde.KindeClientSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openapitools.client.model.EntitlementResponse;
import org.openapitools.client.model.EntitlementsResponse;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Test class for KindeEntitlements functionality.
 */
public class KindeEntitlementsTest {

    @Mock
    private KindeClientSession mockSession;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Setup mock behavior
        when(mockSession.getDomain()).thenReturn("https://test.kinde.com");
        when(mockSession.getAccessToken()).thenReturn("test-access-token");
    }

    @Test
    public void testEntitlementsInterface() {
        // Test that we can create an entitlements instance
        KindeEntitlements entitlements = new KindeEntitlementsImpl(mockSession);
        assertNotNull(entitlements);
    }

    @Test
    public void testGetEntitlements() {
        KindeEntitlements entitlements = new KindeEntitlementsImpl(mockSession);
        CompletableFuture<EntitlementsResponse> result = entitlements.getEntitlements();
        assertNotNull(result);
    }

    @Test
    public void testGetEntitlement() {
        KindeEntitlements entitlements = new KindeEntitlementsImpl(mockSession);
        CompletableFuture<EntitlementResponse> result = entitlements.getEntitlement("test-key");
        assertNotNull(result);
    }
} 