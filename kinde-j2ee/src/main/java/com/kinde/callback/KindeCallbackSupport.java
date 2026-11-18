package com.kinde.callback;

import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationCallback;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.constants.KindeAuthenticationAction;
import com.kinde.exceptions.InvalidStateException;
import com.kinde.exceptions.KindeCallbackException;
import com.kinde.exceptions.StepUpNotSupportedException;
import com.kinde.session.KindeRequestParameters;
import com.kinde.stepup.AcrValue;
import com.kinde.token.KindeTokens;
import com.kinde.stepup.StepUpRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import static com.kinde.constants.KindeJ2eeConstants.AUTHORIZATION_URL;
import static com.kinde.constants.KindeJ2eeConstants.KINDE_CALLBACK_ERROR;

/**
 * Callback validation and step-up helpers shared by the Kinde servlets and filters.
 */
public final class KindeCallbackSupport {

    private KindeCallbackSupport() {
    }

    /** The authorization request stored in the session, or null. */
    public static AuthorizationUrl pendingAuthorization(HttpServletRequest req) {
        Object pending = req.getSession().getAttribute(AUTHORIZATION_URL);
        return pending instanceof AuthorizationUrl ? (AuthorizationUrl) pending : null;
    }

    /** Whether the session holds an authorization request that is still waiting for its callback. */
    public static boolean isAwaitingCallback(AuthorizationUrl pending) {
        return pending != null && pending.getState() != null;
    }

    /**
     * Validates the {@code state} of a callback, and rejects the callback if it carries an
     * OAuth {@code error}.
     *
     * <p>On an OAuth error the pending request is marked as consumed, the session's tokens
     * are left untouched, and the typed {@link KindeCallbackException} is both set as the
     * {@link com.kinde.constants.KindeJ2eeConstants#KINDE_CALLBACK_ERROR} request attribute
     * and thrown as the root cause of a {@link ServletException}, so it can be mapped with an
     * {@code <error-page>} for its exception type.
     *
     * @throws ServletException if the state is missing or wrong, or the callback carries an error
     */
    public static void validateCallback(HttpServletRequest req, AuthorizationUrl pending) throws ServletException {
        try {
            AuthorizationCallback.validate(
                    pending,
                    req.getParameter("state"),
                    req.getParameter("error"),
                    req.getParameter("error_description"));
        } catch (InvalidStateException e) {
            throw new ServletException("Invalid state parameter on the authorization callback", e);
        } catch (KindeCallbackException e) {
            // The authorization response has arrived: this request is finished
            req.getSession().setAttribute(AUTHORIZATION_URL, pending.consumed());
            req.setAttribute(KINDE_CALLBACK_ERROR, e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Starts a step-up authentication for the given requirement.
     *
     * @param acrValues required context ({@code mfa} / {@code phr}), or null
     * @param maxAge maximum age of the last authentication in seconds, as text, or null
     * @throws ServletException if the requirement is missing or invalid, or the Kinde
     *         environment does not support step-up
     */
    public static AuthorizationUrl startStepUp(KindeClientSession session, String acrValues, String maxAge)
            throws ServletException {
        try {
            StepUpRequest.Builder request = StepUpRequest.builder();
            if (acrValues != null && !acrValues.isBlank()) {
                request.acrValues(acrValues);
            }
            if (maxAge != null && !maxAge.isBlank()) {
                request.parameter(KindeRequestParameters.MAX_AGE, maxAge);
            }
            return session.stepUp(request.build());
        } catch (IllegalArgumentException | StepUpNotSupportedException e) {
            throw new ServletException("Cannot start step-up authentication: " + e.getMessage(), e);
        }
    }

    /**
     * Checks that the tokens from a step-up callback meet the requirement of the request it
     * answers. Does nothing for an ordinary sign-in (a request without {@code acr_values} or
     * {@code max_age}). {@code auth_time} is trusted only when the environment advertises
     * step-up, the same rule the step-up filter uses.
     *
     * @throws ServletException if the requirement is not met; the tokens must not be stored
     */
    public static void requireStepUpMet(KindeClientSession session, KindeTokens tokens, AuthorizationUrl pending)
            throws ServletException {
        if (pending == null || (pending.getAcrValues() == null && pending.getMaxAge() == null)) {
            return;
        }
        AcrValue requiredAcr = AcrValue.strongest(pending.getAcrValues()).orElse(null);
        boolean trustAuthTime = pending.getMaxAge() != null && session.isStepUpSupported();
        if (tokens == null || !tokens.getAuthenticationContext(trustAuthTime).satisfies(requiredAcr, pending.getMaxAge())) {
            throw new ServletException("Step-up authentication completed, but the new tokens do not meet the requirement: acr_values="
                    + pending.getAcrValues() + ", max_age=" + pending.getMaxAge());
        }
    }

    /**
     * Starts a new authorization after Kinde reported {@code error=login_link_expired}.
     *
     * <p>For {@link KindeAuthenticationAction#STEP_UP} the restart is a step-up with the same
     * requirement, never an ordinary login: a plain sign-in must not stand in for the stronger
     * authentication that was asked for. A step-up with no known requirement is rejected.
     *
     * @param acrValues the required context of the step-up being restarted, or null
     * @param maxAge the required maximum age of the step-up being restarted, as text, or null
     */
    public static AuthorizationUrl restartAfterExpiredLink(KindeClientSession session, KindeAuthenticationAction action,
                                                           String acrValues, String maxAge) throws ServletException {
        if (action != KindeAuthenticationAction.STEP_UP) {
            return session.login();
        }
        if ((acrValues == null || acrValues.isBlank()) && (maxAge == null || maxAge.isBlank())) {
            throw new ServletException("Cannot restart step-up authentication after an expired login link: "
                    + "the step-up requirement is not known");
        }
        return startStepUp(session, acrValues, maxAge);
    }

    /**
     * Appends the parameters Kinde returned in {@code reauth_state} to an authorization URL.
     * For a step-up, parameters that would change the requirement or make it non-interactive
     * ({@code acr_values}, {@code max_age}, {@code prompt}) are dropped: they come from the
     * browser, and the requirement is the one the SDK set.
     */
    public static String appendReauthParameters(String authorizationUrl, String reauthParameters, KindeAuthenticationAction action) {
        String params = reauthParameters;
        if (action == KindeAuthenticationAction.STEP_UP && params != null) {
            StringBuilder kept = new StringBuilder();
            for (String pair : params.split("&")) {
                String name = pair.contains("=") ? pair.substring(0, pair.indexOf('=')) : pair;
                String decoded = java.net.URLDecoder.decode(name, java.nio.charset.StandardCharsets.UTF_8).trim();
                if (decoded.isEmpty() || STEP_UP_CONTROLLED.contains(decoded.toLowerCase(java.util.Locale.ROOT))) {
                    continue;
                }
                if (kept.length() > 0) {
                    kept.append('&');
                }
                kept.append(pair);
            }
            params = kept.toString();
        }
        if (params == null || params.isEmpty()) {
            return authorizationUrl;
        }
        return authorizationUrl + (authorizationUrl.contains("?") ? "&" : "?") + params;
    }

    private static final java.util.Set<String> STEP_UP_CONTROLLED = java.util.Set.of(
            KindeRequestParameters.ACR_VALUES, KindeRequestParameters.MAX_AGE, KindeRequestParameters.PROMPT);
}
