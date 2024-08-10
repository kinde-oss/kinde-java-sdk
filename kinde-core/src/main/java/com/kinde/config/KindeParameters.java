package com.kinde.config;

public enum KindeParameters {
    DOMAIN("DOMAIN"),
    REDIRECT_URI("REDIRECT_URI"),
    LOGOUT_REDIRECT_URI("LOGOUT_REDIRECT_URI"),
    OPENID_ENDPOINT("OPENID_ENDPOINT"),
    AUTHORIZATION_ENDPOINT("AUTHORIZATION_ENDPOINT"),
    TOKEN_ENDPOINT("TOKEN_ENDPOINT"),
    LOGOUT_ENDPOINT("LOGOUT_ENDPOINT"),
    CLIENT_ID("CLIENT_ID"),
    CLIENT_SECRET("CLIENT_SECRET"),
    GRANT_TYPE("GRANT_TYPE"),
    SCOPES("SCOPES"),
    PROTOCOL("PROTOCOL");


    // Field to store the string value
    private final String value;

    // Private constructor to initialize the enum constants
    private KindeParameters(String value) {
        this.value = value;
    }

    // Getter method to retrieve the string value
    public String getValue() {
        return value;
    }

    // Method to get the enum constant by string value
    public static KindeParameters fromValue(String value) {
        for (KindeParameters constant : KindeParameters.values()) {
            if (constant.value.equals(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
