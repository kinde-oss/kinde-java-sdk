package com.kinde;

import com.kinde.token.AccessToken;
import com.kinde.token.IDToken;
import com.kinde.token.KindeToken;
import com.kinde.token.RefreshToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.util.List;

public class LoginServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        if (code == null) {
            // Redirect to the OAuth provider's authorization page
            resp.sendRedirect(KindeSingleton.getInstance().getKindeClient().clientSession().authorizationUrl().toString());
        } else {
            // Exchange the authorization code for an access token
            try {
                List<KindeToken> tokens = KindeSingleton.getInstance().getKindeClient().initClientSession(code).retrieveTokens();

                tokens.stream().filter(token->token instanceof AccessToken).forEach(token->req.getSession().setAttribute("access_token",token.token()));
                tokens.stream().filter(token->token instanceof IDToken).forEach(token->req.getSession().setAttribute("id_token",token.token()));
                tokens.stream().filter(token->token instanceof RefreshToken).forEach(token->req.getSession().setAttribute("refresh_token",token.token()));
                resp.sendRedirect("/kinde-j2ee-app/protected-resource");
            } catch (Exception e) {
                throw new ServletException("OAuth token exchange failed", e);
            }
        }
    }
}
