package com.kinde.filter;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.exceptions.InvalidStateException;
import com.kinde.exceptions.StepUpNotSupportedException;
import com.kinde.exceptions.UnmetAuthenticationRequirementsException;
import com.kinde.principal.KindeAuthentication;
import com.kinde.principal.KindePrincipal;
import com.kinde.servlet.KindeSingleton;
import com.kinde.stepup.AuthenticationContext;
import com.kinde.stepup.StepUpRequest;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeTokens;
import com.kinde.user.UserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.kinde.constants.KindeJ2eeConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KindeStepUpFilterTest {

    private KindeStepUpFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private FilterChain filterChain;
    private KindeClientSession mockSession;
    private MockedStatic<KindeSingleton> kindeSingletonStatic;

    @Before
    public void setUp() throws Exception {
        filter = new KindeStepUpFilter();
        filter.init(config("mfa", "300"));

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        filterChain = mock(FilterChain.class);

        KindeClientBuilder mockBuilder = mock(KindeClientBuilder.class);
        KindeClient mockClient = mock(KindeClient.class);
        mockSession = mock(KindeClientSession.class);

        kindeSingletonStatic = Mockito.mockStatic(KindeSingleton.class);
        KindeSingleton singleton = mock(KindeSingleton.class);
        kindeSingletonStatic.when(KindeSingleton::getInstance).thenReturn(singleton);

        when(singleton.getKindeClientBuilder()).thenReturn(mockBuilder);
        when(mockBuilder.redirectUri(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.grantType(any())).thenReturn(mockBuilder);
        when(mockBuilder.orgCode(any())).thenReturn(mockBuilder);
        when(mockBuilder.lang(any())).thenReturn(mockBuilder);
        when(mockBuilder.scopes(any())).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockClient);
        when(mockClient.clientSession()).thenReturn(mockSession);
        when(request.getRequestURI()).thenReturn("/private/transfer");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/private/transfer"));
        when(singleton.getKindeClient()).thenReturn(mockClient);
        when(mockClient.initClientSession(anyString(), any())).thenReturn(mockSession);
        when(mockSession.isStepUpSupported()).thenReturn(true);
        when(request.getSession()).thenReturn(session);
    }

    @After
    public void tearDown() {
        kindeSingletonStatic.close();
    }

    private static FilterConfig config(String acrValues, String maxAge) {
        FilterConfig config = mock(FilterConfig.class);
        when(config.getInitParameter(ACR_VALUES)).thenReturn(acrValues);
        when(config.getInitParameter(MAX_AGE)).thenReturn(maxAge);
        return config;
    }

    private static AuthorizationUrl url(String state) throws Exception {
        return new AuthorizationUrl(new URL("http://test.kinde.com/oauth2/auth"), null, state, "mfa", 300);
    }

    private static KindeTokens tokens(String acr, Long authTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("acr", acr);
        claims.put("auth_time", authTime);
        KindeTokens tokens = mock(KindeTokens.class);
        when(tokens.getAuthenticationContext(anyBoolean())).thenAnswer(invocation ->
                AuthenticationContext.fromClaims(claims, (Map<String, Object>) null, invocation.getArgument(0)));
        AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.getUser()).thenReturn("kp_1");
        when(tokens.getAccessToken()).thenReturn(accessToken);
        return tokens;
    }

    private static KindePrincipal principal(String name) {
        KindePrincipal principal = mock(KindePrincipal.class);
        when(principal.getName()).thenReturn(name);
        return principal;
    }

    private void signedInWith(KindeTokens tokens) throws Exception {
        signedInWith(principal("kp_1"), tokens);
    }

    private void signedInWith(KindePrincipal principal, KindeTokens tokens) throws Exception {
        AuthorizationUrl consumed = url("state-123").consumed();
        KindeAuthentication authentication = new KindeAuthentication(principal, tokens);
        when(session.getAttribute(KINDE_AUTHENTICATION)).thenReturn(authentication);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(consumed);
    }

    @Test
    public void tokensOfAnotherUserNeverSatisfyTheRequirement() throws Exception {
        signedInWith(principal("kp_2"), tokens("phr", now() - 10));
        when(mockSession.isStepUpSupported()).thenReturn(true);
        when(mockSession.stepUp(any())).thenReturn(url("state-456"));

        filter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        verify(mockSession).stepUp(any());
    }

    @Test
    public void principalAndTokensAreReadOncePerRequest() throws Exception {
        signedInWith(tokens("phr", now() - 10));
        when(mockSession.isStepUpSupported()).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        // One attribute holds both, so the pair cannot change between two reads
        verify(session, times(1)).getAttribute(KINDE_AUTHENTICATION);
        verify(session, never()).getAttribute(KINDE_TOKENS);
        verify(session, never()).getAttribute(AUTHENTICATED_USER);
    }

    @Test
    public void theRequestRunsAsThePrincipalWhoseTokensWereChecked() throws Exception {
        // The separate attributes hold an older sign-in of the same user with other permissions
        KindePrincipal checked = principal("kp_1");
        signedInWith(checked, tokens("phr", now() - 10));
        KindePrincipal older = principal("kp_1");
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(older);
        when(mockSession.isStepUpSupported()).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        ArgumentCaptor<ServletRequest> passed = ArgumentCaptor.forClass(ServletRequest.class);
        verify(filterChain).doFilter(passed.capture(), any(ServletResponse.class));
        assertSame(checked, ((jakarta.servlet.http.HttpServletRequest) passed.getValue()).getUserPrincipal());
    }

    @Test
    public void aSessionWithoutTheCombinedAttributeGoesThroughAStepUp() throws Exception {
        // For example a session signed in before KINDE_AUTHENTICATION existed
        KindePrincipal principal = principal("kp_1");
        KindeTokens tokens = tokens("phr", now() - 10);
        AuthorizationUrl consumed = url("state-123").consumed();
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(principal);
        when(session.getAttribute(KINDE_TOKENS)).thenReturn(tokens);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(consumed);
        when(mockSession.stepUp(any())).thenReturn(url("state-456"));

        filter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        verify(mockSession).stepUp(any());
    }

    @Test
    public void anExpiredLoginLinkRestartsTheStepUpNotAPlainLogin() throws Exception {
        String reauth = java.util.Base64.getUrlEncoder().encodeToString(
                "login_hint=a%40b.c&acr_values=&max_age=99999&prompt=none&foo=bar".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        when(request.getParameter("error")).thenReturn("login_link_expired");
        when(request.getParameter("reauth_state")).thenReturn(reauth);
        when(mockSession.stepUp(any())).thenReturn(url("state-456"));

        filter.doFilter(request, response, filterChain);

        ArgumentCaptor<StepUpRequest> captor = ArgumentCaptor.forClass(StepUpRequest.class);
        verify(mockSession).stepUp(captor.capture());
        assertEquals(Optional.of("mfa"), captor.getValue().getAcrValues());
        assertEquals("300", captor.getValue().getParameters().get("max_age"));
        verify(mockSession, never()).login();
        // Browser-supplied parameters cannot change the requirement or skip the prompt
        verify(response).sendRedirect("http://test.kinde.com/oauth2/auth?login_hint=a%40b.c&foo=bar");
    }

    private static long now() {
        return System.currentTimeMillis() / 1000;
    }

    @Test
    public void initRejectsMissingOrInvalidRequirements() {
        assertThrows(ServletException.class, () -> new KindeStepUpFilter().init(config(null, null)));
        assertThrows(ServletException.class, () -> new KindeStepUpFilter().init(config("gold", null)));
        assertThrows(ServletException.class, () -> new KindeStepUpFilter().init(config("mfa", "soon")));
        assertThrows(ServletException.class, () -> new KindeStepUpFilter().init(config(null, "0")));
    }

    @Test
    public void aSessionThatMeetsTheRequirementPassesThrough() throws Exception {
        signedInWith(tokens("phr", now() - 10));

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void aSingleFactorSessionIsSentToStepUpAndKeepsItsTokens() throws Exception {
        signedInWith(tokens(null, now() - 10));
        AuthorizationUrl stepUpUrl = url("state-456");
        when(mockSession.stepUp(any())).thenReturn(stepUpUrl);

        filter.doFilter(request, response, filterChain);

        ArgumentCaptor<StepUpRequest> captor = ArgumentCaptor.forClass(StepUpRequest.class);
        verify(mockSession).stepUp(captor.capture());
        assertEquals(Optional.of("mfa"), captor.getValue().getAcrValues());
        assertEquals("300", captor.getValue().getParameters().get("max_age"));
        verify(session).setAttribute(AUTHORIZATION_URL, stepUpUrl);
        verify(session, never()).setAttribute(eq(KINDE_TOKENS), any());
        verify(response).sendRedirect("http://test.kinde.com/oauth2/auth");
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void anAuthenticationThatIsTooOldIsSentToStepUp() throws Exception {
        signedInWith(tokens("mfa", now() - 3600));
        when(mockSession.stepUp(any())).thenReturn(url("state-456"));

        filter.doFilter(request, response, filterChain);

        verify(mockSession).stepUp(any());
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void aUserWhoIsNotSignedInSignsInWithTheRequiredContext() throws Exception {
        when(mockSession.stepUp(any())).thenReturn(url("state-456"));

        filter.doFilter(request, response, filterChain);

        verify(mockSession).stepUp(any());
        verify(mockSession, never()).login();
        verify(response).sendRedirect("http://test.kinde.com/oauth2/auth");
    }

    @Test
    public void theCallbackReplacesTheTokensAndReturnsToThePage() throws Exception {
        KindeTokens newTokens = tokens("mfa", now());
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(mock(KindePrincipal.class));
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(url("state-123"));
        when(request.getParameter("code")).thenReturn("authCode");
        when(request.getParameter("state")).thenReturn("state-123");
        when(mockSession.retrieveTokens()).thenReturn(newTokens);
        when(mockSession.retrieveUserInfo()).thenReturn(mock(UserInfo.class));

        filter.doFilter(request, response, filterChain);

        verify(session).setAttribute(KINDE_TOKENS, newTokens);
        ArgumentCaptor<Object> stored = ArgumentCaptor.forClass(Object.class);
        verify(session).setAttribute(eq(KINDE_AUTHENTICATION), stored.capture());
        assertSame(newTokens, ((KindeAuthentication) stored.getValue()).getTokens());
        ArgumentCaptor<AuthorizationUrl> consumed = ArgumentCaptor.forClass(AuthorizationUrl.class);
        verify(session).setAttribute(eq(AUTHORIZATION_URL), consumed.capture());
        assertNull(consumed.getValue().getState());
        verify(response).sendRedirect("/private/transfer");
    }

    @Test
    public void aCallbackWhoseTokensStillLackTheContextDoesNotLoop() throws Exception {
        KindeTokens weakTokens = tokens(null, now());
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(url("state-123"));
        when(request.getParameter("code")).thenReturn("authCode");
        when(request.getParameter("state")).thenReturn("state-123");
        when(mockSession.retrieveTokens()).thenReturn(weakTokens);
        when(mockSession.retrieveUserInfo()).thenReturn(mock(UserInfo.class));

        assertThrows(ServletException.class, () -> filter.doFilter(request, response, filterChain));
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void aCallbackWithAWrongStateIsRejected() throws Exception {
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(mock(KindePrincipal.class));
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(url("state-123"));
        when(request.getParameter("code")).thenReturn("authCode");
        when(request.getParameter("state")).thenReturn("forged");

        ServletException ex = assertThrows(ServletException.class,
                () -> filter.doFilter(request, response, filterChain));
        assertTrue(ex.getRootCause() instanceof InvalidStateException);
        verify(mockSession, never()).retrieveTokens();
    }

    @Test
    public void unmetRequirementsKeepsTheSessionAndDoesNotRetry() throws Exception {
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(mock(KindePrincipal.class));
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(url("state-123"));
        when(request.getParameter("error")).thenReturn("unmet_authentication_requirements");
        when(request.getParameter("state")).thenReturn("state-123");

        ServletException ex = assertThrows(ServletException.class,
                () -> filter.doFilter(request, response, filterChain));

        assertTrue(ex.getRootCause() instanceof UnmetAuthenticationRequirementsException);
        verify(request).setAttribute(eq(KINDE_CALLBACK_ERROR), any());
        verify(session, never()).setAttribute(eq(KINDE_TOKENS), any());
        verify(mockSession, never()).stepUp(any());
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void aCallbackWhoseTokensAreTooOldDoesNotLoop() throws Exception {
        KindeTokens oldTokens = tokens("mfa", now() - 3600);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(url("state-123"));
        when(request.getParameter("code")).thenReturn("authCode");
        when(request.getParameter("state")).thenReturn("state-123");
        when(mockSession.retrieveTokens()).thenReturn(oldTokens);
        when(mockSession.retrieveUserInfo()).thenReturn(mock(UserInfo.class));

        assertThrows(ServletException.class, () -> filter.doFilter(request, response, filterChain));
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void aMaxAgeOnlyCallbackWithoutAuthTimeDoesNotLoop() throws Exception {
        KindeStepUpFilter maxAgeOnly = new KindeStepUpFilter();
        maxAgeOnly.init(config(null, "300"));
        KindeTokens noAuthTime = tokens(null, null);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(url("state-123"));
        when(request.getParameter("code")).thenReturn("authCode");
        when(request.getParameter("state")).thenReturn("state-123");
        when(mockSession.retrieveTokens()).thenReturn(noAuthTime);
        when(mockSession.retrieveUserInfo()).thenReturn(mock(UserInfo.class));

        assertThrows(ServletException.class, () -> maxAgeOnly.doFilter(request, response, filterChain));
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void aPendingCallbackWithoutStateIsRejected() throws Exception {
        when(session.getAttribute(AUTHENTICATED_USER)).thenReturn(mock(KindePrincipal.class));
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(url("state-123"));
        when(request.getParameter("code")).thenReturn("authCode");

        ServletException ex = assertThrows(ServletException.class,
                () -> filter.doFilter(request, response, filterChain));
        assertTrue(ex.getRootCause() instanceof InvalidStateException);
        verify(mockSession, never()).retrieveTokens();
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void aPendingErrorWithoutStateIsRejected() throws Exception {
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(url("state-123"));
        when(request.getParameter("error")).thenReturn("unmet_authentication_requirements");

        ServletException ex = assertThrows(ServletException.class,
                () -> filter.doFilter(request, response, filterChain));
        assertTrue(ex.getRootCause() instanceof InvalidStateException);
        verify(mockSession, never()).stepUp(any());
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void withoutStepUpSupportAuthTimeIsNotTrusted() throws Exception {
        KindeStepUpFilter maxAgeOnly = new KindeStepUpFilter();
        maxAgeOnly.init(config(null, "300"));
        when(mockSession.isStepUpSupported()).thenReturn(false);
        when(mockSession.stepUp(any())).thenThrow(new StepUpNotSupportedException("not supported"));
        signedInWith(tokens(null, now() - 10));

        ServletException ex = assertThrows(ServletException.class,
                () -> maxAgeOnly.doFilter(request, response, filterChain));
        assertTrue(ex.getRootCause() instanceof StepUpNotSupportedException);
        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void stepUpSupportIsReadFromDiscoveryOnce() throws Exception {
        signedInWith(tokens("mfa", now() - 10));

        filter.doFilter(request, response, filterChain);
        filter.doFilter(request, response, filterChain);

        verify(mockSession, times(1)).isStepUpSupported();
        verify(filterChain, times(2)).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void ordinaryCodeAndErrorParametersOfTheApplicationAreLeftAlone() throws Exception {
        signedInWith(tokens("mfa", now() - 10));
        when(request.getParameter("code")).thenReturn("PROMO-1");
        when(request.getParameter("error")).thenReturn("invalid_amount");
        when(request.getParameter("state")).thenReturn("draft");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        verify(mockSession, never()).retrieveTokens();
    }
}
