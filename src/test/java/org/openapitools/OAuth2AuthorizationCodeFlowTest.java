package org.openapitools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.sdk.KindeClientSDK;
import org.openapitools.sdk.enums.GrantType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OAuth2AuthorizationCodeFlowTest {

    @MockBean
//    @InjectMocks
    private KindeClientSDK kindeClientSDK;

    @MockBean
    private HttpServletResponse response;

    @MockBean
    private HttpServletRequest request;

    @Value("${kinde.host}")
    public String domain;

    @Value("${kinde.redirect.url}")
    public String redirectUri;

    @Value("${kinde.post.logout.redirect.url}")
    public String logoutRedirectUri;

    @Value("${kinde.client.id}")
    public String clientId;

    @Value("${kinde.client.secret}")
    public String clientSecret;

    @BeforeEach
    void setUp() {

    }


    @Test
    void testLoginTypeAuthorizationCodeFlow() {
        kindeClientSDK = new KindeClientSDK(domain, redirectUri, clientId, clientSecret, GrantType.AUTHORIZATION_CODE.getValue(), logoutRedirectUri);
        Object result = kindeClientSDK.login(response);
        System.out.println(result);
        assertNotNull(result);
        assertTrue(result instanceof RedirectView);
    }

    @Test
    void testLoginTypeAuthorizationCodeFlowWithAudience() {
        kindeClientSDK = new KindeClientSDK(domain, redirectUri, clientId, clientSecret, GrantType.AUTHORIZATION_CODE.getValue(), logoutRedirectUri, "", Collections.singletonMap("audience", domain + "/api"));
        Object result = kindeClientSDK.login(response);
        System.out.println(result);
        assertNotNull(result);
        assertTrue(result instanceof RedirectView);
    }

    @Test
    void testLoginTypeAuthorizationCodeFlowWithAdditional() {
        kindeClientSDK = new KindeClientSDK(domain, redirectUri, clientId, clientSecret, GrantType.AUTHORIZATION_CODE.getValue(), logoutRedirectUri, "", Collections.singletonMap("audience", domain + "/api"));
        Map<String, Object> additional = new HashMap<>();
        additional.put("org_code", "org_123");
        additional.put("org_name", "My Application");
        Object result = kindeClientSDK.login(response,additional);
        System.out.println(result);
        assertNotNull(result);
        assertTrue(result instanceof RedirectView);
    }
}
