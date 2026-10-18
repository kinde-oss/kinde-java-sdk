package com.kinde.auth;

import com.kinde.KindeClientSession;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EntitlementsTest {

    @Test
    void interpretsBooleanAndStatusValues() {
        TestEntitlements entitlements = new TestEntitlements();
        for (String value : List.of("active", "enabled", "true", "1", "yes", "on")) {
            entitlements.put(value, Map.of("status", value));
            assertTrue(entitlements.hasEntitlement(value));
        }
        entitlements.put("boolean-status", Map.of("status", true, "value", false));
        assertTrue(entitlements.hasEntitlement("boolean-status"));
        entitlements.put("disabled", Map.of("status", "inactive"));
        assertFalse(entitlements.hasEntitlement("disabled"));
    }

    @Test
    void fallsBackToValueAndTypeWhenStatusIsAbsent() {
        TestEntitlements entitlements = new TestEntitlements();
        for (String value : List.of("true", "1", "yes", "on")) {
            entitlements.put(value, Map.of("type", "boolean", "value", value));
            assertTrue(entitlements.hasEntitlement(value));
        }
        entitlements.put("boolean-off", Map.of("type", "boolean", "value", "false"));
        entitlements.put("active", Map.of("type", "string", "value", "active"));
        entitlements.put("enabled", Map.of("value", "enabled"));
        entitlements.put("not-active", Map.of("value", "true"));

        assertFalse(entitlements.hasEntitlement("boolean-off"));
        assertTrue(entitlements.hasEntitlement("active"));
        assertTrue(entitlements.hasEntitlement("enabled"));
        assertFalse(entitlements.hasEntitlement("not-active"));
        assertFalse(entitlements.hasEntitlement("missing"));
    }

    @Test
    void handlesEmptyCollectionsAndMissingSession() {
        TestEntitlements entitlements = new TestEntitlements();

        assertFalse(entitlements.hasAnyEntitlement(null));
        assertFalse(entitlements.hasAnyEntitlement(List.of()));
        assertFalse(entitlements.hasAllEntitlements(null));
        assertFalse(entitlements.hasAllEntitlements(List.of()));
        assertFalse(entitlements.hasAnyEntitlement(List.of("missing")));
        assertFalse(entitlements.hasAllEntitlements(List.of("missing")));
        assertNull(entitlements.getEntitlement(null));
        assertNull(entitlements.getEntitlement("  "));
        assertNull(entitlements.getEntitlement("missing"));
        assertTrue(entitlements.getAllEntitlements().isEmpty());
    }

    private static final class TestEntitlements extends Entitlements {
        private final Map<String, Map<String, Object>> values = new HashMap<>();

        private void put(String key, Map<String, Object> value) {
            values.put(key, value);
        }

        @Override
        public Map<String, Object> getEntitlement(String key) {
            return values.get(key);
        }

        @Override
        protected Optional<KindeClientSession> getSession() {
            return Optional.empty();
        }
    }
}
