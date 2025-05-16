package com.kinde.authorization;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public enum AuthorizationType {
    CODE("CODE",List.of("authorization_code")),
    IMPLICIT("IMPLICIT"),
    CUSTOM("CUSTOM");

    @Getter
    private final String value;
    private final List<String> values = new ArrayList<>();

    AuthorizationType(String value) {
        this.value = value;
    }

    AuthorizationType(String value, List<String> values) {
        this.value = value;
        this.values.addAll(values);
    }

    public static AuthorizationType fromValue(Object value) {
        if (value instanceof AuthorizationType) {
            return (AuthorizationType)value;
        }
        for (AuthorizationType constant : AuthorizationType.values()) {
            if (constant.value.equals(value)) {
                return constant;
            }
            if (constant.values.stream().anyMatch(entry->entry.equals(value))) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

}
