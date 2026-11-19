package com.kinde.spring;

import com.kinde.stepup.AcrValue;
import org.springframework.security.access.AccessDeniedException;

/**
 * Thrown when the caller is authenticated, but not strongly or recently enough for the
 * operation (step-up authentication, RFC 9470).
 *
 * <p>On a resource server, {@link KindeStepUpAccessDeniedHandler} answers it with HTTP 401
 * and a {@code WWW-Authenticate: Bearer error="insufficient_user_authentication"} challenge.
 * In a browser application, handle it by sending the user to
 * {@code /oauth2/authorization/kinde?acr_values=...&max_age=...}.
 */
public class InsufficientUserAuthenticationException extends AccessDeniedException {

    private final transient AcrValue requiredAcr;
    private final Integer requiredMaxAge;

    public InsufficientUserAuthenticationException(AcrValue requiredAcr, Integer requiredMaxAge) {
        super("A different authentication level is required");
        this.requiredAcr = requiredAcr;
        this.requiredMaxAge = requiredMaxAge;
    }

    /** The authentication context the operation requires, or null. */
    public AcrValue getRequiredAcr() {
        return requiredAcr;
    }

    /** The maximum age, in seconds, of the last authentication the operation allows, or null. */
    public Integer getRequiredMaxAge() {
        return requiredMaxAge;
    }
}
