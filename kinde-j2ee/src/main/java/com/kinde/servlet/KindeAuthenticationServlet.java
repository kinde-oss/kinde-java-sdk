package com.kinde.servlet;

import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.constants.KindeAuthenticationAction;
import com.kinde.principal.KindePrincipal;
import com.kinde.token.KindeTokens;
import com.kinde.user.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;

import static com.kinde.constants.KindeConstants.*;
import static com.kinde.constants.KindeJ2eeConstants.*;

@Slf4j
public class KindeAuthenticationServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp, KindeAuthenticationAction kindeAuthenticationAction) throws ServletException, IOException {
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
                    redirectUrl = redirectUrl + (redirectUrl.contains("?") ? "&" : "?") + urlParams;
                    resp.sendRedirect(redirectUrl);
                    return;
                } catch (IllegalArgumentException ex) {
                    throw new ServletException("Error parsing reauth state: " + ex.getMessage(), ex);
                }
            }
        }

        String code = req.getParameter("code");
        if (code == null) {
            String postLoginUrl = req.getParameter(POST_LOGIN_URL);
            if (postLoginUrl == null) {
                throw new ServletException("Must provide a POST_LOGIN_URL to use this servlet effectively");
            }
            // Redirect to the OAuth provider's authorization page
            KindeClientSession kindeClientSession = createKindeClientSession(req);
            AuthorizationUrl authorizationUrl = null;
            if (kindeAuthenticationAction == KindeAuthenticationAction.LOGIN) {
                authorizationUrl = kindeClientSession.login();
            } else if (kindeAuthenticationAction == KindeAuthenticationAction.REGISTER) {
                authorizationUrl = kindeClientSession.register();
            } else if (kindeAuthenticationAction == KindeAuthenticationAction.CREATE_ORG) {
                if (req.getParameter(ORG_NAME) == null) {
                    throw new ServletException("Must provide org_name query parameter to create an organisation.");
                }
                authorizationUrl = kindeClientSession.createOrg(req.getParameter(ORG_NAME));
            }
            req.getSession().setAttribute(AUTHORIZATION_URL,authorizationUrl);
            req.getSession().setAttribute(POST_LOGIN_URL,postLoginUrl);
            resp.sendRedirect(authorizationUrl.getUrl().toString());
        } else {
            // Exchange the authorization code for an access token
            try {
                AuthorizationUrl authorizationUrl = (AuthorizationUrl)req.getSession().getAttribute(AUTHORIZATION_URL);
                String postLoginUrl = (String)req.getSession().getAttribute(POST_LOGIN_URL);
                KindeClientSession kindeClientSession = KindeSingleton.getInstance().getKindeClient().initClientSession(code,authorizationUrl);
                KindeTokens kindeTokens = kindeClientSession.retrieveTokens();
                req.getSession().setAttribute(KINDE_TOKENS,kindeTokens);
                req.getSession().setAttribute(ACCESS_TOKEN,kindeTokens.getAccessToken());
                UserInfo userInfo = kindeClientSession.retrieveUserInfo();
                Principal principal = new KindePrincipal(kindeTokens.getAccessToken().getUser(), kindeTokens.getAccessToken().getPermissions(), userInfo);
                req.getSession().setAttribute(AUTHENTICATED_USER,principal);
                req.getSession().setAttribute(ID_TOKEN,kindeTokens.getIdToken());
                req.getSession().setAttribute(REFRESH_TOKEN,kindeTokens.getRefreshToken());

                resp.sendRedirect(postLoginUrl);
            } catch (Exception e) {
                throw new ServletException("OAuth token exchange failed", e);
            }
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
