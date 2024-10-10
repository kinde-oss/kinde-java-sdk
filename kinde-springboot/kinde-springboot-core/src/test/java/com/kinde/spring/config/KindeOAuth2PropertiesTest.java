package com.kinde.spring.config;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;

public class KindeOAuth2PropertiesTest {

    @Test
    public void testKindeOAuth2Properties() {
        OAuth2ClientProperties clientProperties = Mockito.mock(OAuth2ClientProperties.class);
        KindeOAuth2Properties kindeOAuth2Properties = new KindeOAuth2Properties(clientProperties);

        Assert.assertNull(kindeOAuth2Properties.getRedirectUri());
        Assert.assertNull(kindeOAuth2Properties.getClientId());
        Assert.assertNull(kindeOAuth2Properties.getClientSecret());
        Assert.assertNull(kindeOAuth2Properties.getAuthorizationGrantType());
        Assert.assertNull(kindeOAuth2Properties.getDomain());
        Assert.assertEquals("api://default",kindeOAuth2Properties.getAudience());
        Assert.assertNull(kindeOAuth2Properties.getPostLogoutRedirectUri());
        Assert.assertNull(kindeOAuth2Properties.getProxy());
        Assert.assertNull(kindeOAuth2Properties.getScopes());

        Assert.assertNotNull(kindeOAuth2Properties.getPermissionsClaim());

        kindeOAuth2Properties.setRedirectUri("https://kinde.com");
        Assert.assertEquals("https://kinde.com",kindeOAuth2Properties.getRedirectUri());

        kindeOAuth2Properties.setClientId("client-id");
        Assert.assertEquals("client-id",kindeOAuth2Properties.getClientId());

        kindeOAuth2Properties.setClientSecret("client-secret");
        Assert.assertEquals("client-secret",kindeOAuth2Properties.getClientSecret());

        kindeOAuth2Properties.setDomain("https://kinde.com");
        Assert.assertEquals("https://kinde.com",kindeOAuth2Properties.getDomain());

        kindeOAuth2Properties.setAudience("https://kinde.com/api");
        Assert.assertEquals("https://kinde.com/api",kindeOAuth2Properties.getAudience());
    }


}
