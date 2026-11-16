package com.kinde.exceptions;

import com.kinde.stepup.StepUp;

/**
 * Thrown when the user cannot meet the requested authentication context (for example no
 * enrolled MFA factor, or no passkey).
 *
 * <p>This is a final answer: the same request gives the same error. Don't retry and don't
 * redirect to login. Tell the user why instead.
 */
public class UnmetAuthenticationRequirementsException extends KindeCallbackException {

    public UnmetAuthenticationRequirementsException(String errorDescription, String state,
                                                    String acrValues, Integer maxAge) {
        super(StepUp.ERROR_UNMET_AUTHENTICATION_REQUIREMENTS, errorDescription, state, acrValues, maxAge);
    }
}
