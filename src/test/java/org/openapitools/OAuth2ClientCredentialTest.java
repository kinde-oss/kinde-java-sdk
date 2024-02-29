package org.openapitools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.sdk.KindeClientSDK;
import org.openapitools.sdk.enums.GrantType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OAuth2ClientCredentialTest {

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
    void testLoginTypeClientCredential() {
        kindeClientSDK = new KindeClientSDK(domain, redirectUri, clientId, clientSecret, GrantType.CLIENT_CREDENTIALS.getValue(), logoutRedirectUri);
        Object resp = kindeClientSDK.login(response);
        System.out.println(resp);
        assertResponse(resp);
    }

    @Test
    void testLoginTypeClientCredentialFlowWithAudience() {
        kindeClientSDK = new KindeClientSDK(domain, redirectUri, clientId, clientSecret, GrantType.CLIENT_CREDENTIALS.getValue(), logoutRedirectUri, "", Collections.singletonMap("audience", domain + "/api"));
        Object resp = kindeClientSDK.login(response);
        System.out.println(resp);
        assertResponse(resp);
    }

    @Test
    void testLoginTypeClientCredentialFlowWithOrgCode() {
        kindeClientSDK = new KindeClientSDK(domain, redirectUri, clientId, clientSecret, GrantType.CLIENT_CREDENTIALS.getValue(), logoutRedirectUri, "", Collections.singletonMap("audience", domain + "/api"));
        Map<String, Object> additional = new HashMap<>();
        additional.put("org_code", "org_123");
        additional.put("org_name", "My Application");
        Object resp = kindeClientSDK.login(response,additional);
        System.out.println(resp);
        assertResponse(resp);
    }

    private void assertResponse(Object response) {
        assertTrue(response instanceof Map);
        Map<?, ?> responseMap = (Map<?, ?>) response;
        assertTrue(responseMap.containsKey("access_token"));
        assertTrue(responseMap.containsKey("expires_in"));
        assertTrue(responseMap.containsKey("scope"));
        assertTrue(responseMap.containsKey("token_type"));
    }
}
