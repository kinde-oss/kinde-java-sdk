package com.kinde.spring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

/**
 * Failure handler for Spring Security's {@code OAuth2AuthorizationRequestRedirectFilter}.
 *
 * <p>Spring answers every failure to build an authorization request with HTTP 500. This
 * handler answers with the status of a {@link ResponseStatusException} when that is the
 * cause, so an invalid {@code acr_values} or {@code max_age} rejected by
 * {@link KindeOAuth2AuthorizationRequestResolver} is reported as HTTP 400. Anything else
 * stays an HTTP 500, as Spring's default does.
 *
 * <p>{@link Kinde#configureOAuth2WithPkce} installs it. If you add the resolver to
 * {@code oauth2Login} yourself, set this handler on the redirect filter with an
 * {@code ObjectPostProcessor}, or invalid step-up parameters are answered with HTTP 500.
 */
public class KindeAuthorizationRequestFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        for (Throwable t = exception; t != null; t = t.getCause()) {
            if (t instanceof ResponseStatusException) {
                ResponseStatusException status = (ResponseStatusException) t;
                String reason = status.getReason();
                response.sendError(status.getStatusCode().value(),
                        reason != null ? reason : HttpStatus.BAD_REQUEST.getReasonPhrase());
                return;
            }
            if (t.getCause() == t) {
                break;
            }
        }
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}
