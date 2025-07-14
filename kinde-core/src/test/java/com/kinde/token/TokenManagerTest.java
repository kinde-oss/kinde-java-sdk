package com.kinde.token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenManagerTest {

    @Mock
    private AccessToken mockAccessToken;
    
    @Mock
    private RefreshToken mockRefreshToken;
    
    @Mock
    private KindeTokens mockTokens;
    
    private TokenManager tokenManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockTokens.getAccessToken()).thenReturn(mockAccessToken);
        when(mockTokens.getRefreshToken()).thenReturn(mockRefreshToken);
    }

    @Test
    void testNeedsRefresh_WhenTokenExpiresInFuture_ReturnsFalse() {
        // Arrange
        long futureExpiry = System.currentTimeMillis() / 1000L + 3600; // 1 hour from now
        when(mockAccessToken.getClaim("exp")).thenReturn(futureExpiry);
        
        tokenManager = new TokenManager(mockTokens, 5); // 5-minute buffer

        // Act
        boolean result = tokenManager.needsRefresh();

        // Assert
        assertFalse(result);
    }

    @Test
    void testNeedsRefresh_WhenTokenExpiresWithinBuffer_ReturnsTrue() {
        // Arrange
        long nearExpiry = System.currentTimeMillis() / 1000L + 180; // 3 minutes from now
        when(mockAccessToken.getClaim("exp")).thenReturn(nearExpiry);
        
        tokenManager = new TokenManager(mockTokens, 5); // 5-minute buffer

        // Act
        boolean result = tokenManager.needsRefresh();

        // Assert
        assertTrue(result);
    }

    @Test
    void testNeedsRefresh_WhenTokenAlreadyExpired_ReturnsTrue() {
        // Arrange
        long pastExpiry = System.currentTimeMillis() / 1000L - 3600; // 1 hour ago
        when(mockAccessToken.getClaim("exp")).thenReturn(pastExpiry);
        
        tokenManager = new TokenManager(mockTokens, 5);

        // Act
        boolean result = tokenManager.needsRefresh();

        // Assert
        assertTrue(result);
    }

    @Test
    void testNeedsRefresh_WhenNoExpiryClaim_ReturnsFalse() {
        // Arrange
        when(mockAccessToken.getClaim("exp")).thenReturn(null);
        
        tokenManager = new TokenManager(mockTokens, 5);

        // Act
        boolean result = tokenManager.needsRefresh();

        // Assert
        assertFalse(result);
    }

    @Test
    void testNeedsRefresh_WhenNoAccessToken_ReturnsFalse() {
        // Arrange
        when(mockTokens.getAccessToken()).thenReturn(null);
        
        tokenManager = new TokenManager(mockTokens, 5);

        // Act
        boolean result = tokenManager.needsRefresh();

        // Assert
        assertFalse(result);
    }

    @Test
    void testNeedsRefresh_WhenExpiryIsDate_ReturnsCorrectResult() {
        // Arrange
        java.util.Date futureDate = new java.util.Date(System.currentTimeMillis() + 3600000); // 1 hour from now
        when(mockAccessToken.getClaim("exp")).thenReturn(futureDate);
        
        tokenManager = new TokenManager(mockTokens, 5);

        // Act
        boolean result = tokenManager.needsRefresh();

        // Assert
        assertFalse(result);
    }

    @Test
    void testCanRefresh_WhenRefreshTokenAvailable_ReturnsTrue() {
        // Arrange
        when(mockRefreshToken.token()).thenReturn("refresh_token_value");
        
        tokenManager = new TokenManager(mockTokens, 5);

        // Act
        boolean result = tokenManager.canRefresh();

        // Assert
        assertTrue(result);
    }

    @Test
    void testCanRefresh_WhenNoRefreshToken_ReturnsFalse() {
        // Arrange
        when(mockTokens.getRefreshToken()).thenReturn(null);
        
        tokenManager = new TokenManager(mockTokens, 5);

        // Act
        boolean result = tokenManager.canRefresh();

        // Assert
        assertFalse(result);
    }

    @Test
    void testCanRefresh_WhenRefreshTokenIsNull_ReturnsFalse() {
        // Arrange
        when(mockRefreshToken.token()).thenReturn(null);
        
        tokenManager = new TokenManager(mockTokens, 5);

        // Act
        boolean result = tokenManager.canRefresh();

        // Assert
        assertFalse(result);
    }

    @Test
    void testCanRefresh_WhenNoTokens_ReturnsFalse() {
        // Arrange
        tokenManager = new TokenManager(null, 5);

        // Act
        boolean result = tokenManager.canRefresh();

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetTokens_ReturnsCorrectTokens() {
        // Arrange
        tokenManager = new TokenManager(mockTokens, 5);

        // Act
        KindeTokens result = tokenManager.getTokens();

        // Assert
        assertEquals(mockTokens, result);
    }

    @Test
    void testConstructor_WithValidParameters_CreatesInstance() {
        // Act
        tokenManager = new TokenManager(mockTokens, 10);

        // Assert
        assertNotNull(tokenManager);
        assertEquals(mockTokens, tokenManager.getTokens());
    }

    @Test
    void testConstructor_WithNullTokens_CreatesInstance() {
        // Act
        tokenManager = new TokenManager(null, 5);

        // Assert
        assertNotNull(tokenManager);
        assertNull(tokenManager.getTokens());
    }

    @Test
    void testNeedsRefresh_WithZeroBuffer_OnlyRefreshesWhenExpired() {
        // Arrange
        long futureExpiry = System.currentTimeMillis() / 1000L + 300; // 5 minutes from now
        when(mockAccessToken.getClaim("exp")).thenReturn(futureExpiry);
        
        tokenManager = new TokenManager(mockTokens, 0); // 0-minute buffer

        // Act
        boolean result = tokenManager.needsRefresh();

        // Assert
        assertFalse(result);
    }

    @Test
    void testNeedsRefresh_WithLargeBuffer_RefreshesEarly() {
        // Arrange
        long futureExpiry = System.currentTimeMillis() / 1000L + 3600; // 1 hour from now
        when(mockAccessToken.getClaim("exp")).thenReturn(futureExpiry);
        
        tokenManager = new TokenManager(mockTokens, 60); // 60-minute buffer

        // Act
        boolean result = tokenManager.needsRefresh();

        // Assert
        assertTrue(result); // Should refresh because 1 hour is within 60-minute buffer
    }
} 