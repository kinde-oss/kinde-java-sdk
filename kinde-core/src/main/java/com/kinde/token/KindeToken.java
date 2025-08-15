package com.kinde.token;


import java.util.List;
import java.util.Map;

public interface KindeToken {

    boolean valid();

    String token();

    String getUser();

    List<String> getOrganisations();

    Object getClaim(String key);

    List<String> getPermissions();

    /**
     * Gets the roles from the token.
     * This method checks both "roles" and "x-hasura-roles" claims and returns the first non-null result.
     * 
     * @return List of role strings, or null if no roles are found
     */
    List<String> getRoles();

    String getStringFlag(String key);

    Integer getIntegerFlag(String key);

    Boolean getBooleanFlag(String key);
    
    Map<String, Object> getFlags();
}
