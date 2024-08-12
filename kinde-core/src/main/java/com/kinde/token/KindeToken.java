package com.kinde.token;


import java.util.List;

public interface KindeToken {

    boolean valid();

    String token();

    String getUser();

    List<String> getOrganisations();

    Object getClaim(String key);

    List<String> getPermissions();

    String getStringFlag(String key);

    Integer getIntegerFlag(String key);

    Boolean getBooleanFlag(String key);
}
