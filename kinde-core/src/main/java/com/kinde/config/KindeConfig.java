package com.kinde.config;

import java.util.Map;

public interface KindeConfig {

    String domain();

    Map<String, Object> parameters();

    String getStringValue(String key);

    Long getLongValue(String key);

    Object getValue(String key);
}
