package com.kinde.servlet;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.constants.KindeAuthenticationAction;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeTokens;
import com.kinde.user.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.kinde.constants.KindeJ2eeConstants.AUTHORIZATION_URL;
import static com.kinde.constants.KindeJ2eeConstants.POST_LOGIN_URL;
import static org.mockito.Mockito.*;

public class KindeAuthenticationServletTest {

    private KindeAuthenticationServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private KindeClientSession mockSession;
    private AuthorizationUrl mockAuthUrl;
    private KindeTokens mockTokens;
    private UserInfo mockUserInfo;

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
    public void testDoGet_LoginLinkExpired() throws Exception {
        when(request.getParameter("error")).thenReturn("login_link_expired");
        String encodedState = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString("foo=bar".getBytes(StandardCharsets.UTF_8));
        when(request.getParameter("reauth_state")).thenReturn(encodedState);
        when(request.getParameter(POST_LOGIN_URL)).thenReturn("http://example.com");
        when(mockAuthUrl.getUrl()).thenReturn(new URL("http://example.com"));
        when(mockSession.login()).thenReturn(mockAuthUrl);

        servlet.doGet(request, response, KindeAuthenticationAction.LOGIN);

        verify(response, atLeastOnce()).sendRedirect(anyString());
    }

    @Test
    public void testDoGet_NoCode_RedirectToAuthorization() throws Exception {
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter(POST_LOGIN_URL)).thenReturn("http://example.com");
        when(mockAuthUrl.getUrl()).thenReturn(new URL("http://test.kinde.com"));
        when(mockSession.login()).thenReturn(mockAuthUrl);

        servlet.doGet(request, response, KindeAuthenticationAction.LOGIN);

        verify(response).sendRedirect("http://test.kinde.com");
    }

    @Test
    public void testDoGet_WithCode_TokenExchange() throws Exception {
        when(request.getParameter("code")).thenReturn("authCode");
        when(session.getAttribute(AUTHORIZATION_URL)).thenReturn(mockAuthUrl);
        when(session.getAttribute(POST_LOGIN_URL)).thenReturn("http://redirect.com");
        when(mockSession.retrieveTokens()).thenReturn(mockTokens);
        when(mockTokens.getAccessToken()).thenReturn(mock(AccessToken.class));
        when(mockSession.retrieveUserInfo()).thenReturn(mockUserInfo);

        servlet.doGet(request, response, KindeAuthenticationAction.LOGIN);

        verify(response).sendRedirect("http://redirect.com");
    }

    @Test(expected = ServletException.class)
    public void testDoGet_MissingPostLoginUrl_ThrowsException() throws Exception {
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter(POST_LOGIN_URL)).thenReturn(null);

        servlet.doGet(request, response, KindeAuthenticationAction.LOGIN);
    }
}