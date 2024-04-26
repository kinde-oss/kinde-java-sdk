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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class OAuth2PKCETest {

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
    public void testLoginTypePKCE() {
        kindeClientSDK= new KindeClientSDK(
                domain,redirectUri,clientId,clientSecret,GrantType.PKCE.getValue(),logoutRedirectUri,null,null,null
        );
        Object result = kindeClientSDK.login(response);
        System.out.println("testLoginTypePKCE() :: "+result.toString());
        assertNotNull(result);
        assertTrue(result instanceof RedirectView);
    }

    @Test
    public void testLoginTypeClientCredentialFlowWithAudience() {
        kindeClientSDK= new KindeClientSDK(
                domain,redirectUri,clientId,clientSecret,GrantType.PKCE.getValue(),logoutRedirectUri,null, Collections.singletonMap("audience",domain+"/api"),null
        );
        Object result = kindeClientSDK.login(response);
        System.out.println("testLoginTypeClientCredentialFlowWithAudience() :: "+result.toString());
        assertNotNull(result);
        assertTrue(result instanceof RedirectView);
    }

    @Test
    public void testLoginTypeClientCredentialFlowWithOrgCode() {
        kindeClientSDK= new KindeClientSDK(
                domain,redirectUri,clientId,clientSecret,GrantType.PKCE.getValue(),logoutRedirectUri,null, Collections.singletonMap("audience",domain+"/api"),null
        );
        Map<String,Object> additonalParameters=new HashMap<>();
        additonalParameters.put("org_code","org_b3afab4f52a");
        additonalParameters.put("org_name","My Application");
        Object result = kindeClientSDK.login(response,additonalParameters);
        System.out.println("testLoginTypeClientCredentialFlowWithOrgCode() :: "+result.toString());
        assertNotNull(result);
        assertTrue(result instanceof RedirectView);
    }

    @Test
    public void testRegisterTypeClientCredentialFlowWithAdditional() {
        kindeClientSDK= new KindeClientSDK(
                domain,redirectUri,clientId,clientSecret,GrantType.PKCE.getValue(),logoutRedirectUri,null, Collections.singletonMap("audience",domain+"/api"),null
        );
        Map<String,Object> additonalParameters=new HashMap<>();
        additonalParameters.put("org_code","org_b3afab4f52a");
        additonalParameters.put("org_name","My Application");
        Object result = kindeClientSDK.register(response,additonalParameters);
        System.out.println("testRegisterTypeClientCredentialFlowWithAdditional() :: "+result.toString());
        assertNotNull(result);
        assertTrue(result instanceof RedirectView);
    }

    @Test
    public void testCreateOrgTypeClientCredentialFlow() {
        kindeClientSDK= new KindeClientSDK(
                domain,redirectUri,clientId,clientSecret,GrantType.PKCE.getValue(),logoutRedirectUri,null, Collections.singletonMap("audience",domain+"/api"),null
        );
        Object result = kindeClientSDK.createOrg(response);
        System.out.println("testCreateOrgTypeClientCredentialFlow() :: "+result.toString());
        assertNotNull(result);
        assertTrue(result instanceof RedirectView);
    }

    @Test
    public void testCreateOrgTypeClientCredentialFlowWithAdditional() {
        kindeClientSDK= new KindeClientSDK(
                domain,redirectUri,clientId,clientSecret,GrantType.PKCE.getValue(),logoutRedirectUri,null, Collections.singletonMap("audience",domain+"/api"),null
        );
        Map<String,Object> additonalParameters=new HashMap<>();
        additonalParameters.put("org_code","org_b3afab4f52a");
        additonalParameters.put("org_name","My Application");
        Object result = kindeClientSDK.createOrg(response,additonalParameters);
        System.out.println("testCreateOrgTypeClientCredentialFlowWithAdditional() :: "+result.toString());
        assertNotNull(result);
        assertTrue(result instanceof RedirectView);
    }
}
