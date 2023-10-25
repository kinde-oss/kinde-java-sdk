package org.openapitools.sdk.enums;

import java.util.HashMap;
import java.util.Map;

public class AdditionalParameters {
    public static final Map<String, String> ADDITIONAL_PARAMETER = new HashMap<>();
    static {
        ADDITIONAL_PARAMETER.put("audience", "string");
        ADDITIONAL_PARAMETER.put("org_code", "string");
        ADDITIONAL_PARAMETER.put("org_name", "string");
        ADDITIONAL_PARAMETER.put("is_create_org", "string");
    }
}