package com.kinde.servlet;

import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.constants.KindeAuthenticationAction;
import com.kinde.principal.KindePrincipal;
import com.kinde.token.AccessToken;
import com.kinde.token.IDToken;
import com.kinde.token.KindeToken;
import com.kinde.token.RefreshToken;
import com.kinde.user.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static com.kinde.constants.KindeConstants.*;
import static com.kinde.constants.KindeJ2eeConstants.*;

@Slf4j
public class KindeAuthenticationServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp, KindeAuthenticationAction kindeAuthenticationAction) throws ServletException, IOException {
        String code = req.getParameter("code");
        if (code == null) {
            String postLoginUrl = req.getParameter(POST_LOGIN_URL);
            if (postLoginUrl == null) {
                throw new ServletException("Must provide a POST_LOGIN_URL to use this servlet effectivey");
            }
            // Redirect to the OAuth provider's authorization page
            KindeClientSession kindeClientSession = KindeSingleton
                    .getInstance()
                    .getKindeClientBuilder()
                    .redirectUri(req.getRequestURL().toString())
                    .grantType(AuthorizationType.CODE)
                    .orgCode(req.getParameter(ORG_CODE))
                    .lang(req.getParameter(LANG))
                    .scopes(SCOPE)
                    .build()
                    .clientSession();
            AuthorizationUrl authorizationUrl = null;
            if (kindeAuthenticationAction == KindeAuthenticationAction.LOGIN) {
                authorizationUrl = kindeClientSession.login();
            } else if (kindeAuthenticationAction == KindeAuthenticationAction.REGISTER) {
                authorizationUrl = kindeClientSession.register();
            } else if (kindeAuthenticationAction == KindeAuthenticationAction.CREATE_ORG) {
                if (req.getParameter(ORG_NAME) == null) {
                    throw new ServletException("Must proved org_name query parameter to create an organisation.");
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
                List<KindeToken> tokens = kindeClientSession.retrieveTokens();

                tokens.stream().filter(token->token instanceof AccessToken).forEach(token-> {
                    req.getSession().setAttribute(ACCESS_TOKEN,token.token());
                    UserInfo userInfo = kindeClientSession.retrieveUserInfo();
                    Principal principal = new KindePrincipal(token.getUser(), token.getPermissions(), userInfo);
                    req.getSession().setAttribute(AUTHENTICATED_USER,principal);
                });
                tokens.stream().filter(token->token instanceof IDToken).forEach(token->req.getSession().setAttribute(ID_TOKEN,token.token()));
                tokens.stream().filter(token->token instanceof RefreshToken).forEach(token->req.getSession().setAttribute(REFRESH_TOKEN,token.token()));

                resp.sendRedirect(postLoginUrl);
            } catch (Exception e) {
                throw new ServletException("OAuth token exchange failed", e);
            }
        }
    }
}
