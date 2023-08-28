package org.openapitools.sdk.enums;

public enum StorageEnums {
    TOKEN("token"),
    STATE("state"),
    CODE_VERIFIER("code_verifier"),
    AUTH_STATUS("auth_status"),
    USER_PROFILE("user_profile");

    private final String value;

    StorageEnums(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}