package com.kinde.spring.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientProperties;
import org.springframework.validation.Errors;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KindeOAuth2PropertiesTest {

    @Test
    void bindsLocalOAuth2PropertiesAndValidatesSupportedTypes() {
        OAuth2ClientProperties clientProperties = mock(OAuth2ClientProperties.class);
        when(clientProperties.getRegistration()).thenReturn(Map.of());
        KindeOAuth2Properties properties = new KindeOAuth2Properties(clientProperties);

        properties.setClientId("client");
        properties.setClientSecret("secret");
        properties.setAuthorizationGrantType("client_credentials");
        properties.setDomain("https://example.kinde.com");
        properties.setAudience("api://example");
        properties.setPermissionsClaim("permissions");
        properties.setScopes(Set.of("openid", "profile"));
        properties.setRedirectUri("https://app.example/callback");
        properties.setPostLogoutRedirectUri("https://app.example");

        assertEquals("client", properties.getClientId());
        assertEquals("secret", properties.getClientSecret());
        assertEquals("client_credentials", properties.getAuthorizationGrantType());
        assertEquals("https://example.kinde.com", properties.getDomain());
        assertEquals("api://example", properties.getAudience());
        assertEquals("permissions", properties.getPermissionsClaim());
        assertEquals(Set.of("openid", "profile"), properties.getScopes());
        assertEquals("https://app.example/callback", properties.getRedirectUri());
        assertEquals("https://app.example", properties.getPostLogoutRedirectUri());
        assertTrue(properties.supports(KindeOAuth2Properties.class));
        assertFalse(properties.supports(Object.class));

        Errors errors = mock(Errors.class);
        assertDoesNotThrow(() -> properties.validate(properties, errors));

        KindeOAuth2Properties defaults = new KindeOAuth2Properties(null);
        assertEquals("permissions", defaults.getPermissionsClaim());
        assertNull(defaults.getAudience());
    }

    @Test
    void proxyPropertiesExposeConfiguredValues() {
        KindeOAuth2Properties.Proxy proxy = new KindeOAuth2Properties.Proxy();
        proxy.setHost("proxy.example");
        proxy.setPort(8080);
        proxy.setUsername("user");
        proxy.setPassword("password");

        assertEquals("proxy.example", proxy.getHost());
        assertEquals(8080, proxy.getPort());
        assertEquals("user", proxy.getUsername());
        assertEquals("password", proxy.getPassword());

        KindeOAuth2Properties properties = new KindeOAuth2Properties(null);
        properties.setProxy(proxy);
        assertSame(proxy, properties.getProxy());
    }
}
