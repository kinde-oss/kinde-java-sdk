package com.kinde.servlet;

import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.callback.KindeCallbackSupport;
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
                    // A step-up restarts as the same step-up, never as an ordinary login
                    AuthorizationUrl pending = KindeCallbackSupport.pendingAuthorization(req);
                    AuthorizationUrl authorizationUrl = KindeCallbackSupport.restartAfterExpiredLink(
                            kindeClientSession, kindeAuthenticationAction,
                            pending == null ? null : pending.getAcrValues(),
                            pending == null || pending.getMaxAge() == null ? null : String.valueOf(pending.getMaxAge()));
                    req.getSession().setAttribute(AUTHORIZATION_URL, authorizationUrl);
                    resp.sendRedirect(KindeCallbackSupport.appendReauthParameters(
                            authorizationUrl.getUrl().toString(), urlParams, kindeAuthenticationAction));
                    return;
                } catch (IllegalArgumentException ex) {
                    throw new ServletException("Error parsing reauth state: " + ex.getMessage(), ex);
                }
            }
        }

        String code = req.getParameter("code");
        if (errorParam != null && !errorParam.isBlank()) {
            // Kinde returned an OAuth error (for example unmet_authentication_requirements after
            // a step-up). The state is validated first; the session's tokens are left untouched.
            KindeCallbackSupport.validateCallback(req, KindeCallbackSupport.pendingAuthorization(req));
            throw new ServletException("Authorization failed: " + errorParam);
        }
        String invitationCode = optionalQueryParam(req, INVITATION_CODE);
        String connectionId = optionalQueryParam(req, CONNECTION_ID);
        if (code == null) {
            String postLoginUrl = req.getParameter(POST_LOGIN_URL);
            if (postLoginUrl == null) {
                throw new ServletException("Must provide a POST_LOGIN_URL to use this servlet effectively");
            }
            // Redirect to the OAuth provider's authorization page
            KindeClientSession kindeClientSession = createKindeClientSession(req);
            AuthorizationUrl authorizationUrl = startAuthorization(
                    kindeClientSession, kindeAuthenticationAction, req, invitationCode, connectionId);
            req.getSession().setAttribute(AUTHORIZATION_URL,authorizationUrl);
            req.getSession().setAttribute(POST_LOGIN_URL,postLoginUrl);
            resp.sendRedirect(authorizationUrl.getUrl().toString());
        } else {
            // Exchange the authorization code for an access token
            AuthorizationUrl authorizationUrl = KindeCallbackSupport.pendingAuthorization(req);
            KindeCallbackSupport.validateCallback(req, authorizationUrl);
            try {
                String postLoginUrl = (String)req.getSession().getAttribute(POST_LOGIN_URL);
                KindeClientSession kindeClientSession = KindeSingleton.getInstance().getKindeClient().initClientSession(code,authorizationUrl);
                KindeTokens kindeTokens = kindeClientSession.retrieveTokens();
                req.getSession().setAttribute(KINDE_TOKENS,kindeTokens);
                req.getSession().setAttribute(ACCESS_TOKEN,kindeTokens.getAccessToken());
                UserInfo userInfo = kindeClientSession.retrieveUserInfo();
                Principal principal = new KindePrincipal(kindeTokens.getAccessToken().getUser(), kindeTokens.getAccessToken().getPermissions(), userInfo);
                req.getSession().setAttribute(AUTHENTICATED_USER,principal);
                req.getSession().setAttribute(ID_TOKEN,kindeTokens.getIdToken());
                // The new tokens replace all of the stored ones, so nothing issued before this
                // authorization (such as a refresh token from before a step-up) is left behind
                req.getSession().setAttribute(REFRESH_TOKEN,kindeTokens.getRefreshToken());
                // Single use: the same callback cannot be replayed
                req.getSession().setAttribute(AUTHORIZATION_URL, authorizationUrl.consumed());

                resp.sendRedirect(postLoginUrl);
            } catch (Exception e) {
                throw new ServletException("OAuth token exchange failed", e);
            }
        }
    }

    private static AuthorizationUrl startAuthorization(
            KindeClientSession kindeClientSession,
            KindeAuthenticationAction kindeAuthenticationAction,
            HttpServletRequest req,
            String invitationCode,
            String connectionId) throws ServletException {
        if (kindeAuthenticationAction == KindeAuthenticationAction.LOGIN) {
            return connectionId != null
                    ? kindeClientSession.login(invitationCode, connectionId)
                    : kindeClientSession.login(invitationCode);
        }
        if (kindeAuthenticationAction == KindeAuthenticationAction.REGISTER) {
            return connectionId != null
                    ? kindeClientSession.register(invitationCode, connectionId)
                    : kindeClientSession.register(invitationCode);
        }
        if (kindeAuthenticationAction == KindeAuthenticationAction.CREATE_ORG) {
            String orgName = req.getParameter(ORG_NAME);
            if (orgName == null || orgName.isBlank()) {
                throw new ServletException("Must provide org_name query parameter to create an organisation.");
            }
            return connectionId != null
                    ? kindeClientSession.createOrg(orgName.trim(), invitationCode, connectionId)
                    : kindeClientSession.createOrg(orgName.trim(), invitationCode);
        }
        if (kindeAuthenticationAction == KindeAuthenticationAction.STEP_UP) {
            return KindeCallbackSupport.startStepUp(
                    kindeClientSession, optionalQueryParam(req, ACR_VALUES), optionalQueryParam(req, MAX_AGE));
        }
        throw new ServletException("Unknown authentication action: " + kindeAuthenticationAction);
    }

    private static String optionalQueryParam(HttpServletRequest req, String name) {
        String raw = req.getParameter(name);
        return (raw != null && !raw.isBlank()) ? raw.trim() : null;
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
