package com.kinde.principal;


import lombok.AllArgsConstructor;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
public class KindePrincipal implements Principal {

    private String name;
    private List<String> roles;



    @Override
    public String getName() {
        return this.name;
    }

    public List<String> roles() {
        return this.roles;
    }

    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }
}
