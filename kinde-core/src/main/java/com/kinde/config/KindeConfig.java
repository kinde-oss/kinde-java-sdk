package com.kinde.config;

import com.kinde.KindeClientBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface KindeConfig {

    String domain();

    String redirectUri();

    String logoutRedirectUri();

    String openidEndpoint();

    String authorizationEndpoint();

    String tokenEndpoint();

    String logoutEndpoint();

    String clientId();

    String clientSecret();

    String grantType();

    List<String> scopes();

    String protocol();

    Map<String, Object> parameters();

    String getStringValue(String key);

    Long getLongValue(String key);

    Object getValue(String key);
}
