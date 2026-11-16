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
}
