package com.kinde.servlet;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.constants.KindeAuthenticationAction;
import com.kinde.exceptions.InvalidStateException;
import com.kinde.exceptions.KindeCallbackException;
import com.kinde.exceptions.StepUpNotSupportedException;
import com.kinde.exceptions.UnmetAuthenticationRequirementsException;
import com.kinde.stepup.StepUpRequest;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeTokens;
import com.kinde.token.RefreshToken;
import com.kinde.user.UserInfo;
import jakarta.servlet.ServletException;
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
import java.util.Optional;

import static com.kinde.constants.KindeConstants.REFRESH_TOKEN;
import static com.kinde.constants.KindeJ2eeConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KindeStepUpServletTest {

    private KindeAuthenticationServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private KindeClientSession mockSession;
    private MockedStatic<KindeSingleton> kindeSingletonStatic;

    @Before
    public void setUp() {
        servlet = new KindeAuthenticationServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

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
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/step-up"));
        when(singleton.getKindeClient()).thenReturn(mockClient);
        when(mockClient.initClientSession(anyString(), any())).thenReturn(mockSession);
        when(request.getSession()).thenReturn(session);
    }

    @After
    public void tearDown() {
        kindeSingletonStatic.close();
    }

    private static AuthorizationUrl pending() throws Exception {
        return new AuthorizationUrl(new URL("http://test.kinde.com/oauth2/auth"), null, "state-123", "mfa", 300);
    }

    @Test
    public void stepUpStartsWithTheRequestedContext() throws Exception {
        AuthorizationUrl url = pending();
        when(request.getParameter(ACR_VALUES)).thenReturn("mfa");
        when(request.getParameter(MAX_AGE)).thenReturn("300");
        when(request.getParameter(POST_LOGIN_URL)).thenReturn("/transfer");
        when(mockSession.stepUp(any())).thenReturn(url);

        servlet.doGet(request, response, KindeAuthenticationAction.STEP_UP);

        ArgumentCaptor<StepUpRequest> captor = ArgumentCaptor.forClass(StepUpRequest.class);
        verify(mockSession).stepUp(captor.capture());
        assertEquals(Optional.of("mfa"), captor.getValue().getAcrValues());
        assertEquals("300", captor.getValue().getParameters().get("max_age"));
        verify(session).setAttribute(AUTHORIZATION_URL, url);
        verify(session).setAttribute(POST_LOGIN_URL, "/transfer");
        verify(response).sendRedirect("http://test.kinde.com/oauth2/auth");
    }

    @Test
    public void stepUpWithAnInvalidContextIsRejected() throws Exception {
        when(request.getParameter(ACR_VALUES)).thenReturn("gold");
        when(request.getParameter(POST_LOGIN_URL)).thenReturn("/transfer");

        assertThrows(ServletException.class,
                () -> servlet.doGet(request, response, KindeAuthenticationAction.STEP_UP));
        verify(mockSession, never()).stepUp(any());
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void stepUpOnAnUnsupportedEnvironmentIsRejected() throws Exception {
        when(request.getParameter(ACR_VALUES)).thenReturn("mfa");
        when(request.getParameter(POST_LOGIN_URL)).thenReturn("/transfer");
        when(mockSession.stepUp(any())).thenThrow(new StepUpNotSupportedException("not supported"));

        ServletException ex = assertThrows(ServletException.class,
                () -> servlet.doGet(request, response, KindeAuthenticationAction.STEP_UP));
        assertTrue(ex.getRootCause() instanceof StepUpNotSupportedException);
    }

    private static KindeTokens tokensWith(String acr, Long authTime) {
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("acr", acr);
        claims.put("auth_time", authTime);
        KindeTokens tokens = mock(KindeTokens.class);
        when(tokens.getAuthenticationContext(org.mockito.ArgumentMatchers.anyBoolean())).thenAnswer(invocation ->
                com.kinde.stepup.AuthenticationContext.fromClaims(claims, (java.util.Map<String, Object>) null, invocation.getArgument(0)));
        when(tokens.getAccessToken()).thenReturn(mock(AccessToken.class));
        return tokens;
    }

    private void callbackWith(KindeTokens tokens) throws Exception {
        AuthorizationUrl pending = pending();
        when(request.getParameter("code")).thenReturn("authCode");
        when(request.getParameter("state")).thenReturn("state-123");
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(pending);
        when(session.getAttribute(POST_LOGIN_URL)).thenReturn("/transfer");
        when(mockSession.retrieveTokens()).thenReturn(tokens);
        when(mockSession.retrieveUserInfo()).thenReturn(mock(UserInfo.class));
        when(mockSession.isStepUpSupported()).thenReturn(true);
    }

    @Test
    public void aStepUpCallbackWhoseTokensMissTheContextStoresNothing() throws Exception {
        callbackWith(tokensWith(null, System.currentTimeMillis() / 1000));

        ServletException ex = assertThrows(ServletException.class,
                () -> servlet.doGet(request, response, KindeAuthenticationAction.STEP_UP));

        assertTrue(ex.getMessage().contains("do not meet the requirement"));
        verify(session, never()).setAttribute(eq(KINDE_TOKENS), any());
        verify(session, never()).setAttribute(eq(KINDE_AUTHENTICATION), any());
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void aStepUpCallbackWithAnOldAuthenticationStoresNothing() throws Exception {
        callbackWith(tokensWith("mfa", System.currentTimeMillis() / 1000 - 3600));

        assertThrows(ServletException.class,
                () -> servlet.doGet(request, response, KindeAuthenticationAction.STEP_UP));
        verify(session, never()).setAttribute(eq(KINDE_TOKENS), any());
    }

    @Test
    public void withoutStepUpSupportAMaxAgeRequirementFailsClosed() throws Exception {
        callbackWith(tokensWith("mfa", System.currentTimeMillis() / 1000));
        when(mockSession.isStepUpSupported()).thenReturn(false);

        assertThrows(ServletException.class,
                () -> servlet.doGet(request, response, KindeAuthenticationAction.STEP_UP));
        verify(session, never()).setAttribute(eq(KINDE_TOKENS), any());
    }

    @Test
    public void callbackReplacesAllSessionTokensAndConsumesTheRequest() throws Exception {
        KindeTokens tokens = tokensWith("mfa", System.currentTimeMillis() / 1000);
        when(mockSession.isStepUpSupported()).thenReturn(true);
        RefreshToken refreshToken = mock(RefreshToken.class);
        when(tokens.getRefreshToken()).thenReturn(refreshToken);
        when(request.getParameter("code")).thenReturn("authCode");
        when(request.getParameter("state")).thenReturn("state-123");
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(pending());
        when(session.getAttribute(POST_LOGIN_URL)).thenReturn("/transfer");
        when(mockSession.retrieveTokens()).thenReturn(tokens);
        when(mockSession.retrieveUserInfo()).thenReturn(mock(UserInfo.class));

        servlet.doGet(request, response, KindeAuthenticationAction.STEP_UP);

        verify(session).setAttribute(KINDE_TOKENS, tokens);
        verify(session).setAttribute(REFRESH_TOKEN, refreshToken);
        verify(session).setAttribute(eq(KINDE_AUTHENTICATION), any(com.kinde.principal.KindeAuthentication.class));
        ArgumentCaptor<AuthorizationUrl> consumed = ArgumentCaptor.forClass(AuthorizationUrl.class);
        verify(session).setAttribute(eq(AUTHORIZATION_URL), consumed.capture());
        assertNull(consumed.getValue().getState());
        verify(response).sendRedirect("/transfer");
    }

    @Test
    public void callbackWithAWrongStateIsRejectedBeforeTheCodeIsUsed() throws Exception {
        when(request.getParameter("code")).thenReturn("authCode");
        when(request.getParameter("state")).thenReturn("forged");
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(pending());

        ServletException ex = assertThrows(ServletException.class,
                () -> servlet.doGet(request, response, KindeAuthenticationAction.LOGIN));
        assertTrue(ex.getRootCause() instanceof InvalidStateException);
        verify(mockSession, never()).retrieveTokens();
        verify(session, never()).setAttribute(eq(KINDE_TOKENS), any());
    }

    @Test
    public void callbackWithoutAPendingRequestIsRejected() throws Exception {
        when(request.getParameter("code")).thenReturn("authCode");
        when(request.getParameter("state")).thenReturn("state-123");
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(null);

        assertThrows(ServletException.class,
                () -> servlet.doGet(request, response, KindeAuthenticationAction.LOGIN));
        verify(mockSession, never()).retrieveTokens();
    }

    @Test
    public void unmetRequirementsKeepsTheSessionAndSurfacesTheTypedError() throws Exception {
        when(request.getParameter("error")).thenReturn("unmet_authentication_requirements");
        when(request.getParameter("error_description")).thenReturn("The requested authentication context could not be met");
        when(request.getParameter("state")).thenReturn("state-123");
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(pending());

        ServletException ex = assertThrows(ServletException.class,
                () -> servlet.doGet(request, response, KindeAuthenticationAction.STEP_UP));

        assertTrue(ex.getRootCause() instanceof UnmetAuthenticationRequirementsException);
        KindeCallbackException cause = (KindeCallbackException) ex.getRootCause();
        assertEquals("mfa", cause.getAcrValues());
        assertEquals(Integer.valueOf(300), cause.getMaxAge());
        verify(request).setAttribute(KINDE_CALLBACK_ERROR, cause);
        // Existing tokens are untouched, nothing is retried, and the request is consumed
        verify(session, never()).setAttribute(eq(KINDE_TOKENS), any());
        verify(session, never()).setAttribute(eq(REFRESH_TOKEN), any());
        verify(mockSession, never()).stepUp(any());
        verify(response, never()).sendRedirect(anyString());
        ArgumentCaptor<AuthorizationUrl> consumed = ArgumentCaptor.forClass(AuthorizationUrl.class);
        verify(session).setAttribute(eq(AUTHORIZATION_URL), consumed.capture());
        assertNull(consumed.getValue().getState());
    }

    @Test
    public void aForgedErrorRedirectIsRejectedAsAnInvalidState() throws Exception {
        when(request.getParameter("error")).thenReturn("unmet_authentication_requirements");
        when(request.getParameter("state")).thenReturn("forged");
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(pending());

        ServletException ex = assertThrows(ServletException.class,
                () -> servlet.doGet(request, response, KindeAuthenticationAction.STEP_UP));
        assertTrue(ex.getRootCause() instanceof InvalidStateException);
        verify(request, never()).setAttribute(eq(KINDE_CALLBACK_ERROR), any());
        verify(session, never()).setAttribute(eq(AUTHORIZATION_URL), any());
    }

    @Test
    public void anExpiredLoginLinkRestartsThePendingStepUp() throws Exception {
        String reauth = java.util.Base64.getUrlEncoder().encodeToString("foo=bar".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        when(request.getParameter("error")).thenReturn("login_link_expired");
        when(request.getParameter("reauth_state")).thenReturn(reauth);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(pending());
        AuthorizationUrl restarted = new AuthorizationUrl(new URL("http://test.kinde.com/oauth2/auth"), null, "state-456", "mfa", 300);
        when(mockSession.stepUp(any())).thenReturn(restarted);

        servlet.doGet(request, response, KindeAuthenticationAction.STEP_UP);

        ArgumentCaptor<StepUpRequest> captor = ArgumentCaptor.forClass(StepUpRequest.class);
        verify(mockSession).stepUp(captor.capture());
        assertEquals(Optional.of("mfa"), captor.getValue().getAcrValues());
        assertEquals("300", captor.getValue().getParameters().get("max_age"));
        verify(mockSession, never()).login();
        verify(session).setAttribute(AUTHORIZATION_URL, restarted);
        verify(response).sendRedirect("http://test.kinde.com/oauth2/auth?foo=bar");
    }

    @Test
    public void anExpiredLoginLinkWithNoKnownStepUpIsRejected() throws Exception {
        String reauth = java.util.Base64.getUrlEncoder().encodeToString("foo=bar".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        when(request.getParameter("error")).thenReturn("login_link_expired");
        when(request.getParameter("reauth_state")).thenReturn(reauth);
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(null);

        assertThrows(ServletException.class,
                () -> servlet.doGet(request, response, KindeAuthenticationAction.STEP_UP));
        verify(mockSession, never()).login();
        verify(response, never()).sendRedirect(anyString());
    }
}
