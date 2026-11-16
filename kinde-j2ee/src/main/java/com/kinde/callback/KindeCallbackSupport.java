package com.kinde.callback;

import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationCallback;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.exceptions.InvalidStateException;
import com.kinde.exceptions.KindeCallbackException;
import com.kinde.exceptions.StepUpNotSupportedException;
import com.kinde.session.KindeRequestParameters;
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
}
