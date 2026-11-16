package com.kinde.authorization;

import com.kinde.exceptions.InvalidStateException;
import com.kinde.exceptions.KindeCallbackException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Validation of the redirect Kinde sends back to the callback URL.
 */
public final class AuthorizationCallback {

    private AuthorizationCallback() {
    }

    /**
     * Checks that a callback answers the pending authorization request.
     *
     * @param pending the request that was started, as kept since the redirect to Kinde
     * @param state the {@code state} parameter of the callback
     * @throws InvalidStateException if there is no pending request, or the state is missing
     *         or does not match. The comparison is constant-time.
     */
    public static void validateState(AuthorizationUrl pending, String state) {
        String expected = pending == null ? null : pending.getState();
        if (state == null || state.isEmpty() || expected == null || expected.isEmpty()
                || !MessageDigest.isEqual(
                        state.getBytes(StandardCharsets.UTF_8),
                        expected.getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidStateException("Invalid state parameter");
        }
    }

    /**
     * Validates a callback and throws if it carries an OAuth error.
     *
     * <p>The state is checked first, so a forged error redirect is rejected as an
     * {@link InvalidStateException} and never reported as an answer from Kinde. Returns
     * normally when the state matches and there is no error; the {@code code} can then be
     * exchanged. Either way the pending request is finished: discard it (or keep
     * {@link AuthorizationUrl#consumed()}) so the same callback cannot be replayed.
     *
     * @param pending the request that was started
     * @param state the {@code state} parameter of the callback
     * @param error the {@code error} parameter of the callback, or null
     * @param errorDescription the {@code error_description} parameter of the callback, or null
     * @throws InvalidStateException if the state is missing or does not match
     * @throws com.kinde.exceptions.UnmetAuthenticationRequirementsException if the user cannot
     *         meet the requested authentication context. This is final: don't retry.
     * @throws com.kinde.exceptions.LoginRequiredException for {@code error=login_required}
     * @throws KindeCallbackException for any other error
     */
    public static void validate(AuthorizationUrl pending, String state, String error, String errorDescription) {
        validateState(pending, state);
        if (error != null && !error.isBlank()) {
            throw KindeCallbackException.of(
                    error.trim(), errorDescription, state, pending.getAcrValues(), pending.getMaxAge());
        }
    }
}
