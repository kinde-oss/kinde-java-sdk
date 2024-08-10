package com.kinde.token;


import java.util.List;

public interface KindeToken {

    boolean valid();

    String token();

    Object getClaim(String key);

    List<String> getPermissions();
}
