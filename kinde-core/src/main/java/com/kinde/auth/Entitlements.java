package com.kinde.auth;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kinde.KindeClientSession;
import com.kinde.accounts.KindeAccountsClient;
import com.kinde.accounts.dto.EntitlementDto;

/**
 * Client for accessing entitlements functionality.
 * This provides simplified access to user entitlements from the Kinde Accounts API.
 */
public class Entitlements extends BaseAuth {
    
    /**
     * Get all entitlements for the current user's organization.
     * 
     * @return List of entitlement data maps
     */
    public List<Map<String, Object>> getAllEntitlements() {
        try {
            KindeClientSession session = getSession()
                .orElseThrow(() -> {
                    logger.debug("No session available for entitlements retrieval");
                    return new RuntimeException("No session available");
                });
            
            KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
            List<EntitlementDto> entitlements = accountsClient.getEntitlements();
            
            return entitlements.stream()
                .map(this::convertEntitlementDtoToMap)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving entitlements: {}", e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Get a specific entitlement by key.
     * 
     * @param key The entitlement key to retrieve
     * @return Entitlement data map, or null if not found
     */
    public Map<String, Object> getEntitlement(String key) {
        if (key == null || key.trim().isEmpty()) {
            logger.warn("Entitlement key cannot be null or empty");
            return null;
        }
        
        try {
            KindeClientSession session = getSession()
                .orElseThrow(() -> {
                    logger.debug("No session available for entitlement retrieval");
                    return new RuntimeException("No session available");
                });
            
            KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
            EntitlementDto entitlement = accountsClient.getEntitlement(key);
            
            if (entitlement != null) {
                return convertEntitlementDtoToMap(entitlement);
            }
            
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving entitlement '{}': {}", key, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Check if the user has a specific entitlement.
     * 
     * @param entitlementKey The entitlement key to check
     * @return true if the user has the entitlement, false otherwise
     */
    public boolean hasEntitlement(String entitlementKey) {
        Map<String, Object> entitlement = getEntitlement(entitlementKey);
        if (entitlement == null) {
            return false;
        }
        
        // Determine active/enabled state from 'status' or fallback to 'value' and 'type'
        Object status = entitlement.get("status");
        if (status != null) {
            if (status instanceof Boolean) {
                return (Boolean) status;
            }
            String s = String.valueOf(status).trim();
            return s.equalsIgnoreCase("active") ||
                   s.equalsIgnoreCase("enabled") ||
                   s.equalsIgnoreCase("true") ||
                   s.equals("1") ||
                   s.equalsIgnoreCase("yes") ||
                   s.equalsIgnoreCase("on");
        }

        Object value = entitlement.get("value");
        Object type = entitlement.get("type");
        if (value != null) {
            String v = String.valueOf(value).trim();
            if (type != null && "boolean".equalsIgnoreCase(String.valueOf(type))) {
                return v.equalsIgnoreCase("true") || v.equals("1") || v.equalsIgnoreCase("yes") || v.equalsIgnoreCase("on");
            }
            // Heuristic: treat active-like string values as enabled when type is not boolean
            return v.equalsIgnoreCase("active") || v.equalsIgnoreCase("enabled");
        }

        return false;
    }
    
    /**
     * Check if the user has any of the specified entitlements.
     * 
     * @param entitlementKeys The entitlement keys to check
     * @return true if the user has any of the entitlements, false otherwise
     */
    public boolean hasAnyEntitlement(List<String> entitlementKeys) {
        if (entitlementKeys == null || entitlementKeys.isEmpty()) {
            return false;
        }
        
        return entitlementKeys.stream().anyMatch(this::hasEntitlement);
    }
    
    /**
     * Check if the user has all of the specified entitlements.
     * 
     * @param entitlementKeys The entitlement keys to check
     * @return true if the user has all of the entitlements, false otherwise
     */
    public boolean hasAllEntitlements(List<String> entitlementKeys) {
        if (entitlementKeys == null || entitlementKeys.isEmpty()) {
            return false;
        }
        
        return entitlementKeys.stream().allMatch(this::hasEntitlement);
    }
    
    /**
     * Convert EntitlementDto to a Map for consistent API.
     * 
     * @param entitlement The EntitlementDto to convert
     * @return Map representation of the entitlement
     */
    private Map<String, Object> convertEntitlementDtoToMap(EntitlementDto entitlement) {
        Map<String, Object> result = new HashMap<>();
        
        if (entitlement != null) {
            result.put("key", entitlement.getKey());
            result.put("name", entitlement.getName());
            result.put("description", entitlement.getDescription());
            result.put("type", entitlement.getType());
            result.put("value", entitlement.getValue());
            result.put("orgCode", entitlement.getOrgCode());
            result.put("plans", entitlement.getPlans());
            // Derive a boolean status for easy checks when type is boolean
            if (entitlement.getType() != null && "boolean".equalsIgnoreCase(entitlement.getType())) {
                String v = String.valueOf(entitlement.getValue());
                boolean enabled = v != null && (v.equalsIgnoreCase("true") || v.equals("1") || v.equalsIgnoreCase("yes") || v.equalsIgnoreCase("on"));
                result.put("status", enabled);
            }
        }
        
        return result;
    }
}
