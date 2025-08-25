package com.kinde.auth;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kinde.KindeClientSession;
import com.kinde.accounts.KindeAccountsClient;
import com.kinde.accounts.dto.RoleDto;

/**
 * Client for accessing roles functionality.
 * This provides simplified access to user roles from tokens and API.
 */
public class Roles extends BaseAuth {
    
    /**
     * Get a specific role for the current user.
     * 
     * @param roleKey The role key to check (e.g. "admin", "user")
     * @return Map containing role details with "roleKey", "orgCode", and "isGranted" keys
     */
    public Map<String, Object> getRole(String roleKey) {
        Map<String, Object> result = new HashMap<>();
        result.put("roleKey", roleKey);
        result.put("orgCode", null);
        result.put("isGranted", false);
        
        // First try to get from token
        Optional<com.kinde.token.KindeToken> tokenOpt = getToken();
        if (tokenOpt.isPresent()) {
            com.kinde.token.KindeToken token = tokenOpt.get();
            List<String> roles = token.getRoles();
            List<String> orgCodes = token.getOrganisations();
            
            String orgCode = orgCodes != null && !orgCodes.isEmpty() ? orgCodes.get(0) : null;
            boolean isGranted = roles != null && roles.contains(roleKey);
            
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
                    logger.debug("No session available for role API fallback");
                    return new RuntimeException("No session available");
                });
            
            KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
            List<RoleDto> apiRoles = accountsClient.getRoles();
            
            if (apiRoles != null) {
                boolean hasRole = apiRoles.stream()
                    .anyMatch(role -> roleKey.equals(role.getKey()) || roleKey.equalsIgnoreCase(role.getName()));
                
                if (hasRole) {
                    result.put("isGranted", true);
                    RoleDto foundRole = apiRoles.stream()
                        .filter(role -> roleKey.equals(role.getKey()) || roleKey.equalsIgnoreCase(role.getName()))
                        .findFirst()
                        .orElse(null);
                    
                    if (foundRole != null) {
                        result.put("role", convertRoleDtoToMap(foundRole));
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error retrieving role from API: {}", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get all roles for the current user.
     * 
     * @return Map containing organization code and list of roles
     */
    public Map<String, Object> getRoles() {
        Map<String, Object> result = new HashMap<>();
        result.put("orgCode", null);
        result.put("roles", new java.util.ArrayList<String>());
        
        // First try to get from token
        Optional<com.kinde.token.KindeToken> tokenOpt = getToken();
        if (tokenOpt.isPresent()) {
            com.kinde.token.KindeToken token = tokenOpt.get();
            List<String> roles = token.getRoles();
            List<String> orgCodes = token.getOrganisations();
            
            String orgCode = orgCodes != null && !orgCodes.isEmpty() ? orgCodes.get(0) : null;
            
            result.put("orgCode", orgCode);
            result.put("roles", roles != null ? roles : new java.util.ArrayList<String>());
        }
        
        // Fall back to API for additional roles
        try {
            KindeClientSession session = getSession()
                .orElseThrow(() -> {
                    logger.debug("No session available for roles API fallback");
                    return new RuntimeException("No session available");
                });
            
            KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
            List<RoleDto> apiRoles = accountsClient.getRoles();
            
            if (apiRoles != null && !apiRoles.isEmpty()) {
                List<String> apiRoleKeys = apiRoles.stream()
                    .map(RoleDto::getKey)
                    .filter(key -> key != null)
                    .collect(Collectors.toList());

                @SuppressWarnings("unchecked")
                List<String> existingRoles = (List<String>) result.get("roles");
                java.util.LinkedHashSet<String> merged = new java.util.LinkedHashSet<>(existingRoles);
                merged.addAll(apiRoleKeys);
                result.put("roles", new java.util.ArrayList<>(merged));
            }
        } catch (Exception e) {
            logger.debug("Error retrieving roles from API: {}", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Check if the user has a specific role.
     * 
     * @param roleKey The role key to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String roleKey) {
        // First try to get from token
        Optional<com.kinde.token.KindeToken> tokenOpt = getToken();
        if (tokenOpt.isPresent()) {
            com.kinde.token.KindeToken token = tokenOpt.get();
            List<String> roles = token.getRoles();
            
            if (roles != null && roles.contains(roleKey)) {
                return true; // Found in token, return early
            }
        }
        
        // Fall back to API if not found in token
        try {
            KindeClientSession session = getSession()
                .orElseThrow(() -> {
                    logger.debug("No session available for role check API fallback");
                    return new RuntimeException("No session available");
                });
            
            KindeAccountsClient accountsClient = new KindeAccountsClient(session, true);
            List<RoleDto> apiRoles = accountsClient.getRoles();
            
            if (apiRoles != null) {
                return apiRoles.stream()
                    .anyMatch(role -> roleKey.equals(role.getKey()) || roleKey.equalsIgnoreCase(role.getName()));
            }
        } catch (Exception e) {
            logger.debug("Error checking role from API: {}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if the user has any of the specified roles.
     * 
     * @param roleKeys The role keys to check
     * @return true if the user has any of the roles, false otherwise
     */
    public boolean hasAnyRole(List<String> roleKeys) {
        if (roleKeys == null || roleKeys.isEmpty()) {
            return false;
        }
        
        return roleKeys.stream().anyMatch(this::hasRole);
    }
    
    /**
     * Check if the user has all of the specified roles.
     * 
     * @param roleKeys The role keys to check
     * @return true if the user has all of the roles, false otherwise
     */
    public boolean hasAllRoles(List<String> roleKeys) {
        if (roleKeys == null || roleKeys.isEmpty()) {
            return false;
        }
        
        return roleKeys.stream().allMatch(this::hasRole);
    }
    
    /**
     * Convert RoleDto to a Map for consistent API.
     * 
     * @param role The RoleDto to convert
     * @return Map representation of the role
     */
    private Map<String, Object> convertRoleDtoToMap(RoleDto role) {
        Map<String, Object> result = new HashMap<>();
        
        if (role != null) {
            result.put("id", role.getId());
            result.put("name", role.getName());
            result.put("description", role.getDescription());
        }
        
        return result;
    }
}
