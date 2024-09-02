package com.kinde.filter;

import com.kinde.principal.KindePrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;

public class KindeHttpRequestWrapper extends HttpServletRequestWrapper{

    private Principal principal;

    public KindeHttpRequestWrapper(HttpServletRequest request, Principal principal) {
        super(request);
        this.principal = principal;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }


    @Override
    public boolean isUserInRole(String role) {
        if (principal instanceof KindePrincipal) {
            return ((KindePrincipal) principal).hasRole(role);
        }
        return false;
    }
}
