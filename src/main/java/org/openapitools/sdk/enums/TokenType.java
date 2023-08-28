package org.openapitools.sdk.enums;

public enum TokenType {
    ACCESS_TOKEN("access_token"),
    ID_TOKEN("id_token");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}