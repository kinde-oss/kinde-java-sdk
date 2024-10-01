package com.kinde.authorization;

import com.kinde.config.KindeParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum AuthorizationType {
    CODE("CODE",List.of("authorization_code")),
    IMPLICIT("IMPLICIT"),
    CUSTOM("CUSTOM");

    // Field to store the string value
    private final String value;
    private final List<String> values = new ArrayList<>();

    // Private constructor to initialize the enum constants
    AuthorizationType(String value) {
        this.value = value;
    }

    AuthorizationType(String value, List<String> values) {
        this.value = value;
        this.values.addAll(values);
    }

    // Getter method to retrieve the string value
    public String getValue() {
        return value;
    }

    // Method to get the enum constant by string value
    public static AuthorizationType fromValue(Object value) {
        if (value instanceof AuthorizationType) {
            return (AuthorizationType)value;
        }
        for (AuthorizationType constant : AuthorizationType.values()) {
            if (constant.value.equals(value)) {
                return constant;
            }
            if (constant.values.stream().filter(entry->entry.equals(value)).findAny().isPresent()) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

}
