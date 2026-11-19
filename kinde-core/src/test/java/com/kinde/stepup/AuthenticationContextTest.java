package com.kinde.stepup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationContextTest {

    private static final long NOW = 1_716_800_300L;
    private static final Clock CLOCK = Clock.fixed(Instant.ofEpochSecond(NOW), ZoneOffset.UTC);

    private static Map<String, Object> claims(Object... pairs) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((String) pairs[i], pairs[i + 1]);
        }
        return map;
    }

    @Test
    @DisplayName("reads acr, amr and auth_time from the ID token")
    void readsFromIdToken() {
        AuthenticationContext context = AuthenticationContext.fromClaims(
                claims("sub", "kp_1", "acr", "mfa", "amr", List.of("pwd", "totp"), "auth_time", 1_716_800_000L),
                claims("sub", "kp_1"),
                true);
        assertEquals(Optional.of(AcrValue.MFA), context.getAcr());
        assertEquals(List.of("pwd", "totp"), context.getAmr());
        assertEquals(Optional.of(1_716_800_000L), context.getAuthTime());
    }

    @Test
    @DisplayName("falls back to the access token for each claim")
    void fallsBackToAccessToken() {
        AuthenticationContext context = AuthenticationContext.fromClaims(
                claims("sub", "kp_1"),
                claims("sub", "kp_1", "acr", "PHR", "amr", List.of("passkey"), "auth_time", 1_716_800_000),
                true);
        assertEquals(Optional.of(AcrValue.PHR), context.getAcr());
        assertEquals(List.of("passkey"), context.getAmr());
        assertEquals(Optional.of(1_716_800_000L), context.getAuthTime());
    }

    @Test
    @DisplayName("ID token auth_time wins over the access token's")
    void idTokenAuthTimeWins() {
        AuthenticationContext context = AuthenticationContext.fromClaims(
                claims("auth_time", 100L), claims("auth_time", 200L), true);
        assertEquals(Optional.of(100L), context.getAuthTime());
    }

    @Test
    @DisplayName("tokens of two different users give an empty context")
    void differentSubjects() {
        AuthenticationContext context = AuthenticationContext.fromClaims(
                claims("sub", "kp_1", "acr", "mfa", "auth_time", NOW),
                claims("sub", "kp_2", "acr", "mfa"),
                true);
        assertEquals(Optional.empty(), context.getAcr());
        assertEquals(Optional.empty(), context.getAuthTime());
        assertFalse(context.satisfies(AcrValue.MFA, null));
    }

    @Test
    @DisplayName("auth_time is left out when it cannot be trusted")
    void untrustedAuthTime() {
        AuthenticationContext context = AuthenticationContext.fromClaims(
                claims("acr", "mfa", "auth_time", NOW), (Map<String, Object>) null, false);
        assertEquals(Optional.empty(), context.getAuthTime());
        assertFalse(context.satisfies(null, 300, CLOCK, 0));
    }

    @Test
    @DisplayName("malformed claims are ignored")
    void malformedClaims() {
        AuthenticationContext context = AuthenticationContext.fromClaims(
                claims("acr", 5, "amr", "pwd", "auth_time", "yesterday"), (Map<String, Object>) null, true);
        assertEquals(Optional.empty(), context.getAcr());
        assertTrue(context.getAmr().isEmpty());
        assertEquals(Optional.empty(), context.getAuthTime());
    }

    @Test
    @DisplayName("an absent acr meets no acr requirement, and no requirement is always met")
    void absentAcr() {
        AuthenticationContext context = AuthenticationContext.fromClaims(
                claims("auth_time", NOW), (Map<String, Object>) null, true);
        assertFalse(context.satisfies(AcrValue.MFA, null));
        assertFalse(context.satisfies(AcrValue.PHR, null));
        assertTrue(context.satisfies(null, null));
        assertFalse(AuthenticationContext.empty().satisfies(AcrValue.MFA, null));
        assertTrue(AuthenticationContext.empty().satisfies(null, null));
    }

    @Test
    @DisplayName("phr meets an mfa requirement but not the reverse")
    void acrRequirement() {
        AuthenticationContext phr = AuthenticationContext.fromClaims(claims("acr", "phr"), (Map<String, Object>) null, true);
        AuthenticationContext mfa = AuthenticationContext.fromClaims(claims("acr", "mfa"), (Map<String, Object>) null, true);
        assertTrue(phr.satisfies(AcrValue.MFA, null));
        assertTrue(phr.satisfies(AcrValue.PHR, null));
        assertTrue(mfa.satisfies(AcrValue.MFA, null));
        assertFalse(mfa.satisfies(AcrValue.PHR, null));
    }

    @Test
    @DisplayName("max_age compares auth_time with the clock, with optional leeway")
    void maxAgeRequirement() {
        AuthenticationContext context = AuthenticationContext.fromClaims(
                claims("acr", "mfa", "auth_time", NOW - 300), (Map<String, Object>) null, true);
        assertTrue(context.satisfies(AcrValue.MFA, 300, CLOCK, 0));
        assertFalse(context.satisfies(AcrValue.MFA, 299, CLOCK, 0));
        assertTrue(context.satisfies(AcrValue.MFA, 299, CLOCK, 5));
        assertFalse(context.satisfies(null, 0, CLOCK, 0));
        assertThrows(IllegalArgumentException.class, () -> context.satisfies(null, -1, CLOCK, 0));
    }

    @Test
    @DisplayName("a missing auth_time meets no max_age requirement")
    void missingAuthTime() {
        AuthenticationContext context = AuthenticationContext.fromClaims(
                claims("acr", "mfa"), (Map<String, Object>) null, true);
        assertFalse(context.satisfies(AcrValue.MFA, 300, CLOCK, 0));
        assertTrue(context.satisfies(AcrValue.MFA, null, CLOCK, 0));
    }
}
