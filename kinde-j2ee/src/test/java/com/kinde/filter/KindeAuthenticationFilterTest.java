package com.kinde.filter;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.constants.KindeAuthenticationAction;
import com.kinde.principal.KindePrincipal;
import com.kinde.servlet.KindeSingleton;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeTokens;
import com.kinde.user.UserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.URL;
import java.security.Principal;

import static com.kinde.constants.KindeJ2eeConstants.AUTHENTICATED_USER;
import static com.kinde.constants.KindeJ2eeConstants.AUTHORIZATION_URL;
import static org.mockito.Mockito.*;

public class KindeAuthenticationFilterTest {

    private TestKindeAuthenticationFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private FilterChain filterChain;

    private KindeClientSession mockSession;
    private AuthorizationUrl mockAuthUrl;
    private KindeTokens mockTokens;
    private UserInfo mockUserInfo;
    private MockedStatic<KindeSingleton> kindeSingletonStatic;

    @Before
    public void setUp() throws Exception {
        filter = new TestKindeAuthenticationFilter();

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        filterChain = mock(FilterChain.class);

        KindeClientBuilder mockBuilder = mock(KindeClientBuilder.class);
        KindeClient mockClient = mock(KindeClient.class);
        mockSession = mock(KindeClientSession.class);
        mockAuthUrl = mock(AuthorizationUrl.class);
        mockTokens = mock(KindeTokens.class);
        mockUserInfo = mock(UserInfo.class);

        // Static mocking for KindeSingleton
        kindeSingletonStatic = Mockito.mockStatic(KindeSingleton.class);
        KindeSingleton singleton = mock(KindeSingleton.class);
        kindeSingletonStatic.when(KindeSingleton::getInstance).thenReturn(singleton);

        // Builder chain
        when(singleton.getKindeClientBuilder()).thenReturn(mockBuilder);
        when(mockBuilder.redirectUri(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.grantType(any())).thenReturn(mockBuilder);
        when(mockBuilder.orgCode(any())).thenReturn(mockBuilder);
        when(mockBuilder.lang(any())).thenReturn(mockBuilder);
        when(mockBuilder.scopes(any())).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockClient);
        when(mockClient.clientSession()).thenReturn(mockSession);
        when(request.getRequestURI()).thenReturn("/example/redirect");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));

        // For code exchange branch
        when(singleton.getKindeClient()).thenReturn(mockClient);
        when(mockClient.initClientSession(anyString(), any())).thenReturn(mockSession);

        // Session and request
        when(request.getSession()).thenReturn(session);
    }

    @After
    public void tearDown() {
        kindeSingletonStatic.close();
    }

    @Test
    public void testRedirectToAuthorizationWhenNoPrincipalOrAuthUrl() throws Exception {
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(null);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(null);
        when(mockAuthUrl.getUrl()).thenReturn(new URL("http://auth.url"));
        when(mockSession.login()).thenReturn(mockAuthUrl);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.LOGIN);

        verify(response).sendRedirect("http://auth.url");
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void testExchangeCodeAndRedirect() throws Exception {
        when(request.getParameter("code")).thenReturn("authCode");
        Principal principal = mock(KindePrincipal.class);
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(principal);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(mockAuthUrl);
        when(mockSession.retrieveTokens()).thenReturn(mockTokens);
        when(mockTokens.getAccessToken()).thenReturn(mock(AccessToken.class));
        when(mockSession.retrieveUserInfo()).thenReturn(mockUserInfo);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.LOGIN);

        verify(response).sendRedirect(anyString());
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void testProceedWithFilterChainWhenUserPrincipalPresent() throws Exception {
        Principal principal = mock(KindePrincipal.class);
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(principal);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(mockAuthUrl);
        when(request.getParameter("code")).thenReturn(null);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.LOGIN);

        verify(filterChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void testLoginLinkExpiredRedirectsWithDecodedParams() throws Exception {
        // Setup base64-encoded URL params
        String urlParams = "param1=value1&param2=value2";
        String encodedState = java.util.Base64.getEncoder().encodeToString(urlParams.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        when(request.getParameter("error")).thenReturn("login_link_expired");
        when(request.getParameter("reauth_state")).thenReturn(encodedState);
        when(mockAuthUrl.getUrl()).thenReturn(new URL("http://auth.url"));
        when(mockSession.login()).thenReturn(mockAuthUrl);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.LOGIN);

        // Should redirect to the auth URL with decoded params appended
        verify(response).sendRedirect("http://auth.url?" + urlParams);
        // Should set the session attribute
        verify(session).setAttribute(AUTHORIZATION_URL, mockAuthUrl);
        // Should not proceed with the filter chain
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void testInvitationCodeOnLoginPassesCodeToLogin() throws Exception {
        when(request.getParameter("invitation_code")).thenReturn("inv_filter123");
        AuthorizationUrl invitationAuthUrl = mock(AuthorizationUrl.class);
        when(invitationAuthUrl.getUrl()).thenReturn(new URL("http://auth.url?invitation_code=inv_filter123&is_invitation=true"));
        when(mockSession.login("inv_filter123")).thenReturn(invitationAuthUrl);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.LOGIN);

        verify(mockSession).login("inv_filter123");
        verify(session).setAttribute(AUTHORIZATION_URL, invitationAuthUrl);
        verify(response).sendRedirect(invitationAuthUrl.getUrl().toString());
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void testInvitationCodeOnRegisterPassesCodeToRegister() throws Exception {
        when(request.getParameter("invitation_code")).thenReturn("inv_filter_reg");
        AuthorizationUrl invitationAuthUrl = mock(AuthorizationUrl.class);
        when(invitationAuthUrl.getUrl()).thenReturn(new URL("http://auth.url?invitation_code=inv_filter_reg&is_invitation=true"));
        when(mockSession.register("inv_filter_reg")).thenReturn(invitationAuthUrl);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.REGISTER);

        verify(mockSession).register("inv_filter_reg");
        verify(session).setAttribute(AUTHORIZATION_URL, invitationAuthUrl);
        verify(response).sendRedirect(invitationAuthUrl.getUrl().toString());
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void testInvitationCodeOverridesExistingSession() throws Exception {
        Principal principal = mock(KindePrincipal.class);
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(principal);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(mockAuthUrl);
        when(request.getParameter("invitation_code")).thenReturn("inv_override");
        AuthorizationUrl invitationAuthUrl = mock(AuthorizationUrl.class);
        when(invitationAuthUrl.getUrl()).thenReturn(new URL("http://auth.url?invitation_code=inv_override&is_invitation=true"));
        when(mockSession.login("inv_override")).thenReturn(invitationAuthUrl);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.LOGIN);

        verify(mockSession).login("inv_override");
        verify(response).sendRedirect(invitationAuthUrl.getUrl().toString());
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void testEmptyInvitationCodeFallsThrough() throws Exception {
        when(request.getParameter("invitation_code")).thenReturn("");
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(null);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(null);
        when(mockAuthUrl.getUrl()).thenReturn(new URL("http://auth.url"));
        when(mockSession.login()).thenReturn(mockAuthUrl);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.LOGIN);

        verify(mockSession).login();
    }

    @Test
    public void testWhitespaceOnlyInvitationCodeFallsThrough() throws Exception {
        when(request.getParameter("invitation_code")).thenReturn("   ");
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(null);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(null);
        when(mockAuthUrl.getUrl()).thenReturn(new URL("http://auth.url"));
        when(mockSession.login()).thenReturn(mockAuthUrl);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.LOGIN);

        verify(mockSession).login();
    }

    @Test
    public void testInvitationCodeOnCreateOrgPassesCodeToCreateOrg() throws Exception {
        when(request.getParameter("invitation_code")).thenReturn("inv_org_create");
        when(request.getParameter("org_name")).thenReturn("MyOrg");
        AuthorizationUrl invitationAuthUrl = mock(AuthorizationUrl.class);
        when(invitationAuthUrl.getUrl()).thenReturn(new URL("http://auth.url?invitation_code=inv_org_create&is_invitation=true"));
        when(mockSession.createOrg("MyOrg", "inv_org_create")).thenReturn(invitationAuthUrl);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.CREATE_ORG);

        verify(mockSession).createOrg("MyOrg", "inv_org_create");
        verify(session).setAttribute(AUTHORIZATION_URL, invitationAuthUrl);
        verify(response).sendRedirect(invitationAuthUrl.getUrl().toString());
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test(expected = jakarta.servlet.ServletException.class)
    public void testInvitationCodeOnCreateOrgMissingOrgNameThrows() throws Exception {
        when(request.getParameter("invitation_code")).thenReturn("inv_org_no_name");
        when(request.getParameter("org_name")).thenReturn(null);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.CREATE_ORG);
    }

    @Test
    public void testLoginLinkExpiredWithQueryParamsInAuthUrl() throws Exception {
        String urlParams = "param1=value1";
        String encodedState = java.util.Base64.getEncoder().encodeToString(urlParams.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        when(request.getParameter("error")).thenReturn("login_link_expired");
        when(request.getParameter("reauth_state")).thenReturn(encodedState);
        when(mockAuthUrl.getUrl()).thenReturn(new URL("http://auth.url?existing=param"));
        when(mockSession.login()).thenReturn(mockAuthUrl);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.LOGIN);

        verify(response).sendRedirect("http://auth.url?existing=param&" + urlParams);
    }

    @Test
    public void testRegisterRedirectWhenNoPrincipal() throws Exception {
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(null);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(null);
        when(mockAuthUrl.getUrl()).thenReturn(new URL("http://auth.url"));
        when(mockSession.register()).thenReturn(mockAuthUrl);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.REGISTER);

        verify(mockSession).register();
        verify(response).sendRedirect("http://auth.url");
    }

    @Test
    public void testCreateOrgRedirectWhenNoPrincipal() throws Exception {
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(null);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(null);
        when(request.getParameter("org_name")).thenReturn("NewOrg");
        when(mockAuthUrl.getUrl()).thenReturn(new URL("http://auth.url"));
        when(mockSession.createOrg("NewOrg")).thenReturn(mockAuthUrl);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.CREATE_ORG);

        verify(mockSession).createOrg("NewOrg");
        verify(response).sendRedirect("http://auth.url");
    }

    @Test(expected = jakarta.servlet.ServletException.class)
    public void testCreateOrgMissingOrgNameThrowsWhenNoPrincipal() throws Exception {
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(null);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(null);
        when(request.getParameter("org_name")).thenReturn(null);

        filter.doFilter(request, response, filterChain, KindeAuthenticationAction.CREATE_ORG);
    }
}