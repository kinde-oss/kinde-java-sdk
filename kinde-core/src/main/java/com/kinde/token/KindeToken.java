package com.kinde.token;


import java.util.Collections;
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
     * @return List of role strings, never null (empty list if no roles are found)
     */
    default List<String> getRoles() {
        return Collections.emptyList();
    }

    String getStringFlag(String key);

    Integer getIntegerFlag(String key);

        Boolean getBooleanFlag(String key);

    /**
     * Returns all flags available on the token as a simple key-value map.
     * Implementations should expose parsed values. Never null.
     *
     * @return a non-null map of flags (empty if none)
     */
    default Map<String, Object> getFlags() {
        return Collections.emptyMap();
    }
}
