package com.kinde.token;

import com.kinde.token.jwt.JwtGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionIdTokenTest {

    @Test
    @DisplayName("getConnectionId should return connection_id from token when present as direct claim")
    public void testGetConnectionIdDirectClaim() throws Exception {
        String connectionId = "conn_123456789";
        String tokenString = JwtGenerator.generateIDTokenWithConnectionId(connectionId);
        
        KindeToken kindeToken = IDToken.init(tokenString, true);
        
        assertNotNull(kindeToken);
        assertTrue(kindeToken.valid());
        assertEquals(connectionId, kindeToken.getConnectionId(), 
                "getConnectionId() should return the connection_id from the token");
    }

    @Test
    @DisplayName("getConnectionId should return connection_id from ext_provider nested structure")
    public void testGetConnectionIdFromExtProvider() throws Exception {
        String connectionId = "conn_enterprise_saml_789";
        String tokenString = JwtGenerator.generateIDTokenWithExtProviderConnectionId(connectionId);
        
        KindeToken kindeToken = IDToken.init(tokenString, true);
        
        assertNotNull(kindeToken);
        assertTrue(kindeToken.valid());
        assertEquals(connectionId, kindeToken.getConnectionId(), 
                "getConnectionId() should return the connection_id from ext_provider.connection_id");
    }

    @Test
    @DisplayName("getConnectionId should return null when connection_id is not present")
    public void testGetConnectionIdWhenNotPresent() throws Exception {
        String tokenString = JwtGenerator.generateIDToken();
        
        KindeToken kindeToken = IDToken.init(tokenString, true);
        
        assertNotNull(kindeToken);
        assertTrue(kindeToken.valid());
        assertNull(kindeToken.getConnectionId(), 
                "getConnectionId() should return null when connection_id is not in the token");
    }

    @Test
    @DisplayName("getConnectionId should prefer direct connection_id over nested ext_provider.connection_id")
    public void testGetConnectionIdPreferDirectOverNested() throws Exception {
        // Create a token with both direct and nested connection_id to test preference
        String directConnectionId = "conn_direct_123";
        String nestedConnectionId = "conn_nested_456";
        
        String tokenString = JwtGenerator.generateIDTokenWithBothConnectionIds(directConnectionId, nestedConnectionId);
        
        KindeToken kindeToken = IDToken.init(tokenString, true);
        
        assertNotNull(kindeToken);
        assertTrue(kindeToken.valid());
        assertEquals(directConnectionId, kindeToken.getConnectionId(), 
                "getConnectionId() should prefer direct connection_id over ext_provider.connection_id");
    }

    @Test
    @DisplayName("getConnectionId should work with AccessToken")
    public void testGetConnectionIdWithAccessToken() throws Exception {
        String connectionId = "conn_access_token_123";
        String tokenString = JwtGenerator.generateIDTokenWithConnectionId(connectionId);
        
        // AccessToken uses the same BaseToken implementation
        KindeToken kindeToken = AccessToken.init(tokenString, true);
        
        assertNotNull(kindeToken);
        assertTrue(kindeToken.valid());
        assertEquals(connectionId, kindeToken.getConnectionId(), 
                "getConnectionId() should work with AccessToken");
    }

    @Test
    @DisplayName("getConnectionId should return null for invalid token")
    public void testGetConnectionIdWithInvalidToken() throws Exception {
        String tokenString = "invalid.token.string";
        
        KindeToken kindeToken = IDToken.init(tokenString, false);
        
        assertNotNull(kindeToken);
        assertFalse(kindeToken.valid());
        assertNull(kindeToken.getConnectionId(), 
                "getConnectionId() should return null for invalid tokens");
    }

    @Test
    @DisplayName("getConnectionId should handle null ext_provider gracefully")
    public void testGetConnectionIdWithNullExtProvider() throws Exception {
        // Token without ext_provider should work fine
        String tokenString = JwtGenerator.generateIDToken();
        
        KindeToken kindeToken = IDToken.init(tokenString, true);
        
        assertNotNull(kindeToken);
        assertTrue(kindeToken.valid());
        assertNull(kindeToken.getConnectionId(), 
                "getConnectionId() should handle missing ext_provider gracefully");
    }

    @Test
    @DisplayName("getConnectionId should handle ext_provider without connection_id gracefully")
    public void testGetConnectionIdWithExtProviderButNoConnectionId() throws Exception {
        // This test verifies that if ext_provider exists but doesn't have connection_id, it returns null
        String tokenString = JwtGenerator.generateIDTokenWithExtProviderButNoConnectionId();

        KindeToken kindeToken = IDToken.init(tokenString, true);

        assertNotNull(kindeToken);
        assertTrue(kindeToken.valid());
        assertNull(kindeToken.getConnectionId(), 
                "getConnectionId() should return null when ext_provider exists but has no connection_id");
    }
}
