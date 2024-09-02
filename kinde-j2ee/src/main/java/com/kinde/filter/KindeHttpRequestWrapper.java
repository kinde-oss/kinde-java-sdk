package com.kinde.filter;

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
}
