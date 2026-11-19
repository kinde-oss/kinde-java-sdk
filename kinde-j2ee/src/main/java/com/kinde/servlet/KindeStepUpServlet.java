package com.kinde.servlet;

import com.kinde.constants.KindeAuthenticationAction;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Starts a step-up authentication (RFC 9470) and handles its callback.
 *
 * <p>Send the user here before a sensitive action, for example
 * {@code /step-up?acr_values=mfa&max_age=300&post_login_url=/transfer}. Kinde asks only for
 * what is missing, and the tokens from the callback replace the ones in the session.
 *
 * <p>If the user cannot meet the requirement, Kinde returns
 * {@code error=unmet_authentication_requirements} and this servlet throws a
 * {@link ServletException} whose root cause is an
 * {@link com.kinde.exceptions.UnmetAuthenticationRequirementsException}. Map it with an
 * {@code <error-page>}; the existing session is kept.
 */
public class KindeStepUpServlet extends KindeAuthenticationServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp, KindeAuthenticationAction.STEP_UP);
    }
}
