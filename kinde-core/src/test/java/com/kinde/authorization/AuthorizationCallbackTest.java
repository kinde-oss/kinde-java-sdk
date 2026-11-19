package com.kinde.authorization;

import com.kinde.exceptions.InvalidStateException;
import com.kinde.exceptions.KindeCallbackException;
import com.kinde.exceptions.LoginRequiredException;
import com.kinde.exceptions.UnmetAuthenticationRequirementsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationCallbackTest {

    private static AuthorizationUrl pending() throws Exception {
        return new AuthorizationUrl(new URL("https://example.kinde.com/oauth2/auth"), null, "state-123", "mfa", 300);
    }

    @Test
    @DisplayName("a matching state with no error passes")
    void matchingState() throws Exception {
        assertDoesNotThrow(() -> AuthorizationCallback.validate(pending(), "state-123", null, null));
    }

    @Test
    @DisplayName("a missing, wrong or unknown state is rejected")
    void invalidState() throws Exception {
        AuthorizationUrl pending = pending();
        assertThrows(InvalidStateException.class, () -> AuthorizationCallback.validateState(pending, null));
        assertThrows(InvalidStateException.class, () -> AuthorizationCallback.validateState(pending, ""));
        assertThrows(InvalidStateException.class, () -> AuthorizationCallback.validateState(pending, "state-124"));
        assertThrows(InvalidStateException.class, () -> AuthorizationCallback.validateState(null, "state-123"));
        AuthorizationUrl legacy = new AuthorizationUrl(pending.getUrl(), null);
        assertThrows(InvalidStateException.class, () -> AuthorizationCallback.validateState(legacy, "state-123"));
    }

    @Test
    @DisplayName("a consumed request matches no callback")
    void consumedRequest() throws Exception {
        AuthorizationUrl consumed = pending().consumed();
        assertNull(consumed.getState());
        assertNull(consumed.getCodeVerifier());
        assertEquals("mfa", consumed.getAcrValues());
        assertThrows(InvalidStateException.class, () -> AuthorizationCallback.validateState(consumed, "state-123"));
    }

    @Test
    @DisplayName("an error is only reported after the state is validated")
    void forgedErrorIsRejected() throws Exception {
        AuthorizationUrl pending = pending();
        assertThrows(InvalidStateException.class, () -> AuthorizationCallback.validate(
                pending, "forged", "unmet_authentication_requirements", "nope"));
    }

    @Test
    @DisplayName("unmet_authentication_requirements carries what the request asked for")
    void unmetRequirements() throws Exception {
        AuthorizationUrl pending = pending();
        UnmetAuthenticationRequirementsException ex = assertThrows(
                UnmetAuthenticationRequirementsException.class,
                () -> AuthorizationCallback.validate(pending, "state-123",
                        "unmet_authentication_requirements",
                        "The requested authentication context could not be met"));
        assertEquals("unmet_authentication_requirements", ex.getError());
        assertEquals("The requested authentication context could not be met", ex.getErrorDescription());
        assertEquals("state-123", ex.getState());
        assertEquals("mfa", ex.getAcrValues());
        assertEquals(300, ex.getMaxAge());
    }

    @Test
    @DisplayName("login_required and other errors map to their exception types")
    void otherErrors() throws Exception {
        AuthorizationUrl pending = pending();
        assertThrows(LoginRequiredException.class,
                () -> AuthorizationCallback.validate(pending, "state-123", "login_required", null));
        KindeCallbackException ex = assertThrows(KindeCallbackException.class,
                () -> AuthorizationCallback.validate(pending, "state-123", "access_denied", null));
        assertEquals(KindeCallbackException.class, ex.getClass());
        assertEquals("access_denied", ex.getError());
        assertEquals("", ex.getErrorDescription());
    }

    @Test
    @DisplayName("the allowed age is never less than the time since the request was sent")
    void effectiveMaxAge() throws Exception {
        URL url = new URL("https://example.kinde.com/oauth2/auth");
        AuthorizationUrl fresh = new AuthorizationUrl(url, null, "s", "mfa", 0, 1_000L);
        assertEquals(0, fresh.effectiveMaxAge(1_000L));
        assertEquals(5, fresh.effectiveMaxAge(1_005L));
        assertEquals(0, fresh.effectiveMaxAge(990L));

        AuthorizationUrl window = new AuthorizationUrl(url, null, "s", "mfa", 300, 1_000L);
        assertEquals(300, window.effectiveMaxAge(1_005L));
        assertEquals(400, window.effectiveMaxAge(1_400L));

        assertNull(new AuthorizationUrl(url, null, "s", "mfa", null, 1_000L).effectiveMaxAge(1_005L));
        assertEquals(60, new AuthorizationUrl(url, null, "s", null, 60, null).effectiveMaxAge(9_999L));
        assertEquals(Long.valueOf(1_000L), fresh.consumed().getRequestedAt());
    }
}
