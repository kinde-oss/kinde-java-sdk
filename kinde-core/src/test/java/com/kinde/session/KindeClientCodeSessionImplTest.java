package com.kinde.session;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeTokens;
import com.kinde.token.RefreshToken;
import com.kinde.token.jwt.JwtGenerator;
import com.kinde.user.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.*;


public class KindeClientCodeSessionImplTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        KindeGuiceSingleton.fin();
        KindeEnvironmentSingleton.fin();
        KindeEnvironmentSingleton.init(KindeEnvironmentSingleton.State.ACTIVE);
        wireMockServer = new WireMockServer(8089); // you can specify the port
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);

        // Mocking the token endpoint
        wireMockServer.stubFor(
                WireMock.post(urlPathMatching("/oauth/token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"access_token\":\"test-token\",\"token_type\":\"Bearer\",\"expires_in\":3600}")));
        wireMockServer.stubFor(
                WireMock.get(urlPathMatching("/.well-known/openid-configuration"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"issuer\": \"http://localhost:8089\", \"jwks_uri\": " +
                                        "\"http://localhost:8089/.well-known/jwks\", \"token_endpoint\": " +
                                        "\"http://localhost:8089/oauth2/token\", \"claims_supported\":" +
                                        " [\"aud\", \"exp\", \"iat\", \"iss\", \"sub\"], " +
                                        "\"scopes_supported\": [\"address\", \"email\", \"offline\", \"openid\", " +
                                        "\"phone\", \"profile\"], \"userinfo_endpoint\": \"http://localhost:8089/oauth2/v2/user_profile\", " +
                                        "\"revocation_endpoint\": \"http://localhost:8089/oauth2/revoke\", " +
                                        "\"end_session_endpoint\": \"http://localhost:8089/logout\", \"authorization_endpoint\": " +
                                        "\"http://localhost:8089/oauth2/auth\", \"introspection_endpoint\": " +
                                        "\"http://localhost:8089/oauth2/introspect\"," +
                                        " \"subject_types_supported\": [\"public\"], \"response_modes_supported\": " +
                                        "[\"form_post\", \"query\", \"fragment\"], \"response_types_supported\":" +
                                        " [\"code\", \"token\", \"id_token\", \"code token\", \"code id_token\", " +
                                        "\"id_token token\", \"code id_token token\"], \"request_uri_parameter_supported\": " +
                                        "false, \"code_challenge_methods_supported\": [\"S256\"], " +
                                        "\"id_token_signing_alg_values_supported\": [\"RS256\"], " +
                                        "\"token_endpoint_auth_methods_supported\": [\"client_secret_post\"]}")));
        wireMockServer.stubFor(
                WireMock.post(urlPathMatching("/oauth2/token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\n" +
                                        "  \"access_token\": \"" + JwtGenerator.generateAccessToken() + "\",\n" +
                                        "  \"token_type\": \"Bearer\",\n" +
                                        "  \"expires_in\": 3600,\n" +
                                        "  \"id_token\": \"" + JwtGenerator.generateIDToken() + "\",\n" +
                                        "  \"refresh_token\": \"" + JwtGenerator.refreshToken() + "\",\n" +
                                        "  \"scope\": \"openid profile email\"\n" +
                                        "}")));

        wireMockServer.stubFor(
                WireMock.get(urlPathMatching("/oauth2/v2/user_profile"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                                          {
                                                          "sub": "1234567890",
                                                          "name": "John Doe",
                                                          "given_name": "John",
                                                          "family_name": "Doe",
                                                          "email": "johndoe@example.com",
                                                          "email_verified": true,
                                                          "picture": "https://example.com/johndoe.jpg",
                                                          "locale": "en-US",
                                                          "updated_at": 1611693980
                                                        }
                                        """)));
        ///oauth2/token
        System.out.println("Instanciate the wiremock service");
    }


    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
        KindeGuiceSingleton.fin();
        KindeEnvironmentSingleton.fin();
    }

    @Test
    public void testAuthorizationUrlRequestTest() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        AuthorizationUrl authorizationUrl1 = kindeClientSession.authorizationUrl();
        assertNotNull(authorizationUrl1);
        assertNotNull(authorizationUrl1.getUrl());
        assertTrue(!authorizationUrl1.getUrl().toString().contains("prompt"));
        assertNull(authorizationUrl1.getCodeVerifier());

        KindeClient kindeClient2 = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .addScope("openid")
                .addAudience("http://localhost:8089/api")
                .grantType(AuthorizationType.CODE)
                .build();
        KindeClientSession kindeClientSession2 = kindeClient2.initClientSession("test", null);
        AuthorizationUrl authorizationUrl2 = kindeClientSession2.authorizationUrl();
        assertNotNull(authorizationUrl2);
        assertNotNull(authorizationUrl2.getUrl());
        assertTrue(!authorizationUrl1.getUrl().toString().contains("prompt"));
        assertNotNull(authorizationUrl2.getCodeVerifier());
    }

    @Test
    public void testLoginUrlRequestTest() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        AuthorizationUrl authorizationUrl1 = kindeClientSession.login();
        assertNotNull(authorizationUrl1);
        assertNotNull(authorizationUrl1.getUrl());
        assertTrue(!authorizationUrl1.getUrl().toString().contains("prompt"));
        assertNull(authorizationUrl1.getCodeVerifier());

        KindeClient kindeClient2 = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .addScope("openid")
                .addAudience("http://localhost:8089/api")
                .grantType(AuthorizationType.CODE)
                .build();
        KindeClientSession kindeClientSession2 = kindeClient2.initClientSession("test", null);
        AuthorizationUrl authorizationUrl2 = kindeClientSession2.login();
        assertNotNull(authorizationUrl2);
        assertNotNull(authorizationUrl2.getUrl());
        assertTrue(!authorizationUrl1.getUrl().toString().contains("prompt"));
        assertNotNull(authorizationUrl2.getCodeVerifier());
    }

    @Test
    public void testRegisterUrlRequestTest() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        AuthorizationUrl authorizationUrl1 = kindeClientSession.register("test1", "plan1");
        assertNotNull(authorizationUrl1);
        assertNotNull(authorizationUrl1.getUrl());
        System.out.println(authorizationUrl1.getUrl());
        assertTrue(authorizationUrl1.getUrl().toString().contains("prompt=create"));
        assertTrue(authorizationUrl1.getUrl().toString().contains("pricing_table_key=test1"));
        assertNull(authorizationUrl1.getCodeVerifier());

        KindeClient kindeClient2 = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .addScope("openid")
                .addAudience("http://localhost:8089/api")
                .grantType(AuthorizationType.CODE)
                .orgCode("TEST")
                .hasSuccessPage(Boolean.TRUE)
                .build();
        KindeClientSession kindeClientSession2 = kindeClient2.initClientSession("test", null);
        AuthorizationUrl authorizationUrl2 = kindeClientSession2.register("test1", "plan1");
        assertNotNull(authorizationUrl2);
        assertNotNull(authorizationUrl2.getUrl());
        System.out.println(authorizationUrl2.getUrl());
        assertTrue(authorizationUrl2.getUrl().toString().contains("prompt=create"));
        assertTrue(authorizationUrl2.getUrl().toString().contains("org_code=TEST"));
        assertTrue(authorizationUrl2.getUrl().toString().contains("has_success_page=true"));
        assertTrue(authorizationUrl1.getUrl().toString().contains("pricing_table_key=test1"));
        assertNotNull(authorizationUrl2.getCodeVerifier());
    }

    @Test
    public void testRegisterUrlRequestPricingTableKeyEmptyTestTest() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        AuthorizationUrl authorizationUrl1 = kindeClientSession.register("", "plan1");
        assertNotNull(authorizationUrl1);
        assertNotNull(authorizationUrl1.getUrl());
        System.out.println(authorizationUrl1.getUrl());
        assertTrue(authorizationUrl1.getUrl().toString().contains("prompt=create"));
        assertFalse(authorizationUrl1.getUrl().toString().contains("pricing_table_key"));
        assertNull(authorizationUrl1.getCodeVerifier());
    }

    @Test
    public void testLogoutUrlRequestTest() throws Exception {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .logoutRedirectUri("http://localhost:8089")
                .build();
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        AuthorizationUrl authorizationUrl1 = kindeClientSession.logout();

        assertNotNull(authorizationUrl1);
        assertNotNull(authorizationUrl1.getUrl());
        assertTrue(authorizationUrl1.getUrl().toString().contains("redirect="));
    }

    @Test
    public void testOrgCreateUrlRequestTest() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        AuthorizationUrl authorizationUrl1 = kindeClientSession.createOrg("TEST1", "KEY1", "PLAN1");
        assertNotNull(authorizationUrl1);
        assertNotNull(authorizationUrl1.getUrl());
        System.out.println(authorizationUrl1.getUrl());
        assertTrue(authorizationUrl1.getUrl().toString().contains("prompt=create"));
        assertTrue(authorizationUrl1.getUrl().toString().contains("org_name=TEST1"));
        assertTrue(authorizationUrl1.getUrl().toString().contains("pricing_table_key=KEY1"));
        assertTrue(authorizationUrl1.getUrl().toString().contains("plan_interest=PLAN1"));
        assertNull(authorizationUrl1.getCodeVerifier());

        KindeClient kindeClient2 = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .addScope("openid")
                .addAudience("http://localhost:8089/api")
                .grantType(AuthorizationType.CODE)
                .orgCode("TEST")
                .hasSuccessPage(Boolean.TRUE)
                .build();
        KindeClientSession kindeClientSession2 = kindeClient2.initClientSession("test", null);
        AuthorizationUrl authorizationUrl2 = kindeClientSession2.createOrg("TEST2", "KEY1", "PLAN1");
        assertNotNull(authorizationUrl2);
        assertNotNull(authorizationUrl2.getUrl());
        System.out.println(authorizationUrl2.getUrl());
        assertTrue(authorizationUrl2.getUrl().toString().contains("prompt=create"));
        assertTrue(authorizationUrl2.getUrl().toString().contains("org_code=TEST"));
        assertTrue(authorizationUrl2.getUrl().toString().contains("org_name=TEST2"));
        assertTrue(authorizationUrl2.getUrl().toString().contains("has_success_page=true"));
        assertTrue(authorizationUrl2.getUrl().toString().contains("pricing_table_key=KEY1"));
        assertTrue(authorizationUrl2.getUrl().toString().contains("plan_interest=PLAN1"));
        assertNotNull(authorizationUrl2.getCodeVerifier());
    }

    @Test
    public void testOrgCreateUrlRequestPricingTableKeyEmptyTest() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        AuthorizationUrl authorizationUrl1 = kindeClientSession.createOrg("TEST1", "", "");
        assertNotNull(authorizationUrl1);
        assertNotNull(authorizationUrl1.getUrl());
        System.out.println(authorizationUrl1.getUrl());
        assertTrue(authorizationUrl1.getUrl().toString().contains("prompt=create"));
        assertTrue(authorizationUrl1.getUrl().toString().contains("org_name=TEST1"));
        assertFalse(authorizationUrl1.getUrl().toString().contains("pricing_table_key"));
        assertFalse(authorizationUrl1.getUrl().toString().contains("plan_interest"));
        assertNull(authorizationUrl1.getCodeVerifier());
    }

    @Test
    public void testOrgCreateUrlRequestPricingTableKeyAndPlanInterestsNullTest() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        AuthorizationUrl authorizationUrl1 = kindeClientSession.createOrg("TEST1", null, null);
        assertNotNull(authorizationUrl1);
        assertNotNull(authorizationUrl1.getUrl());
        System.out.println(authorizationUrl1.getUrl());
        assertTrue(authorizationUrl1.getUrl().toString().contains("prompt=create"));
        assertTrue(authorizationUrl1.getUrl().toString().contains("org_name=TEST1"));
        assertFalse(authorizationUrl1.getUrl().toString().contains("pricing_table_key"));
        assertFalse(authorizationUrl1.getUrl().toString().contains("plan_interest"));
        assertNull(authorizationUrl1.getCodeVerifier());
    }

    @Test
    public void testCodeResponseTokenRequestTest() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession = kindeClient.initClientSession("test", null);
        KindeTokens kindeTokens = kindeClientSession.retrieveTokens();
        assertNotNull(kindeTokens.getAccessToken());
        assertTrue(kindeClientSession.authorizationUrl() != null);

        KindeClient kindeClient2 = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .addScope("openid")
                .addAudience("http://localhost:8089/api")
                .grantType(AuthorizationType.CODE)
                .build();
        KindeClientSession kindeClientSession2 = kindeClient2.initClientSession("test", null);
        KindeTokens kindeTokens2 = kindeClientSession2.retrieveTokens();
        assertNotNull(kindeTokens2.getAccessToken());
        assertTrue(kindeClientSession2.authorizationUrl() != null);

        assertNotNull(kindeClient);
        assertNotNull(kindeClientSession);
        assertNotNull(kindeClient2);
        assertNotNull(kindeClientSession2);

        assertTrue(kindeClientSession2.retrieveUserInfo() != null);

        KindeClientSession kindeClientSession3 = kindeClient2.initClientSession("test", null);
        UserInfo userInfo = kindeClientSession3.retrieveUserInfo();
        assertNotNull(userInfo);
        assertNotNull(userInfo.getEmail());
        assertNotNull(userInfo.getSubject());
        assertNotNull(userInfo.getId());
        assertNotNull(userInfo.getPicture());
        assertNotNull(userInfo.getGivenName());
        assertNotNull(userInfo.getFamilyName());


        wireMockServer.removeStub(
                WireMock.post(urlPathMatching("/oauth2/token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\n" +
                                        "  \"access_token\": \"" + JwtGenerator.generateAccessToken() + "\",\n" +
                                        "  \"token_type\": \"Bearer\",\n" +
                                        "  \"expires_in\": 3600,\n" +
                                        "  \"id_token\": \"" + JwtGenerator.generateIDToken() + "\",\n" +
                                        "  \"refresh_token\": \"" + JwtGenerator.refreshToken() + "\",\n" +
                                        "  \"scope\": \"openid profile email\"\n" +
                                        "}")));

        try {
            KindeClientSession kindeClientSession4 = kindeClient2.initClientSession("test", null);
            KindeTokens kindeTokens4 = kindeClientSession4.retrieveTokens();
            fail("Response should fail");
        } catch (Exception ex) {
            // ignore exception expected behavior
        }

        wireMockServer.removeStub(
                WireMock.get(urlPathMatching("/oauth2/v2/user_profile"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                                          {
                                                          "sub": "1234567890",
                                                          "name": "John Doe",
                                                          "given_name": "John",
                                                          "family_name": "Doe",
                                                          "email": "johndoe@example.com",
                                                          "email_verified": true,
                                                          "picture": "https://example.com/johndoe.jpg",
                                                          "locale": "en-US",
                                                          "updated_at": 1611693980
                                                        }
                                        """)));

        try {
            KindeClientSession kindeClientSession4 = kindeClient2.initClientSession(AccessToken.init(JwtGenerator.refreshToken(), true));
            kindeClientSession4.retrieveUserInfo();
            fail("Response should fail");
        } catch (Exception ex) {
            // ignore exception expected behavior
        }
    }

    @Test
    public void testcClientTokenRequestTest() throws Exception {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession = kindeClient.clientSession();
        KindeTokens kindeTokens = kindeClientSession.retrieveTokens();
        assertNotNull(kindeTokens.getAccessToken());
        assertTrue(kindeClientSession.authorizationUrl() != null);

        KindeClient kindeClient2 = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .logoutRedirectUri("http://localhost:8080/")
                .addScope("openid")
                .addAudience("http://localhost:8089/api")
                .build();
        KindeClientSession kindeClientSession2 = kindeClient2.clientSession();
        KindeTokens kindeTokens2 = kindeClientSession2.retrieveTokens();
        assertNotNull(kindeTokens2.getAccessToken());
        assertTrue(kindeClientSession2.authorizationUrl() != null);
        assertTrue(kindeClientSession2.logout() != null);

        assertNotNull(kindeClient);
        assertNotNull(kindeClientSession);
        assertNotNull(kindeClient2);
        assertNotNull(kindeClientSession2);

        wireMockServer.removeStub(
                WireMock.post(urlPathMatching("/oauth2/token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\n" +
                                        "  \"access_token\": \"" + JwtGenerator.generateAccessToken() + "\",\n" +
                                        "  \"token_type\": \"Bearer\",\n" +
                                        "  \"expires_in\": 3600,\n" +
                                        "  \"id_token\": \"" + JwtGenerator.generateIDToken() + "\",\n" +
                                        "  \"refresh_token\": \"" + JwtGenerator.refreshToken() + "\",\n" +
                                        "  \"scope\": \"openid profile email\"\n" +
                                        "}")));

        try {
            KindeClientSession kindeClientSession4 = kindeClient2.clientSession();
            KindeTokens kindeTokens4 = kindeClientSession4.retrieveTokens();
            fail("Response should fail");
        } catch (Exception ex) {
            // ignore exception expected behavior
        }

    }

    @Test
    public void testTokenRequestTest() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession = kindeClient.initClientSession(RefreshToken.init(JwtGenerator.refreshToken(), true));
        KindeTokens kindeTokens = kindeClientSession.retrieveTokens();
        assertNotNull(kindeTokens.getAccessToken());
        assertTrue(kindeClientSession.authorizationUrl() != null);

        KindeClient kindeClient2 = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .addScope("openid")
                .addAudience("http://localhost:8089/api")
                .build();
        KindeClientSession kindeClientSession2 = kindeClient2.initClientSession(AccessToken.init(JwtGenerator.refreshToken(), true));
        KindeTokens kindeTokens2 = kindeClientSession2.retrieveTokens();
        assertNotNull(kindeTokens2.getAccessToken());
        assertTrue(kindeClientSession2.authorizationUrl() != null);
        assertNotNull(kindeClient);
        assertNotNull(kindeClientSession);
        assertNotNull(kindeClient2);
        assertNotNull(kindeClientSession2);


        UserInfo userInfo = kindeClientSession2.retrieveUserInfo();
        assertNotNull(userInfo);
        assertNotNull(userInfo.getEmail());
        assertNotNull(userInfo.getSubject());
        assertNotNull(userInfo.getId());
        assertNotNull(userInfo.getPicture());
        assertNotNull(userInfo.getGivenName());
        assertNotNull(userInfo.getFamilyName());

        try {
            KindeClientSession kindeClientSession3 = kindeClient2.initClientSession(RefreshToken.init(JwtGenerator.refreshToken(), true));
            kindeClientSession3.retrieveUserInfo();
            fail("Expected an exception when retrieving the user info using the incorrect token.");
        } catch (Exception ex) {
            // ignore
        }

        wireMockServer.removeStub(
                WireMock.post(urlPathMatching("/oauth2/token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\n" +
                                        "  \"access_token\": \"" + JwtGenerator.generateAccessToken() + "\",\n" +
                                        "  \"token_type\": \"Bearer\",\n" +
                                        "  \"expires_in\": 3600,\n" +
                                        "  \"id_token\": \"" + JwtGenerator.generateIDToken() + "\",\n" +
                                        "  \"refresh_token\": \"" + JwtGenerator.refreshToken() + "\",\n" +
                                        "  \"scope\": \"openid profile email\"\n" +
                                        "}")));

        try {
            KindeClientSession kindeClientSession4 = kindeClient2.initClientSession(AccessToken.init(JwtGenerator.refreshToken(), true));
            KindeTokens kindeTokens4 = kindeClientSession4.retrieveTokens();
            fail("Response should fail");
        } catch (Exception ex) {
            // ignore exception expected behavior
        }

        wireMockServer.removeStub(
                WireMock.get(urlPathMatching("/oauth2/v2/user_profile"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                                          {
                                                          "sub": "1234567890",
                                                          "name": "John Doe",
                                                          "given_name": "John",
                                                          "family_name": "Doe",
                                                          "email": "johndoe@example.com",
                                                          "email_verified": true,
                                                          "picture": "https://example.com/johndoe.jpg",
                                                          "locale": "en-US",
                                                          "updated_at": 1611693980
                                                        }
                                        """)));

        try {
            KindeClientSession kindeClientSession4 = kindeClient2.initClientSession(AccessToken.init(JwtGenerator.refreshToken(), true));
            kindeClientSession4.retrieveUserInfo();
            fail("Response should fail");
        } catch (Exception ex) {
            // ignore exception expected behavior
        }
    }
}
