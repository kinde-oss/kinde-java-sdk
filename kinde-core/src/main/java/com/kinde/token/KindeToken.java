package com.kinde.token;


import java.util.Set;

public interface KindeToken {

    boolean valid();

    String token();

    Object getClaim(String key);

    Set<String> getPermissions();
}
