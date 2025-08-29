package com.kinde.auth;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Client for accessing token claims functionality.
 * This provides simplified access to claims from the user's tokens.
 */
public class Claims extends BaseAuth {
    
    /**
     * Get a specific claim from the user's tokens.
     * 
     * @param claimName The name of the claim to retrieve (e.g. "aud", "given_name")
     * @param tokenType The type of token to get the claim from ("access_token" or "id_token")
     * @return Map containing claim details with "name" and "value" keys
     */
    public Map<String, Object> getClaim(String claimName, String tokenType) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", claimName);
        result.put("value", null);
        
        Optional<com.kinde.token.KindeToken> tokenOpt = getToken();
        if (tokenOpt.isEmpty()) {
            logger.debug("No token available for claim retrieval");
            return result;
        }
        
        com.kinde.token.KindeToken token = tokenOpt.get();
        Object value = token.getClaim(claimName);
        result.put("value", value);
        
        return result;
    }
    
    /**
     * Get a specific claim from the access token.
     * 
     * @param claimName The name of the claim to retrieve
     * @return Map containing claim details with "name" and "value" keys
     */
    public Map<String, Object> getClaim(String claimName) {
        return getClaim(claimName, "access_token");
    }
    
    /**
     * Get all claims from the user's tokens.
     * 
     * @param tokenType The type of token to get claims from ("access_token" or "id_token")
     * @return Map containing all claims from the token
     */
    public Map<String, Object> getAllClaims(String tokenType) {
        Optional<com.kinde.token.KindeToken> tokenOpt = getToken();
        if (tokenOpt.isEmpty()) {
            logger.debug("No token available for claims retrieval");
            return new HashMap<>();
        }
        
        com.kinde.token.KindeToken token = tokenOpt.get();
        Map<String, Object> claims = new HashMap<>();
        
        // Get common claims
        claims.put("sub", token.getUser());
        claims.put("permissions", token.getPermissions());
        claims.put("roles", token.getRoles());
        claims.put("org_codes", token.getOrganisations());
        claims.put("feature_flags", token.getFlags());
        
        return claims;
    }
    
    /**
     * Get all claims from the access token.
     * 
     * @return Map containing all claims from the access token
     */
    public Map<String, Object> getAllClaims() {
        return getAllClaims("access_token");
    }
}
