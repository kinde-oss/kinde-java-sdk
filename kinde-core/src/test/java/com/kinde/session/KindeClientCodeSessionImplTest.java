package com.kinde.session;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.client.KindeClientGuiceTestModule;
import com.kinde.client.oidc.OidcMetaDataImplTest;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import com.kinde.token.AccessToken;
import com.kinde.token.RefreshToken;
import com.kinde.token.jwt.JwtGenerator;
import org.junit.jupiter.api.*;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.assertNotNull;


public class KindeClientCodeSessionImplTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        KindeGuiceSingleton.fin();
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
        assertTrue(kindeClientSession.retrieveTokens().size()>0);
        assertTrue(kindeClientSession.authorizationUrl()!=null);

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
        assertTrue(kindeClientSession2.retrieveTokens().size()>0);
        assertTrue(kindeClientSession2.authorizationUrl()!=null);

        assertNotNull(kindeClient);
        assertNotNull(kindeClientSession);
        assertNotNull(kindeClient2);
        assertNotNull(kindeClientSession2);
    }

    @Test
    public void testcClientTokenRequestTest() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession = kindeClient.clientSession();
        assertTrue(kindeClientSession.retrieveTokens().size()>0);
        assertTrue(kindeClientSession.authorizationUrl()!=null);

        KindeClient kindeClient2 = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .addScope("openid")
                .addAudience("http://localhost:8089/api")
                .build();
        KindeClientSession kindeClientSession2 = kindeClient2.clientSession();
        assertTrue(kindeClientSession2.retrieveTokens().size()>0);
        assertTrue(kindeClientSession2.authorizationUrl()!=null);

        assertNotNull(kindeClient);
        assertNotNull(kindeClientSession);
        assertNotNull(kindeClient2);
        assertNotNull(kindeClientSession2);
    }

    @Test
    public void testTokenRequestTest() {
        KindeClient kindeClient = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .build();
        KindeClientSession kindeClientSession =  kindeClient.initClientSession(RefreshToken.init(JwtGenerator.refreshToken(),true));
        assertTrue(kindeClientSession.retrieveTokens().size()>0);
        assertTrue(kindeClientSession.authorizationUrl()!=null);

        KindeClient kindeClient2 = KindeClientBuilder.builder()
                .domain("http://localhost:8089")
                .clientId("test")
                .clientSecret("test")
                .redirectUri("http://localhost:8080/")
                .addScope("openid")
                .addAudience("http://localhost:8089/api")
                .build();
        KindeClientSession kindeClientSession2 =  kindeClient2.initClientSession(AccessToken.init(JwtGenerator.refreshToken(),true));
        assertTrue(kindeClientSession2.retrieveTokens().size()>0);
        assertTrue(kindeClientSession2.authorizationUrl()!=null);
        assertNotNull(kindeClient);
        assertNotNull(kindeClientSession);
        assertNotNull(kindeClient2);
        assertNotNull(kindeClientSession2);

        assertTrue(kindeClientSession2.retrieveUserInfo()!=null);
    }
}
