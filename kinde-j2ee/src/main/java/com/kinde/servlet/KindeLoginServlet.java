package com.kinde.servlet;

import com.kinde.authorization.AuthorizationUrl;
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
import java.util.List;

@Slf4j
public class KindeLoginServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        if (code == null) {
            // Redirect to the OAuth provider's authorization page
            AuthorizationUrl authorizationUrl = KindeSingleton.getInstance().getKindeClient().clientSession().authorizationUrl();
            req.getSession().setAttribute("AuthorizationUrl",authorizationUrl);
            resp.sendRedirect(authorizationUrl.getUrl().toString());
        } else {
            // Exchange the authorization code for an access token
            try {
                AuthorizationUrl authorizationUrl = (AuthorizationUrl)req.getSession().getAttribute("AuthorizationUrl");
                List<KindeToken> tokens = KindeSingleton.getInstance().getKindeClient().initClientSession(code,authorizationUrl).retrieveTokens();

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
