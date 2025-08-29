package com.kinde.auth;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kinde.KindeClientSession;
import com.kinde.accounts.KindeAccountsClient;
import com.kinde.accounts.dto.PermissionDto;

/**
 * Client for accessing permissions functionality.
 * This provides simplified access to user permissions from tokens and API.
 */
public class Permissions extends BaseAuth {
    
    /**
     * Get a specific permission for the current user.
     * 
     * @param permissionKey The permission key to check (e.g. "create:todos")
     * @return Map containing permission details with "permissionKey", "orgCode", and "isGranted" keys
     */
    public Map<String, Object> getPermission(String permissionKey) {
        Map<String, Object> result = new HashMap<>();
        result.put("permissionKey", permissionKey);
        result.put("orgCode", null);
        result.put("isGranted", false);
        
        // First try to get from token
        Optional<com.kinde.token.KindeToken> tokenOpt = getToken();
        if (tokenOpt.isPresent()) {
            com.kinde.token.KindeToken token = tokenOpt.get();
            List<String> permissions = token.getPermissions();
            List<String> orgCodes = token.getOrganisations();
            
            String orgCode = orgCodes != null && !orgCodes.isEmpty() ? orgCodes.get(0) : null;
            boolean isGranted = permissions != null && permissions.contains(permissionKey);
            
            result.put("orgCode", orgCode);
            result.put("isGranted", isGranted);
            
            if (isGranted) {
                return result; // Found in token, return early
            }
        }
        
        // Fall back to API if not found in token
        try {
            KindeClientSession session = getSession()
                .orElseThrow(() -> {
                    logger.debug("No session available for permission API fallback");
                    return new RuntimeException("No session available");
                });
            
            KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
            PermissionDto permission = accountsClient.getPermission(permissionKey);
            
            if (permission != null) {
                result.put("isGranted", true);
                result.put("permission", convertPermissionDtoToMap(permission));
            }
        } catch (Exception e) {
            logger.debug("Error retrieving permission from API: {}", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get all permissions for the current user.
     * 
     * @return Map containing organization code and list of permissions
     */
    public Map<String, Object> getPermissions() {
        Map<String, Object> result = new HashMap<>();
        result.put("orgCode", null);
        result.put("permissions", new java.util.ArrayList<String>());
        
        // First try to get from token
        Optional<com.kinde.token.KindeToken> tokenOpt = getToken();
        if (tokenOpt.isPresent()) {
            com.kinde.token.KindeToken token = tokenOpt.get();
            List<String> permissions = token.getPermissions();
            List<String> orgCodes = token.getOrganisations();
            
            String orgCode = orgCodes != null && !orgCodes.isEmpty() ? orgCodes.get(0) : null;
            
            result.put("orgCode", orgCode);
            result.put("permissions", permissions != null ? permissions : new java.util.ArrayList<String>());
        }
        
        // Fall back to API for additional permissions
        try {
            KindeClientSession session = getSession()
                .orElseThrow(() -> {
                    logger.debug("No session available for permissions API fallback");
                    return new RuntimeException("No session available");
                });
            
            KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
            List<PermissionDto> apiPermissions = accountsClient.getPermissions();
            
            if (apiPermissions != null && !apiPermissions.isEmpty()) {
                List<String> apiPermissionKeys = apiPermissions.stream()
                    .map(PermissionDto::getKey)
                    .filter(key -> key != null)
                    .collect(Collectors.toList());

                @SuppressWarnings("unchecked")
                List<String> existingPermissions = (List<String>) result.get("permissions");
                // De-duplicate while preserving existing order
                java.util.LinkedHashSet<String> merged = new java.util.LinkedHashSet<>(existingPermissions);
                merged.addAll(apiPermissionKeys);
                result.put("permissions", new java.util.ArrayList<>(merged));
            }
        } catch (Exception e) {
            logger.debug("Error retrieving permissions from API: {}", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Check if the user has a specific permission.
     * 
     * @param permissionKey The permission key to check
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(String permissionKey) {
        // First try to get from token
        Optional<com.kinde.token.KindeToken> tokenOpt = getToken();
        if (tokenOpt.isPresent()) {
            com.kinde.token.KindeToken token = tokenOpt.get();
            List<String> permissions = token.getPermissions();
            
            if (permissions != null && permissions.contains(permissionKey)) {
                return true; // Found in token, return early
            }
        }
        
        // Fall back to API if not found in token
        try {
            KindeClientSession session = getSession()
                .orElseThrow(() -> {
                    logger.debug("No session available for permission check API fallback");
                    return new RuntimeException("No session available");
                });
            
            KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
            PermissionDto permission = accountsClient.getPermission(permissionKey);
            return permission != null;
        } catch (Exception e) {
            logger.debug("Error checking permission from API: {}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if the user has any of the specified permissions.
     * 
     * @param permissionKeys The permission keys to check
     * @return true if the user has any of the permissions, false otherwise
     */
    public boolean hasAnyPermission(List<String> permissionKeys) {
        if (permissionKeys == null || permissionKeys.isEmpty()) {
            return false;
        }
        
        return permissionKeys.stream().anyMatch(this::hasPermission);
    }
    
    /**
     * Check if the user has all of the specified permissions.
     * 
     * @param permissionKeys The permission keys to check
     * @return true if the user has all of the permissions, false otherwise
     */
    public boolean hasAllPermissions(List<String> permissionKeys) {
        if (permissionKeys == null || permissionKeys.isEmpty()) {
            return false;
        }
        
        return permissionKeys.stream().allMatch(this::hasPermission);
    }
    
    /**
     * Convert PermissionDto to a Map for consistent API.
     * 
     * @param permission The PermissionDto to convert
     * @return Map representation of the permission
     */
    private Map<String, Object> convertPermissionDtoToMap(PermissionDto permission) {
        Map<String, Object> result = new HashMap<>();
        
        if (permission != null) {
            result.put("id", permission.getId());
            result.put("name", permission.getName());
            result.put("description", permission.getDescription());
        }
        
        return result;
    }
}
