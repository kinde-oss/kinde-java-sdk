package com.kinde.filter;

import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.callback.KindeCallbackSupport;
import com.kinde.stepup.AcrValue;
import com.kinde.constants.KindeAuthenticationAction;
import com.kinde.principal.KindeAuthentication;
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

import static com.kinde.constants.KindeConstants.*;
import static com.kinde.constants.KindeJ2eeConstants.*;

public abstract class KindeAuthenticationFilter implements Filter {

    private volatile Boolean stepUpSupported;

    protected void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain, KindeAuthenticationAction kindeAuthenticationAction) throws IOException, ServletException {
        doFilter(servletRequest, servletResponse, filterChain, kindeAuthenticationAction, null, null);
    }

    /**
     * @param requiredAcrValues for {@link KindeAuthenticationAction#STEP_UP}: the authentication
     *        context the session must have ({@code mfa} / {@code phr}), or null
     * @param requiredMaxAge for {@link KindeAuthenticationAction#STEP_UP}: the maximum age, in
     *        seconds, of the user's last authentication, or null
     */
    protected void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain, KindeAuthenticationAction kindeAuthenticationAction, String requiredAcrValues, Integer requiredMaxAge) throws IOException, ServletException {
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
                    // A step-up filter restarts with its own requirement, never as an ordinary login
                    AuthorizationUrl authorizationUrl = KindeCallbackSupport.restartAfterExpiredLink(
                            kindeClientSession, kindeAuthenticationAction, requiredAcrValues,
                            requiredMaxAge == null ? null : String.valueOf(requiredMaxAge));
                    req.getSession().setAttribute(AUTHORIZATION_URL, authorizationUrl);
                    resp.sendRedirect(KindeCallbackSupport.appendReauthParameters(
                            authorizationUrl.getUrl().toString(), urlParams, kindeAuthenticationAction));
                    return;
                } catch (ServletException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new ServletException("Error parsing reauth state: " + ex.getMessage(), ex);
                }
            }
        }

        String code = req.getParameter("code");
        String invitationCode = optionalQueryParam(req, INVITATION_CODE);
        String connectionId = optionalQueryParam(req, CONNECTION_ID);
        // The principal and the tokens come from one session attribute, so a callback completing
        // in parallel cannot mix two sign-ins. The step-up check and the wrapped request both use
        // this pair. A session without it fails the step-up check and goes through a step-up.
        Object storedAuthentication = req.getSession().getAttribute(KINDE_AUTHENTICATION);
        KindeAuthentication authentication = storedAuthentication instanceof KindeAuthentication
                ? (KindeAuthentication) storedAuthentication : null;
        Principal userPrincipal = authentication != null
                ? authentication.getPrincipal()
                : (Principal) req.getSession().getAttribute(AUTHENTICATED_USER);
        KindeTokens sessionTokens = authentication != null ? authentication.getTokens() : null;
        AuthorizationUrl authorizationUrl = KindeCallbackSupport.pendingAuthorization(req);
        boolean stepUp = kindeAuthenticationAction == KindeAuthenticationAction.STEP_UP;
        String maxAgeText = requiredMaxAge == null ? null : String.valueOf(requiredMaxAge);

        // While an authorization is pending, a request carrying a code or an error is its
        // callback, and a missing or wrong state is rejected. With nothing pending, the
        // protected resource's own "code" or "error" parameters are left alone.
        boolean callback = KindeCallbackSupport.isAwaitingCallback(authorizationUrl)
                && (code != null || (errorParam != null && !errorParam.isBlank()));

        if (callback) {
            // Validates the state, and throws if Kinde returned an OAuth error (for example
            // unmet_authentication_requirements). The session's tokens are left untouched then.
            KindeCallbackSupport.validateCallback(req, authorizationUrl);
            // Exchange the authorization code for tokens
            KindeTokens kindeTokens;
            KindeClientSession kindeClientSession;
            boolean trustAuthTime = false;
            try {
                kindeClientSession = KindeSingleton.getInstance().getKindeClient().initClientSession(code,authorizationUrl);
                if (stepUp) {
                    trustAuthTime = stepUpSupported(kindeClientSession);
                }
                kindeTokens = kindeClientSession.retrieveTokens();
            } catch (Exception e) {
                throw new ServletException("OAuth token exchange failed", e);
            }
            // Checked before anything is stored: a rejected step-up leaves the existing sign-in
            // in place. Asking again would only redirect in a loop, so this fails instead.
            if (stepUp && !KindeCallbackSupport.callbackMeets(
                    kindeTokens, requiredAcrValues, requiredMaxAge, authorizationUrl, trustAuthTime)) {
                req.getSession().setAttribute(AUTHORIZATION_URL, authorizationUrl.consumed());
                throw new ServletException("Step-up authentication completed, but the new tokens do not meet the requirement: acr_values="
                        + requiredAcrValues + ", max_age=" + requiredMaxAge);
            }
            try {
                UserInfo userInfo = kindeClientSession.retrieveUserInfo();
                Principal principal = new KindePrincipal(kindeTokens.getAccessToken().getUser(), kindeTokens.getAccessToken().getPermissions(), userInfo);
                // The new tokens replace all of the stored ones, so nothing issued before this
                // authorization (such as a refresh token from before a step-up) is left behind
                req.getSession().setAttribute(KINDE_TOKENS,kindeTokens);
                req.getSession().setAttribute(ACCESS_TOKEN,kindeTokens.getAccessToken());
                req.getSession().setAttribute(AUTHENTICATED_USER,principal);
                req.getSession().setAttribute(ID_TOKEN,kindeTokens.getIdToken());
                req.getSession().setAttribute(REFRESH_TOKEN,kindeTokens.getRefreshToken());
                req.getSession().setAttribute(KINDE_AUTHENTICATION, new KindeAuthentication(principal, kindeTokens));
                // Single use: the same callback cannot be replayed
                req.getSession().setAttribute(AUTHORIZATION_URL, authorizationUrl.consumed());
            } catch (Exception e) {
                throw new ServletException("OAuth token exchange failed", e);
            }
            resp.sendRedirect(req.getRequestURI());
        } else if (invitationCode != null) {
            // Invitation code always starts a new auth flow, even if already authenticated
            KindeClientSession kindeClientSession = createKindeClientSession(req);
            authorizationUrl = startAuthorization(kindeClientSession, kindeAuthenticationAction, req, invitationCode, connectionId, requiredAcrValues, maxAgeText);
            req.getSession().setAttribute(AUTHORIZATION_URL, authorizationUrl);
            resp.sendRedirect(authorizationUrl.getUrl().toString());
        } else if (userPrincipal == null || authorizationUrl == null) {
            // Redirect to the OAuth provider's authorization page
            KindeClientSession kindeClientSession = createKindeClientSession(req);
            authorizationUrl = startAuthorization(kindeClientSession, kindeAuthenticationAction, req, null, connectionId, requiredAcrValues, maxAgeText);
            req.getSession().setAttribute(AUTHORIZATION_URL,authorizationUrl);
            resp.sendRedirect(authorizationUrl.getUrl().toString());
        } else if (stepUp && !meetsStoredRequirement(req, userPrincipal, sessionTokens, requiredAcrValues, requiredMaxAge)) {
            // Signed in, but not strongly or recently enough: ask Kinde for what is missing.
            // The current tokens stay in place until the callback replaces them. Without
            // step-up support this fails rather than trusting auth_time.
            KindeClientSession kindeClientSession = createKindeClientSession(req);
            authorizationUrl = KindeCallbackSupport.startStepUp(kindeClientSession, requiredAcrValues, maxAgeText);
            req.getSession().setAttribute(AUTHORIZATION_URL, authorizationUrl);
            resp.sendRedirect(authorizationUrl.getUrl().toString());
        } else {
            HttpServletRequest wrappedRequest = new KindeHttpRequestWrapper(req, userPrincipal);
            filterChain.doFilter(wrappedRequest,servletResponse);
        }
    }

    private boolean meetsStoredRequirement(HttpServletRequest req, Principal principal, KindeTokens tokens,
                                           String requiredAcrValues, Integer requiredMaxAge) {
        if (tokens == null || !sameSubject(principal, tokens)) {
            // Fails closed: the user is sent through a step-up, whose callback stores a
            // matching principal and tokens
            return false;
        }
        boolean trustAuthTime = stepUpSupported != null
                ? stepUpSupported
                : stepUpSupported(createKindeClientSession(req));
        return meetsRequirement(tokens, requiredAcrValues, requiredMaxAge, trustAuthTime);
    }

    /** Whether the principal and the tokens belong to the same user. */
    private static boolean sameSubject(Principal principal, KindeTokens tokens) {
        String name = principal == null ? null : principal.getName();
        String subject = tokens.getAccessToken() == null ? null : tokens.getAccessToken().getUser();
        return name != null && name.equals(subject);
    }

    private static boolean meetsRequirement(KindeTokens tokens, String requiredAcrValues, Integer requiredMaxAge, Boolean trustAuthTime) {
        AcrValue requiredAcr = AcrValue.strongest(requiredAcrValues).orElse(null);
        return tokens.getAuthenticationContext(Boolean.TRUE.equals(trustAuthTime)).satisfies(requiredAcr, requiredMaxAge);
    }

    /** Whether the environment advertises step-up, read from discovery once per filter. */
    private boolean stepUpSupported(KindeClientSession kindeClientSession) {
        boolean supported = kindeClientSession.isStepUpSupported();
        stepUpSupported = supported;
        return supported;
    }

    private static AuthorizationUrl startAuthorization(
            KindeClientSession kindeClientSession,
            KindeAuthenticationAction kindeAuthenticationAction,
            HttpServletRequest req,
            String invitationCode,
            String connectionId,
            String requiredAcrValues,
            String requiredMaxAge) throws ServletException {
        if (kindeAuthenticationAction == KindeAuthenticationAction.STEP_UP) {
            // For a user who is not signed in, this is a sign-in that must reach the context
            return KindeCallbackSupport.startStepUp(kindeClientSession, requiredAcrValues, requiredMaxAge);
        }
        if (kindeAuthenticationAction == KindeAuthenticationAction.LOGIN) {
            if (connectionId != null) {
                return kindeClientSession.login(invitationCode, connectionId);
            }
            return invitationCode != null ? kindeClientSession.login(invitationCode) : kindeClientSession.login();
        }
        if (kindeAuthenticationAction == KindeAuthenticationAction.REGISTER) {
            if (connectionId != null) {
                return kindeClientSession.register(invitationCode, connectionId);
            }
            return invitationCode != null ? kindeClientSession.register(invitationCode) : kindeClientSession.register();
        }
        if (kindeAuthenticationAction == KindeAuthenticationAction.CREATE_ORG) {
            String orgName = req.getParameter(ORG_NAME);
            if (orgName == null || orgName.isBlank()) {
                throw new ServletException("Must provide org_name query parameter to create an organisation.");
            }
            String trimmedOrgName = orgName.trim();
            if (connectionId != null) {
                return kindeClientSession.createOrg(trimmedOrgName, invitationCode, connectionId);
            }
            return invitationCode != null
                    ? kindeClientSession.createOrg(trimmedOrgName, invitationCode)
                    : kindeClientSession.createOrg(trimmedOrgName);
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
