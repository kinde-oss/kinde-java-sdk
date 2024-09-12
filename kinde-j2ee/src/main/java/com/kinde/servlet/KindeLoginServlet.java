package com.kinde.servlet;

import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.constants.KindeAuthenticationAction;
import com.kinde.principal.KindePrincipal;
import com.kinde.token.AccessToken;
import com.kinde.token.IDToken;
import com.kinde.token.KindeToken;
import com.kinde.token.RefreshToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static com.kinde.constants.KindeConstants.*;
import static com.kinde.constants.KindeJ2eeConstants.*;

@Slf4j
public class KindeLoginServlet extends KindeAuthenticationServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp, KindeAuthenticationAction.LOGIN);
    }
}
