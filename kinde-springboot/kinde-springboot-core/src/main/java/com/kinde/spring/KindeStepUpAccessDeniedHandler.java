package com.kinde.spring;

import com.kinde.stepup.StepUp;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * Answers an {@link InsufficientUserAuthenticationException} with the step-up challenge from
 * RFC 9470: HTTP 401 and
 * {@code WWW-Authenticate: Bearer error="insufficient_user_authentication", acr_values="...", max_age="..."}.
 * The client then sends the user through a step-up with those values and retries with the
 * new access token. Every other access denial goes to the delegate, by default Spring's
 * {@link BearerTokenAccessDeniedHandler}.
 */
public class KindeStepUpAccessDeniedHandler implements AccessDeniedHandler {

    private final AccessDeniedHandler delegate;

    public KindeStepUpAccessDeniedHandler() {
        this(new BearerTokenAccessDeniedHandler());
    }

    public KindeStepUpAccessDeniedHandler(AccessDeniedHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        InsufficientUserAuthenticationException stepUp = find(accessDeniedException);
        if (stepUp == null) {
            delegate.handle(request, response, accessDeniedException);
            return;
        }
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE,
                StepUp.insufficientUserAuthenticationChallenge(stepUp.getRequiredAcr(), stepUp.getRequiredMaxAge()));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private static InsufficientUserAuthenticationException find(Throwable throwable) {
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            if (t instanceof InsufficientUserAuthenticationException) {
                return (InsufficientUserAuthenticationException) t;
            }
            if (t.getCause() == t) {
                break;
            }
        }
        return null;
    }
}
