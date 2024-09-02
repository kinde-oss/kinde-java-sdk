package com.kinde.servlet;

import com.kinde.KindeClientSession;
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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static com.kinde.constants.KindeConstants.*;
import static com.kinde.constants.KindeJ2eeConstants.*;

public class KindeLogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            AuthorizationUrl authorizationUrl = KindeSingleton
                    .getInstance()
                    .getKindeClientBuilder()
                    .build()
                    .clientSession().logout();
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.removeAttribute(ACCESS_TOKEN);
                session.removeAttribute(ID_TOKEN);
                session.removeAttribute(REFRESH_TOKEN);
                session.removeAttribute(AUTHENTICATED_USER);
                session.removeAttribute(AUTHORIZATION_URL);
                session.invalidate();
            }
            resp.sendRedirect(authorizationUrl.getUrl().toString());
        } catch (Exception ex) {
            throw new ServletException("Failed to logout : " + ex.getMessage(),ex);
        }
    }
}
