package com.kinde.servlet;

import com.kinde.constants.KindeAuthenticationAction;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class KindeCreateOrgServlet extends KindeAuthenticationServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp, KindeAuthenticationAction.CREATE_ORG);
    }
}
