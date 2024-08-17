package com.kinde.config;

import com.kinde.authorization.AuthorizationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum KindeParameters {
    DOMAIN("KINDE_DOMAIN", val->(String)val),
    REDIRECT_URI("KINDE_REDIRECT_URI",val->(String)val),
    LOGOUT_REDIRECT_URI("KINDE_LOGOUT_REDIRECT_URI",val->(String)val),
    OPENID_ENDPOINT("KINDE_OPENID_ENDPOINT",val->(String)val),
    AUTHORIZATION_ENDPOINT("KINDE_AUTHORIZATION_ENDPOINT",val->(String)val),
    TOKEN_ENDPOINT("KINDE_TOKEN_ENDPOINT",val->(String)val),
    LOGOUT_ENDPOINT("KINDE_LOGOUT_ENDPOINT",val->(String)val),
    CLIENT_ID("KINDE_CLIENT_ID",val->(String)val),
    CLIENT_SECRET("KINDE_CLIENT_SECRET",val->(String)val),
    GRANT_TYPE("KINDE_GRANT_TYPE",val->AuthorizationType.fromValue(val)),
    SCOPES("KINDE_SCOPES",val->val instanceof String? new ArrayList(Arrays.asList(((String)val).split(","))) : (List)val),
    PROTOCOL("KINDE_PROTOCOL",val->(String)val),
    AUDIENCE("KINDE_AUDIENCE",val->val instanceof String? new ArrayList(Arrays.asList(((String)val).split(","))) : (List)val);


    // Field to store the string value
    private final String value;
    private Function mapper;

    // Private constructor to initialize the enum constants
    private KindeParameters(String value, Function mapper) {
        this.value = value;
        this.mapper = mapper;
    }

    // Getter method to retrieve the string value
    public String getValue() {
        return value;
    }

    public Function getMapper() { return mapper; }

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
