package com.kinde.session;

import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.exceptions.KindeClientSessionException;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeTokens;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KindeClientSessionImplTest {

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
        when(conn.getInputStream()).thenReturn(new ByteArrayInputStream("{\"url\":\"https://portal.kinde.com/user\"}".getBytes(StandardCharsets.UTF_8)));
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
        when(conn.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
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
        when(conn.getInputStream()).thenReturn(new ByteArrayInputStream("{\"url\":\"ht!tp://bad-url\"}".getBytes(StandardCharsets.UTF_8)));
        Mockito.doReturn(conn).when(kindeClient).openConnection(any(URL.class));
        MalformedURLException ex = assertThrows(MalformedURLException.class, () ->
                kindeClient.generatePortalUrl("https://example.kinde.com", "https://myapp.com/return", "profile")
        );
    }

    private KindeClientSessionImpl createSessionWithOidc() throws Exception {
        KindeConfig mockConfig = mock(KindeConfig.class);
        when(mockConfig.clientId()).thenReturn("test-client-id");
        when(mockConfig.redirectUri()).thenReturn("http://localhost/callback");
        when(mockConfig.grantType()).thenReturn(AuthorizationType.CODE);
        when(mockConfig.scopes()).thenReturn(List.of("openid", "email"));

        OidcMetaData mockMeta = mock(OidcMetaData.class);
        OIDCProviderMetadata metadata = mock(OIDCProviderMetadata.class);
        when(metadata.getAuthorizationEndpointURI()).thenReturn(URI.create("http://localhost:8089/oauth2/auth"));
        when(mockMeta.getOpMetadata()).thenReturn(metadata);

        return new KindeClientSessionImpl(mockConfig, mockMeta);
    }

    @Test
    @DisplayName("login with invitationCode includes both login and invitation params")
    void loginWithInvitationCodeIncludesBothParams() throws Exception {
        KindeClientSessionImpl session = createSessionWithOidc();
        AuthorizationUrl result = session.login("inv_login123");

        assertNotNull(result);
        String url = result.getUrl().toString();
        assertTrue(url.contains("invitation_code=inv_login123"), "URL should contain invitation_code");
        assertTrue(url.contains("is_invitation=true"), "URL should contain is_invitation=true");
        assertTrue(url.contains("supports_reauth=true"), "URL should contain login-specific supports_reauth");
    }

    @Test
    @DisplayName("login without invitationCode does not include invitation params")
    void loginWithoutInvitationCodeOmitsInvitationParams() throws Exception {
        KindeClientSessionImpl session = createSessionWithOidc();
        AuthorizationUrl result = session.login();

        assertNotNull(result);
        String url = result.getUrl().toString();
        assertFalse(url.contains("invitation_code"), "URL should not contain invitation_code");
        assertFalse(url.contains("is_invitation"), "URL should not contain is_invitation");
        assertTrue(url.contains("supports_reauth=true"), "URL should still contain supports_reauth");
    }

    @Test
    @DisplayName("register with invitationCode includes both register and invitation params")
    void registerWithInvitationCodeIncludesBothParams() throws Exception {
        KindeClientSessionImpl session = createSessionWithOidc();
        AuthorizationUrl result = session.register("inv_reg456");

        assertNotNull(result);
        String url = result.getUrl().toString();
        assertTrue(url.contains("invitation_code=inv_reg456"), "URL should contain invitation_code");
        assertTrue(url.contains("is_invitation=true"), "URL should contain is_invitation=true");
        assertTrue(url.contains("prompt=create"), "URL should contain register-specific prompt=create");
    }

    @Test
    @DisplayName("createOrg with invitationCode includes both createOrg and invitation params")
    void createOrgWithInvitationCodeIncludesBothParams() throws Exception {
        KindeClientSessionImpl session = createSessionWithOidc();
        AuthorizationUrl result = session.createOrg("TestOrg", "inv_org789");

        assertNotNull(result);
        String url = result.getUrl().toString();
        assertTrue(url.contains("invitation_code=inv_org789"), "URL should contain invitation_code");
        assertTrue(url.contains("is_invitation=true"), "URL should contain is_invitation=true");
        assertTrue(url.contains("org_name=TestOrg"), "URL should contain org_name");
        assertTrue(url.contains("is_create_org=true"), "URL should contain is_create_org");
    }

    @Test
    @DisplayName("handleInvitation with code includes invitation params")
    void handleInvitationWithCodeIncludesInvitationParams() throws Exception {
        KindeClientSessionImpl session = createSessionWithOidc();
        AuthorizationUrl result = session.handleInvitation("inv_handle123");

        assertNotNull(result);
        String url = result.getUrl().toString();
        assertTrue(url.contains("invitation_code=inv_handle123"), "URL should contain invitation_code");
        assertTrue(url.contains("is_invitation=true"), "URL should contain is_invitation=true");
    }

    @Test
    @DisplayName("handleInvitation with null code omits invitation params")
    void handleInvitationWithNullCodeOmitsInvitationParams() throws Exception {
        KindeClientSessionImpl session = createSessionWithOidc();
        AuthorizationUrl result = session.handleInvitation(null);

        assertNotNull(result);
        String url = result.getUrl().toString();
        assertFalse(url.contains("invitation_code"), "URL should not contain invitation_code");
        assertFalse(url.contains("is_invitation"), "URL should not contain is_invitation");
    }

    @Test
    @DisplayName("handleInvitation with empty code omits invitation params")
    void handleInvitationWithEmptyCodeOmitsInvitationParams() throws Exception {
        KindeClientSessionImpl session = createSessionWithOidc();
        AuthorizationUrl result = session.handleInvitation("");

        assertNotNull(result);
        String url = result.getUrl().toString();
        assertFalse(url.contains("invitation_code"), "URL should not contain invitation_code");
        assertFalse(url.contains("is_invitation"), "URL should not contain is_invitation");
    }

    @Test
    @DisplayName("register without args omits invitation params")
    void registerWithoutArgsOmitsInvitationParams() throws Exception {
        KindeClientSessionImpl session = createSessionWithOidc();
        AuthorizationUrl result = session.register();

        assertNotNull(result);
        String url = result.getUrl().toString();
        assertFalse(url.contains("invitation_code"), "URL should not contain invitation_code");
        assertFalse(url.contains("is_invitation"), "URL should not contain is_invitation");
        assertTrue(url.contains("prompt=create"), "URL should contain register-specific prompt=create");
    }

    @Test
    @DisplayName("createOrg without invitation code omits invitation params")
    void createOrgWithoutInvitationCodeOmitsInvitationParams() throws Exception {
        KindeClientSessionImpl session = createSessionWithOidc();
        AuthorizationUrl result = session.createOrg("TestOrg");

        assertNotNull(result);
        String url = result.getUrl().toString();
        assertFalse(url.contains("invitation_code"), "URL should not contain invitation_code");
        assertFalse(url.contains("is_invitation"), "URL should not contain is_invitation");
        assertTrue(url.contains("org_name=TestOrg"), "URL should contain org_name");
        assertTrue(url.contains("is_create_org=true"), "URL should contain is_create_org");
    }
}

