package com.kinde.filter;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.constants.KindeAuthenticationAction;
import com.kinde.servlet.KindeSingleton;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.security.Principal;

import static com.kinde.constants.KindeConstants.CONNECTION_ID;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ConnectionIdFilterTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private FilterChain filterChain;
    
    @Mock
    private HttpSession session;
    
    @Mock
    private KindeSingleton kindeSingleton;
    
    @Mock
    private KindeClientBuilder kindeClientBuilder;
    
    @Mock
    private KindeClient kindeClient;
    
    @Mock
    private KindeClientSession kindeClientSession;
    
    @Mock
    private AuthorizationUrl authorizationUrl;

    private KindeLoginFilter filter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new KindeLoginFilter();
        
        when(request.getSession()).thenReturn(session);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/test"));
        when(session.getAttribute(anyString())).thenReturn(null);
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);
    }

    @Test
    @DisplayName("Filter should include connection_id in authorization URL when provided as request parameter")
    public void testFilterWithConnectionId() throws ServletException, IOException {
        // Setup
        String connectionId = "conn_123456789";
        when(request.getParameter(CONNECTION_ID)).thenReturn(connectionId);
        when(request.getParameter("org_code")).thenReturn(null);
        when(request.getParameter("lang")).thenReturn(null);
        
        // Mock KindeSingleton
        KindeSingleton.setInstance(kindeSingleton);
        when(kindeSingleton.getKindeClientBuilder()).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.redirectUri(anyString())).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.grantType(any(AuthorizationType.class))).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.orgCode(any())).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.lang(any())).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.scopes(anyString())).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.build()).thenReturn(kindeClient);
        when(kindeClient.clientSession()).thenReturn(kindeClientSession);
        when(kindeClientSession.authorizationUrlWithParameters(any())).thenReturn(authorizationUrl);
        when(authorizationUrl.getUrl()).thenReturn(new java.net.URL("http://example.com/auth?connection_id=" + connectionId));
        
        // Execute
        filter.doFilter(request, response, filterChain);
        
        // Verify
        verify(kindeClientSession).authorizationUrlWithParameters(argThat(params -> 
            params.containsKey(CONNECTION_ID) && 
            params.get(CONNECTION_ID).equals(connectionId) &&
            params.containsKey("supports_reauth")
        ));
        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Filter should work without connection_id when not provided")
    public void testFilterWithoutConnectionId() throws ServletException, IOException {
        // Setup
        when(request.getParameter(CONNECTION_ID)).thenReturn(null);
        when(request.getParameter("org_code")).thenReturn(null);
        when(request.getParameter("lang")).thenReturn(null);
        
        // Mock KindeSingleton
        KindeSingleton.setInstance(kindeSingleton);
        when(kindeSingleton.getKindeClientBuilder()).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.redirectUri(anyString())).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.grantType(any(AuthorizationType.class))).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.orgCode(any())).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.lang(any())).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.scopes(anyString())).thenReturn(kindeClientBuilder);
        when(kindeClientBuilder.build()).thenReturn(kindeClient);
        when(kindeClient.clientSession()).thenReturn(kindeClientSession);
        when(kindeClientSession.login()).thenReturn(authorizationUrl);
        when(authorizationUrl.getUrl()).thenReturn(new java.net.URL("http://example.com/auth"));
        
        // Execute
        filter.doFilter(request, response, filterChain);
        
        // Verify - should use login() method when no connection_id
        verify(kindeClientSession).login();
        verify(response).sendRedirect(anyString());
    }
}
