package com.kinde.session;

import com.kinde.authorization.AuthorizationUrl;
import com.kinde.config.KindeConfig;
import com.kinde.exceptions.KindeClientSessionException;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeTokens;
import com.kinde.client.OidcMetaData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KindeClientSessionImplGeneratePortalUrlTest {
    @Test
    @DisplayName("generatePortalUrl returns AuthorizationUrl for valid input")
    void generatePortalUrlReturnsAuthorizationUrlForValidInput() throws Exception {
        KindeConfig mockConfig = mock(KindeConfig.class);
        OidcMetaData mockMeta = mock(OidcMetaData.class);
        KindeClientSessionImpl kindeClient = Mockito.spy(new KindeClientSessionImpl(mockConfig, mockMeta));
        KindeTokens tokens = mock(KindeTokens.class);
        AccessToken accessToken = mock(AccessToken.class);
        doReturn(tokens).when(kindeClient).retrieveTokens();
        when(tokens.getAccessToken()).thenReturn(accessToken);
        when(accessToken.token()).thenReturn("token123");
        HttpURLConnection conn = mock(HttpURLConnection.class);
        when(conn.getResponseCode()).thenReturn(200);
        when(conn.getInputStream()).thenReturn(new ByteArrayInputStream("{\"url\":\"https://portal.kinde.com/user\"}".getBytes()));
        Mockito.doReturn(conn).when(kindeClient).openConnection(any(URL.class));
        AuthorizationUrl result = kindeClient.generatePortalUrl("https://example.kinde.com", "https://myapp.com/return", "profile");
        assertNotNull(result);
        assertEquals("https://portal.kinde.com/user", result.getUrl().toString());
    }

    @Test
    @DisplayName("generatePortalUrl throws for non-absolute returnUrl")
    void generatePortalUrlThrowsForNonAbsoluteReturnUrl() {
        KindeClientSessionImpl kindeClient = new KindeClientSessionImpl(mock(KindeConfig.class), mock(OidcMetaData.class));
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                kindeClient.generatePortalUrl("https://example.kinde.com", "/relative", "profile")
        );
        assertTrue(ex.getMessage().contains("returnUrl must be an absolute URL"));
    }

    @Test
    @DisplayName("generatePortalUrl throws if access token is missing")
    void generatePortalUrlThrowsIfAccessTokenMissing() {
        KindeClientSessionImpl kindeClient = Mockito.spy(new KindeClientSessionImpl(mock(KindeConfig.class), mock(OidcMetaData.class)));
        doReturn(null).when(kindeClient).retrieveTokens();
        Exception ex = assertThrows(IllegalStateException.class, () ->
                kindeClient.generatePortalUrl("https://example.kinde.com", "https://myapp.com/return", "profile")
        );
        assertTrue(ex.getMessage().contains("Access Token not found"));
    }

    @Test
    @DisplayName("generatePortalUrl throws if API response is not 200")
    void generatePortalUrlThrowsIfApiResponseNot200() throws Exception {
        KindeClientSessionImpl kindeClient = Mockito.spy(new KindeClientSessionImpl(mock(KindeConfig.class), mock(OidcMetaData.class)));
        KindeTokens tokens = mock(KindeTokens.class);
        AccessToken accessToken = mock(AccessToken.class);
        doReturn(tokens).when(kindeClient).retrieveTokens();
        when(tokens.getAccessToken()).thenReturn(accessToken);
        when(accessToken.token()).thenReturn("token123");
        HttpURLConnection conn = mock(HttpURLConnection.class);
        when(conn.getResponseCode()).thenReturn(500);
        when(conn.getResponseMessage()).thenReturn("Internal Server Error");
        Mockito.doReturn(conn).when(kindeClient).openConnection(any(URL.class));
        KindeClientSessionException ex = assertThrows(KindeClientSessionException.class, () ->
                kindeClient.generatePortalUrl("https://example.kinde.com", "https://myapp.com/return", "profile")
        );
        assertTrue(ex.getMessage().contains("Failed to fetch profile URL"));
    }

    @Test
    @DisplayName("generatePortalUrl throws if API returns invalid JSON")
    void generatePortalUrlThrowsIfApiReturnsInvalidJson() throws Exception {
        KindeClientSessionImpl kindeClient = Mockito.spy(new KindeClientSessionImpl(mock(KindeConfig.class), mock(OidcMetaData.class)));
        KindeTokens tokens = mock(KindeTokens.class);
        AccessToken accessToken = mock(AccessToken.class);
        doReturn(tokens).when(kindeClient).retrieveTokens();
        when(tokens.getAccessToken()).thenReturn(accessToken);
        when(accessToken.token()).thenReturn("token123");
        HttpURLConnection conn = mock(HttpURLConnection.class);
        when(conn.getResponseCode()).thenReturn(200);
        when(conn.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        Mockito.doReturn(conn).when(kindeClient).openConnection(any(URL.class));
        KindeClientSessionException ex = assertThrows(KindeClientSessionException.class, () ->
                kindeClient.generatePortalUrl("https://example.kinde.com", "https://myapp.com/return", "profile")
        );
        assertTrue(ex.getMessage().contains("Failed to extract portal URL from response"));
    }

    @Test
    @DisplayName("generatePortalUrl throws if API returns malformed URL")
    void generatePortalUrlThrowsIfApiReturnsMalformedUrl() throws Exception {
        KindeClientSessionImpl kindeClient = Mockito.spy(new KindeClientSessionImpl(mock(KindeConfig.class), mock(OidcMetaData.class)));
        KindeTokens tokens = mock(KindeTokens.class);
        AccessToken accessToken = mock(AccessToken.class);
        doReturn(tokens).when(kindeClient).retrieveTokens();
        when(tokens.getAccessToken()).thenReturn(accessToken);
        when(accessToken.token()).thenReturn("token123");
        HttpURLConnection conn = mock(HttpURLConnection.class);
        when(conn.getResponseCode()).thenReturn(200);
        when(conn.getInputStream()).thenReturn(new ByteArrayInputStream("{\"url\":\"ht!tp://bad-url\"}".getBytes()));
        Mockito.doReturn(conn).when(kindeClient).openConnection(any(URL.class));
        MalformedURLException ex = assertThrows(MalformedURLException.class, () ->
                kindeClient.generatePortalUrl("https://example.kinde.com", "https://myapp.com/return", "profile")
        );
        assertTrue(ex.getMessage().contains("no protocol: ht!tp://bad-url"));
    }
}

