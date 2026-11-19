package com.kinde.exceptions;

/**
 * Thrown when a step-up is requested on a Kinde environment whose discovery document has no
 * {@code acr_values_supported}. Kinde would ignore {@code acr_values} and {@code max_age}
 * there, so nothing is sent. To force a fresh sign-in on such an environment, start a login
 * with {@code prompt=login} instead.
 */
public class StepUpNotSupportedException extends IllegalStateException {

    public StepUpNotSupportedException(String message) {
        super(message);
    }
}
