package com.kinde.filter;

import com.kinde.constants.KindeAuthenticationAction;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

public class KindeLoginFilter extends KindeAuthenticationFilter{
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter(servletRequest,servletResponse,filterChain, KindeAuthenticationAction.LOGIN);
    }
}
