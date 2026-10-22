package com.kinde.auth;

import com.kinde.KindeClientSession;
import com.kinde.token.KindeToken;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeatureFlagsTest {

    @Test
    void readsAndConvertsTokenFeatureFlagValues() {
        TestFeatureFlags featureFlags = featureFlags(Map.of(
                "string", Map.of("t", "s", "v", "hello"),
                "boolean", Map.of("t", "b", "v", "true"),
                "integer", Map.of("t", "i", "v", "42"),
                "simple", "raw"));

        FeatureFlags.FeatureFlag stringFlag = featureFlags.getFeatureFlag("string");
        assertEquals("string", stringFlag.getType());
        assertEquals("hello", stringFlag.getValue());
        assertEquals("hello", featureFlags.getFeatureFlagString("string"));

        FeatureFlags.FeatureFlag booleanFlag = featureFlags.getFeatureFlag("boolean");
        assertEquals("boolean", booleanFlag.getType());
        assertEquals(Boolean.TRUE, booleanFlag.getValue());
        assertTrue(featureFlags.isFeatureFlagEnabled("boolean"));

        assertEquals(42, featureFlags.getFeatureFlagInteger("integer"));
        assertEquals("raw", featureFlags.getFeatureFlagValue("simple"));
        assertEquals("string", featureFlags.getFeatureFlag("simple").getType());
        assertNull(featureFlags.getFeatureFlagInteger("string"));
        assertNull(featureFlags.getFeatureFlagString("integer"));
    }

    @Test
    void handlesInvalidIntegerAndMissingFlags() {
        TestFeatureFlags featureFlags = featureFlags(Map.of(
                "invalid", Map.of("t", "i", "v", "not-an-integer"),
                "disabled", Map.of("t", "b", "v", "false")));

        assertEquals("error", featureFlags.getFeatureFlag("invalid").getType());
        assertFalse(featureFlags.isFeatureFlagEnabled("disabled"));
        assertFalse(featureFlags.isFeatureFlagEnabled("missing"));
        assertNull(featureFlags.getFeatureFlagValue("missing"));
        assertNull(featureFlags.getFeatureFlagString("missing"));
        assertNull(featureFlags.getFeatureFlagInteger("missing"));
    }

    @Test
    void enumeratesTokenFlagsAndReturnsEmptyWhenNoTokenIsAvailable() {
        TestFeatureFlags featureFlags = featureFlags(Map.of("flag", "enabled"));
        assertEquals("enabled", featureFlags.getFeatureFlags().get("flag").getValue());

        TestFeatureFlags noToken = new TestFeatureFlags(Optional.empty());
        assertTrue(noToken.getFeatureFlags().isEmpty());
        assertNull(noToken.getFeatureFlag("missing"));
    }

    private static TestFeatureFlags featureFlags(Map<String, Object> flags) {
        KindeToken token = mock(KindeToken.class);
        when(token.getFlags()).thenReturn(flags);
        return new TestFeatureFlags(Optional.of(token));
    }

    private static final class TestFeatureFlags extends FeatureFlags {
        private final Optional<KindeToken> token;

        private TestFeatureFlags(Optional<KindeToken> token) {
            this.token = token;
        }

        @Override
        protected Optional<KindeToken> getToken() {
            return token;
        }

        @Override
        protected Optional<KindeClientSession> getSession() {
            return Optional.empty();
        }
    }
}
