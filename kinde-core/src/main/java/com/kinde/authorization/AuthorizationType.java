package com.kinde.authorization;

import com.kinde.config.KindeParameters;

import java.util.Arrays;
import java.util.function.Function;

public enum AuthorizationType {
    CODE("CODE"),
    IMPLICIT("IMPLICIT"),
    CUSTOM("CUSTOM");

    // Field to store the string value
    private final String value;

    // Private constructor to initialize the enum constants
    private AuthorizationType(String value) {
        this.value = value;
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
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

}
