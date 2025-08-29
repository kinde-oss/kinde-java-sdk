package com.kinde.auth;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import com.kinde.KindeClientSession;
import com.kinde.accounts.KindeAccountsClient;
import com.kinde.accounts.dto.FeatureFlagDto;

/**
 * Client for accessing feature flags functionality.
 * This provides simplified access to feature flags from tokens and API.
 */
public class FeatureFlags extends BaseAuth {
    
    /**
     * Feature flag data structure.
     */
    public static class FeatureFlag {
        private final String code;
        private final String type;
        private final Object value;
        private final boolean isDefault;
        
        public FeatureFlag(String code, String type, Object value, boolean isDefault) {
            this.code = code;
            this.type = type;
            this.value = value;
            this.isDefault = isDefault;
        }
        
        public String getCode() { return code; }
        public String getType() { return type; }
        public Object getValue() { return value; }
        public boolean isDefault() { return isDefault; }
    }
    
    /**
     * Parse a feature flag value from the token format.
     * 
     * @param flagData The raw flag data from the token
     * @param expectedType Optional type to cast the value to
     * @return FeatureFlag object with parsed value
     */
    private FeatureFlag parseFlagValue(Map<String, Object> flagData, String expectedType) {
        if (flagData == null) {
            throw new IllegalArgumentException("flagData cannot be null");
        }
        
        // Extract raw type and value
        String flagType = (String) flagData.get("t");
        Object rawValue = flagData.get("v");
        
        // Map token type codes to Java types
        String type = "string"; // default
        Object value = rawValue;
        
        try {
            if ("s".equals(flagType)) {
                // String type
                type = "string";
                value = rawValue != null ? String.valueOf(rawValue) : "";
            } else if ("b".equals(flagType)) {
                // Boolean type
                type = "boolean";
                value = Boolean.valueOf(rawValue != null ? String.valueOf(rawValue) : "false");
            } else if ("i".equals(flagType)) {
                // Integer type
                type = "integer";
                value = rawValue != null ? Integer.valueOf(String.valueOf(rawValue)) : 0;
            } else {
                // Unknown type code: return raw as-is
                type = flagType != null ? flagType : "unknown";
                value = rawValue;
            }
        } catch (Exception e) {
            logger.warn("Error parsing flag value: {}", e.getMessage());
            type = "error";
            value = rawValue;
        }
        
        return new FeatureFlag("", type, value, false);
    }
    
    /**
     * Get a specific feature flag by key.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return FeatureFlag object, or null if not found
     */
    public FeatureFlag getFeatureFlag(String flagKey) {
        // First try to get from token
        Optional<com.kinde.token.KindeToken> tokenOpt = getToken();
        if (tokenOpt.isPresent()) {
            com.kinde.token.KindeToken token = tokenOpt.get();
            Map<String, Object> flags = token.getFlags();
            
            if (flags != null && flags.containsKey(flagKey)) {
                Object flagData = flags.get(flagKey);
                if (flagData instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> flagMap = (Map<String, Object>) flagData;
                    FeatureFlag parsed = parseFlagValue(flagMap, null);
                    return new FeatureFlag(flagKey, parsed.getType(), parsed.getValue(), parsed.isDefault());
                } else {
                    // Simple value
                    return new FeatureFlag(flagKey, "string", flagData, false);
                }
            }
        }
        
        // Fall back to API if not found in token
        try {
            KindeClientSession session = getSession()
                .orElseThrow(() -> {
                    logger.debug("No session available for feature flag API fallback");
                    return new RuntimeException("No session available");
                });
            
            KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
            FeatureFlagDto apiFlag = accountsClient.getFeatureFlag(flagKey);
            
            if (apiFlag != null) {
                return convertFeatureFlagDtoToFeatureFlag(apiFlag);
            }
        } catch (Exception e) {
            logger.debug("Error retrieving feature flag from API: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all feature flags for the current user.
     * 
     * @return Map of feature flag keys to FeatureFlag objects
     */
    public Map<String, FeatureFlag> getFeatureFlags() {
        Map<String, FeatureFlag> result = new HashMap<>();
        
        // First try to get from token
        Optional<com.kinde.token.KindeToken> tokenOpt = getToken();
        if (tokenOpt.isPresent()) {
            com.kinde.token.KindeToken token = tokenOpt.get();
            Map<String, Object> flags = token.getFlags();
            
            if (flags != null) {
                for (Map.Entry<String, Object> entry : flags.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    if (value instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> flagMap = (Map<String, Object>) value;
                        FeatureFlag flag = parseFlagValue(flagMap, null);
                        result.put(key, new FeatureFlag(key, flag.getType(), flag.getValue(), flag.isDefault()));
                    } else {
                        result.put(key, new FeatureFlag(key, "string", value, false));
                    }
                }
            }
        }
        
        // Fall back to API for additional feature flags
        try {
            KindeClientSession session = getSession()
                .orElseThrow(() -> {
                    logger.debug("No session available for feature flags API fallback");
                    return new RuntimeException("No session available");
                });
            
            KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
            List<FeatureFlagDto> apiFlags = accountsClient.getFeatureFlags();
            
            if (apiFlags != null) {
                for (FeatureFlagDto apiFlag : apiFlags) {
                                     if (apiFlag.getKey() != null && !result.containsKey(apiFlag.getKey())) {
                     result.put(apiFlag.getKey(), convertFeatureFlagDtoToFeatureFlag(apiFlag));
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error retrieving feature flags from API: {}", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Check if a feature flag is enabled.
     * 
     * @param flagKey The feature flag key to check
     * @return true if the feature flag is enabled, false otherwise
     */
    public boolean isFeatureFlagEnabled(String flagKey) {
        FeatureFlag flag = getFeatureFlag(flagKey);
        if (flag == null) {
            return false;
        }
        
        if ("boolean".equals(flag.getType())) {
            return Boolean.TRUE.equals(flag.getValue());
        } else if ("string".equals(flag.getType())) {
            String value = String.valueOf(flag.getValue());
            return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
        }
        
        return false;
    }
    
    /**
     * Get the value of a feature flag.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return the feature flag value, or null if not found
     */
    public Object getFeatureFlagValue(String flagKey) {
        FeatureFlag flag = getFeatureFlag(flagKey);
        return flag != null ? flag.getValue() : null;
    }
    
    /**
     * Get the string value of a feature flag.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return the feature flag string value, or null if not found
     */
    public String getFeatureFlagString(String flagKey) {
        FeatureFlag flag = getFeatureFlag(flagKey);
        if (flag == null) {
            return null;
        }
        
        if ("string".equals(flag.getType())) {
            return String.valueOf(flag.getValue());
        }
        
        return null;
    }
    
    /**
     * Get the integer value of a feature flag.
     * 
     * @param flagKey The feature flag key to retrieve
     * @return the feature flag integer value, or null if not found or not an integer
     */
    public Integer getFeatureFlagInteger(String flagKey) {
        FeatureFlag flag = getFeatureFlag(flagKey);
        if (flag == null) {
            return null;
        }
        
        if ("integer".equals(flag.getType())) {
            return (Integer) flag.getValue();
        }
        
        return null;
    }
    
    /**
     * Convert FeatureFlagDto to FeatureFlag for consistent API.
     * 
     * @param apiFlag The FeatureFlagDto to convert
     * @return FeatureFlag object
     */
    private FeatureFlag convertFeatureFlagDtoToFeatureFlag(FeatureFlagDto apiFlag) {
        if (apiFlag == null) {
            return null;
        }
        
        String declaredType = apiFlag.getType();
        String raw = apiFlag.getValue();

        String type = "string"; // default
        Object value = raw;

        try {
            if ("boolean".equalsIgnoreCase(declaredType)) {
                type = "boolean";
                boolean v;
                if (raw != null) {
                    v = "true".equalsIgnoreCase(raw) || "1".equals(raw) || "yes".equalsIgnoreCase(raw);
                } else {
                    v = apiFlag.isEnabled();
                }
                value = v;
            } else if ("integer".equalsIgnoreCase(declaredType) || "int".equalsIgnoreCase(declaredType)) {
                type = "integer";
                value = raw != null ? Integer.valueOf(raw.trim()) : 0;
            } else {
                type = "string";
                value = raw;
            }
        } catch (Exception e) {
            logger.warn("Error parsing API feature flag {}: {}", apiFlag.getKey(), e.getMessage());
            type = "error";
            value = raw;
        }

        return new FeatureFlag(apiFlag.getKey(), type, value, false);
    }
}
