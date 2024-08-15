package com.kinde;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ProtectedResourceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accessToken = (String) req.getSession().getAttribute("access_token");
        if (accessToken == null) {
            resp.sendRedirect("login");
        } else {
            // Process the request with the access token
            resp.getWriter().println("Access token: " + accessToken);
        }
    }
}