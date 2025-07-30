package com.kinde.accounts;

import com.kinde.core.KindeClient;
import com.kinde.core.session.KindeClientSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KindeAccountsClientBuilderTest {

    @Mock
    private KindeClient mockKindeClient;
    
    @Mock
    private KindeClientSession mockSession;
    
    @Test
    void testDefaultConstructor() {
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder();
        assertNotNull(builder);
    }
    
    @Test
    void testWithKindeClient() {
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder();
        KindeAccountsClientBuilder result = builder.withKindeClient(mockKindeClient);
        
        assertSame(builder, result);
    }
    
    @Test
    void testWithSession() {
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder();
        KindeAccountsClientBuilder result = builder.withSession(mockSession);
        
        assertSame(builder, result);
    }
    
    @Test
    void testBuildWithKindeClient() {
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder()
                .withKindeClient(mockKindeClient);
        
        KindeAccountsClient client = builder.build();
        
        assertNotNull(client);
    }
    
    @Test
    void testBuildWithSession() {
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder()
                .withSession(mockSession);
        
        KindeAccountsClient client = builder.build();
        
        assertNotNull(client);
    }
    
    @Test
    void testBuildWithBothKindeClientAndSession() {
        // When both are provided, KindeClient should take precedence
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder()
                .withKindeClient(mockKindeClient)
                .withSession(mockSession);
        
        KindeAccountsClient client = builder.build();
        
        assertNotNull(client);
    }
    
    @Test
    void testBuildWithNeitherKindeClientNorSession() {
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            builder.build();
        });
        
        assertEquals("Either KindeClient or KindeClientSession must be provided", exception.getMessage());
    }
    
    @Test
    void testBuilderFluentInterface() {
        KindeAccountsClient client = new KindeAccountsClientBuilder()
                .withKindeClient(mockKindeClient)
                .build();
        
        assertNotNull(client);
    }
    
    @Test
    void testBuilderWithNullKindeClient() {
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder()
                .withKindeClient(null);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            builder.build();
        });
        
        assertEquals("Either KindeClient or KindeClientSession must be provided", exception.getMessage());
    }
    
    @Test
    void testBuilderWithNullSession() {
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder()
                .withSession(null);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            builder.build();
        });
        
        assertEquals("Either KindeClient or KindeClientSession must be provided", exception.getMessage());
    }
    
    @Test
    void testBuilderChaining() {
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder();
        
        // Test that chaining works correctly
        KindeAccountsClientBuilder chained = builder
                .withKindeClient(mockKindeClient)
                .withSession(mockSession); // This should not affect the build since KindeClient is set first
        
        assertSame(builder, chained);
        
        KindeAccountsClient client = chained.build();
        assertNotNull(client);
    }
    
    @Test
    void testMultipleBuildCalls() {
        KindeAccountsClientBuilder builder = new KindeAccountsClientBuilder()
                .withKindeClient(mockKindeClient);
        
        // Should be able to build multiple clients from the same builder
        KindeAccountsClient client1 = builder.build();
        KindeAccountsClient client2 = builder.build();
        
        assertNotNull(client1);
        assertNotNull(client2);
        assertNotSame(client1, client2); // Should be different instances
    }
} 