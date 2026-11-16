package com.kinde.stepup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StepUpTest {

    @Test
    @DisplayName("phr meets mfa, mfa does not meet phr")
    void acrRanking() {
        assertTrue(AcrValue.PHR.satisfies(AcrValue.MFA));
        assertTrue(AcrValue.MFA.satisfies(AcrValue.MFA));
        assertFalse(AcrValue.MFA.satisfies(AcrValue.PHR));
        assertTrue(AcrValue.MFA.satisfies(null));
    }

    @Test
    @DisplayName("parse is case-insensitive and rejects aliases and unknown values")
    void acrParse() {
        assertEquals(Optional.of(AcrValue.MFA), AcrValue.parse("MFA"));
        assertEquals(Optional.of(AcrValue.PHR), AcrValue.parse(" phr "));
        assertEquals(Optional.empty(), AcrValue.parse("urn:okta:loa:2fa:any"));
        assertEquals(Optional.empty(), AcrValue.parse("gold"));
        assertEquals(Optional.empty(), AcrValue.parse(null));
    }

    @Test
    @DisplayName("strongest applies the highest recognised value and reads aliases as mfa")
    void acrStrongest() {
        assertEquals(Optional.of(AcrValue.PHR), AcrValue.strongest("mfa phr"));
        assertEquals(Optional.of(AcrValue.PHR), AcrValue.strongest("PHR gold"));
        assertEquals(Optional.of(AcrValue.MFA), AcrValue.strongest("urn:okta:loa:2fa:any"));
        assertEquals(Optional.of(AcrValue.MFA),
                AcrValue.strongest("http://schemas.openid.net/pape/policies/2007/06/multi-factor"));
        assertEquals(Optional.empty(), AcrValue.strongest("urn:okta:loa:2fa:any:ifpossible"));
        assertEquals(Optional.empty(), AcrValue.strongest("gold"));
        assertEquals(Optional.empty(), AcrValue.strongest("  "));
        assertEquals(Optional.empty(), AcrValue.strongest(null));
    }

    @Test
    @DisplayName("normalizeAcrValues lower-cases, de-duplicates and rejects anything but mfa/phr")
    void normalizeAcrValues() {
        assertEquals("mfa", StepUp.normalizeAcrValues("MFA"));
        assertEquals("mfa phr", StepUp.normalizeAcrValues("  mfa   PHR mfa "));
        assertThrows(IllegalArgumentException.class, () -> StepUp.normalizeAcrValues(null));
        assertThrows(IllegalArgumentException.class, () -> StepUp.normalizeAcrValues(" "));
        assertThrows(IllegalArgumentException.class, () -> StepUp.normalizeAcrValues("mfa gold"));
        assertThrows(IllegalArgumentException.class, () -> StepUp.normalizeAcrValues("urn:okta:loa:2fa:any"));
    }

    @Test
    @DisplayName("normalizeMaxAge accepts zero and rejects negatives")
    void normalizeMaxAge() {
        assertEquals(0, StepUp.normalizeMaxAge(0));
        assertEquals(300, StepUp.normalizeMaxAge(300));
        assertThrows(IllegalArgumentException.class, () -> StepUp.normalizeMaxAge(-1));
    }

    @Test
    @DisplayName("normalizeMaxAgeParameter omits unset values and rejects malformed ones")
    void normalizeMaxAgeParameter() {
        assertNull(StepUp.normalizeMaxAgeParameter(null));
        assertNull(StepUp.normalizeMaxAgeParameter("  "));
        assertEquals("0", StepUp.normalizeMaxAgeParameter("0"));
        assertEquals("300", StepUp.normalizeMaxAgeParameter(" 0300 "));
        for (String bad : new String[]{"undefined", "null", "NaN", "-1", "300.0", "5m", "99999999999"}) {
            assertThrows(IllegalArgumentException.class, () -> StepUp.normalizeMaxAgeParameter(bad), bad);
        }
    }

    @Test
    @DisplayName("normalizeAcrValuesParameter collapses whitespace and omits unset values")
    void normalizeAcrValuesParameter() {
        assertNull(StepUp.normalizeAcrValuesParameter(null));
        assertNull(StepUp.normalizeAcrValuesParameter(" "));
        assertEquals("mfa custom", StepUp.normalizeAcrValuesParameter(" mfa   custom "));
    }

    @Test
    @DisplayName("promptIncludesNone finds none in a space-separated prompt")
    void promptIncludesNone() {
        assertTrue(StepUp.promptIncludesNone("none"));
        assertTrue(StepUp.promptIncludesNone("login NONE"));
        assertFalse(StepUp.promptIncludesNone("login"));
        assertFalse(StepUp.promptIncludesNone("nonexistent"));
        assertFalse(StepUp.promptIncludesNone(null));
    }

    @Test
    @DisplayName("challenge header carries the error, acr_values and max_age")
    void challengeHeader() {
        assertEquals(
                "Bearer error=\"insufficient_user_authentication\", "
                        + "error_description=\"A different authentication level is required\", "
                        + "acr_values=\"mfa\"",
                StepUp.insufficientUserAuthenticationChallenge(AcrValue.MFA, null));
        String withMaxAge = StepUp.insufficientUserAuthenticationChallenge(AcrValue.PHR, 300);
        assertTrue(withMaxAge.endsWith("acr_values=\"phr\", max_age=\"300\""));
        String maxAgeOnly = StepUp.insufficientUserAuthenticationChallenge(null, 60);
        assertFalse(maxAgeOnly.contains("acr_values"));
        assertTrue(maxAgeOnly.endsWith("max_age=\"60\""));
    }

    @Test
    @DisplayName("StepUpRequest builds normalized values and rejects invalid ones")
    void stepUpRequest() {
        StepUpRequest request = StepUpRequest.builder()
                .acr(AcrValue.MFA, AcrValue.PHR, AcrValue.MFA)
                .maxAge(0)
                .parameter("org_code", "org_1")
                .build();
        assertEquals(Optional.of("mfa phr"), request.getAcrValues());
        assertEquals(Optional.of(0), request.getMaxAge());
        assertEquals("org_1", request.getParameters().get("org_code"));

        assertEquals(Optional.of("phr"), StepUpRequest.builder().acrValues("PHR").build().getAcrValues());
        assertEquals(Optional.empty(), StepUpRequest.ofMaxAge(60).getAcrValues());
        assertEquals(Optional.of(300), StepUpRequest.of(AcrValue.MFA, 300).getMaxAge());
        assertThrows(IllegalArgumentException.class, () -> StepUpRequest.builder().maxAge(-5));
        assertThrows(IllegalArgumentException.class, () -> StepUpRequest.builder().acrValues("gold"));
    }
}
