package com.kinde.config;

import com.kinde.authorization.AuthorizationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum KindeParameters {
    DOMAIN("KINDE_DOMAIN", (Function<Object, String>)val->(String)val),
    REDIRECT_URI("KINDE_REDIRECT_URI",(Function<Object, String>)val->(String)val),
    LOGOUT_REDIRECT_URI("KINDE_LOGOUT_REDIRECT_URI",(Function<Object, String>)val->(String)val),
    OPENID_ENDPOINT("KINDE_OPENID_ENDPOINT",(Function<Object, String>)val->(String)val),
    AUTHORIZATION_ENDPOINT("KINDE_AUTHORIZATION_ENDPOINT",(Function<Object, String>)val->(String)val),
    TOKEN_ENDPOINT("KINDE_TOKEN_ENDPOINT",(Function<Object, String>)val->(String)val),
    LOGOUT_ENDPOINT("KINDE_LOGOUT_ENDPOINT",(Function<Object, String>)val->(String)val),
    CLIENT_ID("KINDE_CLIENT_ID",(Function<Object, String>)val->(String)val),
    CLIENT_SECRET("KINDE_CLIENT_SECRET",(Function<Object, String>)val->(String)val),
    GRANT_TYPE("KINDE_GRANT_TYPE",(Function<Object, AuthorizationType>)val->AuthorizationType.fromValue(val)),
    SCOPES("KINDE_SCOPES",(Function<Object, List<String>>)val->val instanceof String? new ArrayList(Arrays.asList(((String)val).split(","))) : (List)val),
    PROTOCOL("KINDE_PROTOCOL",(Function<Object, String>)val->(String)val),
    AUDIENCE("KINDE_AUDIENCE",(Function<Object, List<String>>)val->val instanceof String? new ArrayList(Arrays.asList(((String)val).split(","))) : (List)val),
    LANG("KINDE_LANG",(Function<Object, String>)val->(String)val),
    ORG_CODE("KINDE_ORG_CODE",(Function<Object, String>)val->(String)val),
    HAS_SUCCESS_PAGE("KINDE_HAS_SUCCESS_PAGE",(Function<Object, Boolean>)val->val instanceof String? Boolean.valueOf((String)val) : Boolean.valueOf((Boolean) val));


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
