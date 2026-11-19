package com.kinde.callback;

import com.kinde.authorization.AuthorizationUrl;
import com.kinde.stepup.AuthenticationContext;
import com.kinde.token.KindeTokens;
import org.junit.Test;

import java.net.URL;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KindeCallbackSupportTest {

    /** A clock that moves one second forward every time it is read. */
    private static final class TickingClock extends Clock {
        private long next;

        TickingClock(long start) {
            this.next = start;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochSecond(next++);
        }
    }

    private static KindeTokens tokens(String acr, long authTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("acr", acr);
        claims.put("auth_time", authTime);
        KindeTokens tokens = mock(KindeTokens.class);
        when(tokens.getAuthenticationContext(anyBoolean())).thenAnswer(invocation ->
                AuthenticationContext.fromClaims(claims, (Map<String, Object>) null, invocation.getArgument(0)));
        return tokens;
    }

    private static AuthorizationUrl pending(int maxAge, long requestedAt) throws Exception {
        return new AuthorizationUrl(new URL("https://example.kinde.com/oauth2/auth"), null, "s", "mfa", maxAge, requestedAt);
    }

    @Test
    public void theClockIsReadOnceForBothAges() throws Exception {
        // Sent at 1000, verified at 1000, checked at 1005. Reading the clock a second time
        // (1006) for the authentication's age would reject this valid verification.
        boolean met = KindeCallbackSupport.callbackMeets(
                tokens("mfa", 1_000L), "mfa", 0, pending(0, 1_000L), true, new TickingClock(1_005L));

        assertTrue(met);
    }

    @Test
    public void anAuthenticationFromBeforeTheRequestStillFails() throws Exception {
        boolean met = KindeCallbackSupport.callbackMeets(
                tokens("mfa", 990L), "mfa", 0, pending(0, 1_000L), true, new TickingClock(1_005L));

        assertFalse(met);
    }

    @Test
    public void theRequiredContextIsStillChecked() throws Exception {
        boolean met = KindeCallbackSupport.callbackMeets(
                tokens(null, 1_002L), "mfa", 0, pending(0, 1_000L), true, new TickingClock(1_005L));

        assertFalse(met);
    }
}
