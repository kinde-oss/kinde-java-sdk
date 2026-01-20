package com.kinde.filter;

import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.constants.KindeAuthenticationAction;
import com.kinde.principal.KindePrincipal;
import com.kinde.servlet.KindeSingleton;
import com.kinde.token.KindeTokens;
import com.kinde.user.UserInfo;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.kinde.constants.KindeConstants.*;
import static com.kinde.constants.KindeJ2eeConstants.*;
import com.kinde.session.KindeRequestParameters;

public abstract class KindeAuthenticationFilter implements Filter {
    protected void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain, KindeAuthenticationAction kindeAuthenticationAction) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // Handle login_link_expired error
        String errorParam = req.getParameter("error");
        if ("login_link_expired".equalsIgnoreCase(errorParam)) {
            String reauthStateParam = req.getParameter("reauth_state");
            if (reauthStateParam != null && !reauthStateParam.isBlank()) {
                try {
                    String normalized = reauthStateParam.replaceAll("\\s", "");
                    String urlParams = new String(Base64.getUrlDecoder().decode(normalized), StandardCharsets.UTF_8);
                    KindeClientSession kindeClientSession = createKindeClientSession(req);
                    AuthorizationUrl authorizationUrl = kindeClientSession.login();
                    req.getSession().setAttribute(AUTHORIZATION_URL, authorizationUrl);
                    String redirectUrl = authorizationUrl.getUrl().toString();
                    if (redirectUrl.contains("?")) {
                        redirectUrl = redirectUrl + "&" + urlParams;
                    } else {
                        redirectUrl = redirectUrl + "?" + urlParams;
                    }
                    resp.sendRedirect(redirectUrl);
                    return;
                } catch (Exception ex) {
                    throw new ServletException("Error parsing reauth state: " + ex.getMessage(), ex);
                }
            }
        }

        String code = req.getParameter("code");
        Principal userPrincipal = (Principal) req.getSession().getAttribute(AUTHENTICATED_USER);
        AuthorizationUrl authorizationUrl = (AuthorizationUrl)req.getSession().getAttribute(AUTHORIZATION_URL);
        if (userPrincipal == null || authorizationUrl == null) {
            // Redirect to the OAuth provider's authorization page
            KindeClientSession kindeClientSession = createKindeClientSession(req);
            
            // Build parameters map for connection_id support
            Map<String, String> parameters = new HashMap<>();
            String connectionId = req.getParameter(com.kinde.constants.KindeConstants.CONNECTION_ID);
            if (connectionId != null && !connectionId.isEmpty()) {
                parameters.put(KindeRequestParameters.CONNECTION_ID, connectionId);
            }
            
            if (kindeAuthenticationAction == KindeAuthenticationAction.LOGIN) {
                if (parameters.isEmpty()) {
                    authorizationUrl = kindeClientSession.login();
                } else {
                    Map<String, String> loginParams = new HashMap<>(parameters);
                    loginParams.put("supports_reauth", "true");
                    authorizationUrl = kindeClientSession.authorizationUrlWithParameters(loginParams);
                }
            } else if (kindeAuthenticationAction == KindeAuthenticationAction.REGISTER) {
                if (parameters.isEmpty()) {
                    authorizationUrl = kindeClientSession.register();
                } else {
                    Map<String, String> registerParams = new HashMap<>(parameters);
                    registerParams.put("prompt", "create");
                    authorizationUrl = kindeClientSession.authorizationUrlWithParameters(registerParams);
                }
            } else if (kindeAuthenticationAction == KindeAuthenticationAction.CREATE_ORG) {
                if (req.getParameter(ORG_NAME) == null) {
                    throw new ServletException("Must proved org_name query parameter to create an organisation.");
                }
                if (parameters.isEmpty()) {
                    authorizationUrl = kindeClientSession.createOrg(req.getParameter(ORG_NAME));
                } else {
                    Map<String, String> createOrgParams = new HashMap<>(parameters);
                    createOrgParams.put("prompt", "create");
                    createOrgParams.put("org_name", req.getParameter(ORG_NAME));
                    authorizationUrl = kindeClientSession.authorizationUrlWithParameters(createOrgParams);
                }
            } else {
                throw new ServletException("Unknown authentication action: " + kindeAuthenticationAction);
            }
            
            if (authorizationUrl == null) {
                throw new ServletException("Failed to generate authorization URL");
            }
            req.getSession().setAttribute(AUTHORIZATION_URL,authorizationUrl);
            resp.sendRedirect(authorizationUrl.getUrl().toString());
        } else if (code != null) {
            // Exchange the authorization code for an access token
            try {
                KindeClientSession kindeClientSession = KindeSingleton.getInstance().getKindeClient().initClientSession(code,authorizationUrl);
                KindeTokens kindeTokens = kindeClientSession.retrieveTokens();
                req.getSession().setAttribute(KINDE_TOKENS,kindeTokens);
                req.getSession().setAttribute(ACCESS_TOKEN,kindeTokens.getAccessToken());
                UserInfo userInfo = kindeClientSession.retrieveUserInfo();
                Principal principal = new KindePrincipal(kindeTokens.getAccessToken().getUser(), kindeTokens.getAccessToken().getPermissions(), userInfo);
                req.getSession().setAttribute(AUTHENTICATED_USER,principal);
                req.getSession().setAttribute(ID_TOKEN,kindeTokens.getIdToken());
                req.getSession().setAttribute(REFRESH_TOKEN,kindeTokens.getRefreshToken());
                resp.sendRedirect(req.getRequestURI());
            } catch (Exception e) {
                throw new ServletException("OAuth token exchange failed", e);
            }
        } else {
            // userPrincipal is not null here (otherwise we'd be in the first if block)
            HttpServletRequest wrappedRequest = new KindeHttpRequestWrapper(req, userPrincipal);
            filterChain.doFilter(wrappedRequest,servletResponse);
        }
    }

    private static KindeClientSession createKindeClientSession(HttpServletRequest req) {
        return KindeSingleton
                .getInstance()
                .getKindeClientBuilder()
                .redirectUri(req.getRequestURL().toString())
                .grantType(AuthorizationType.CODE)
                .orgCode(req.getParameter(ORG_CODE))
                .lang(req.getParameter(LANG))
                .scopes(SCOPE)
                .build()
                .clientSession();
    }
}
