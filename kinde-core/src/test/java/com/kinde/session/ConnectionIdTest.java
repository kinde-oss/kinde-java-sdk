package com.kinde.session;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.client.KindeCoreGuiceTestModule;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import com.kinde.token.KindeTokenGuiceTestModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionIdTest {

    @BeforeEach
    public void setUp() {
        KindeGuiceSingleton.fin();
        KindeEnvironmentSingleton.fin();
        KindeEnvironmentSingleton.init(KindeEnvironmentSingleton.State.ACTIVE);
        
        KindeGuiceSingleton.init(
                new KindeCoreGuiceTestModule(),
                new KindeTokenGuiceTestModule());
    }

    @Test
    @DisplayName("authorizationUrlWithParameters should include connection_id when provided")
    public void testAuthorizationUrlWithConnectionId() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put(KindeRequestParameters.CONNECTION_ID, "conn_123456789");
        
        AuthorizationUrl authorizationUrl = kindeClientSession.authorizationUrlWithParameters(parameters);
        
        assertNotNull(authorizationUrl);
        assertNotNull(authorizationUrl.getUrl());
        String urlString = authorizationUrl.getUrl().toString();
        assertTrue(urlString.contains("connection_id=conn_123456789"), 
                "URL should contain connection_id parameter. URL: " + urlString);
    }

    @Test
    @DisplayName("login should support connection_id via authorizationUrlWithParameters")
    public void testLoginWithConnectionId() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("supports_reauth", "true");
        parameters.put(KindeRequestParameters.CONNECTION_ID, "conn_social_google");
        
        AuthorizationUrl authorizationUrl = kindeClientSession.authorizationUrlWithParameters(parameters);
        
        assertNotNull(authorizationUrl);
        assertNotNull(authorizationUrl.getUrl());
        String urlString = authorizationUrl.getUrl().toString();
        assertTrue(urlString.contains("connection_id=conn_social_google"), 
                "URL should contain connection_id parameter. URL: " + urlString);
        assertTrue(urlString.contains("supports_reauth=true"), 
                "URL should contain supports_reauth parameter. URL: " + urlString);
    }

    @Test
    @DisplayName("register should support connection_id via authorizationUrlWithParameters")
    public void testRegisterWithConnectionId() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("prompt", "create");
        parameters.put(KindeRequestParameters.CONNECTION_ID, "conn_enterprise_saml");
        
        AuthorizationUrl authorizationUrl = kindeClientSession.authorizationUrlWithParameters(parameters);
        
        assertNotNull(authorizationUrl);
        assertNotNull(authorizationUrl.getUrl());
        String urlString = authorizationUrl.getUrl().toString();
        assertTrue(urlString.contains("connection_id=conn_enterprise_saml"), 
                "URL should contain connection_id parameter. URL: " + urlString);
        assertTrue(urlString.contains("prompt=create"), 
                "URL should contain prompt parameter. URL: " + urlString);
    }

    @Test
    @DisplayName("connection_id should work with CODE grant type")
    public void testConnectionIdWithCodeGrant() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .grantType(AuthorizationType.CODE)
                .build();
        
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put(KindeRequestParameters.CONNECTION_ID, "conn_123456789");
        
        AuthorizationUrl authorizationUrl = kindeClientSession.authorizationUrlWithParameters(parameters);
        
        assertNotNull(authorizationUrl);
        assertNotNull(authorizationUrl.getUrl());
        assertNotNull(authorizationUrl.getCodeVerifier(), "Code verifier should be present for CODE grant type");
        String urlString = authorizationUrl.getUrl().toString();
        assertTrue(urlString.contains("connection_id=conn_123456789"), 
                "URL should contain connection_id parameter. URL: " + urlString);
    }

    @Test
    @DisplayName("connection_id should work with other parameters like org_code and lang")
    public void testConnectionIdWithOtherParameters() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .orgCode("ORG123")
                .lang("en")
                .build();
        
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put(KindeRequestParameters.CONNECTION_ID, "conn_123456789");
        
        AuthorizationUrl authorizationUrl = kindeClientSession.authorizationUrlWithParameters(parameters);
        
        assertNotNull(authorizationUrl);
        assertNotNull(authorizationUrl.getUrl());
        String urlString = authorizationUrl.getUrl().toString();
        assertTrue(urlString.contains("connection_id=conn_123456789"), 
                "URL should contain connection_id parameter. URL: " + urlString);
        assertTrue(urlString.contains("org_code=ORG123"), 
                "URL should contain org_code parameter. URL: " + urlString);
        assertTrue(urlString.contains("lang=en"), 
                "URL should contain lang parameter. URL: " + urlString);
    }
}
