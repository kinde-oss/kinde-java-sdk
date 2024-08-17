package com.kinde.config;

import com.kinde.KindeClientBuilder;
import com.kinde.authorization.AuthorizationType;

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

    AuthorizationType grantType();

    List<String> scopes();

    String protocol();

    List<String> audience();

    Map<String, Object> parameters();

    String getStringValue(String key);

    Long getLongValue(String key);

    Object getValue(String key);
}
