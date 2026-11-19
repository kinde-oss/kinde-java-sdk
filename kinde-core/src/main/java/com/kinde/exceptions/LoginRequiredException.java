package com.kinde.exceptions;

import com.kinde.stepup.StepUp;

/**
 * Thrown when a {@code prompt=none} request cannot complete without an interactive login.
 * The application decides whether to start one.
 */
public class LoginRequiredException extends KindeCallbackException {

    public LoginRequiredException(String errorDescription, String state,
                                  String acrValues, Integer maxAge) {
        super(StepUp.ERROR_LOGIN_REQUIRED, errorDescription, state, acrValues, maxAge);
    }
}
