package com.kinde.exceptions;

import com.kinde.stepup.StepUp;

/**
 * Thrown when Kinde returns an OAuth error to the callback URL.
 *
 * <p>Only thrown after the {@code state} parameter has been validated. The user's existing
 * session and tokens are left untouched.
 */
public class KindeCallbackException extends RuntimeException {

    private final String error;
    private final String errorDescription;
    private final String state;
    private final String acrValues;
    private final Integer maxAge;

    public KindeCallbackException(String error, String errorDescription, String state,
                                  String acrValues, Integer maxAge) {
        super(message(error, errorDescription));
        this.error = error;
        this.errorDescription = errorDescription == null ? "" : errorDescription;
        this.state = state;
        this.acrValues = acrValues;
        this.maxAge = maxAge;
    }

    /**
     * Creates the exception for an OAuth error code, using the dedicated subtype when there is one.
     */
    public static KindeCallbackException of(String error, String errorDescription, String state,
                                            String acrValues, Integer maxAge) {
        if (StepUp.ERROR_UNMET_AUTHENTICATION_REQUIREMENTS.equals(error)) {
            return new UnmetAuthenticationRequirementsException(errorDescription, state, acrValues, maxAge);
        }
        if (StepUp.ERROR_LOGIN_REQUIRED.equals(error)) {
            return new LoginRequiredException(errorDescription, state, acrValues, maxAge);
        }
        return new KindeCallbackException(error, errorDescription, state, acrValues, maxAge);
    }

    /** The OAuth error code, for example {@code unmet_authentication_requirements}. */
    public String getError() {
        return error;
    }

    /** The {@code error_description} from the callback, or an empty string. */
    public String getErrorDescription() {
        return errorDescription;
    }

    /** The validated {@code state} from the callback. */
    public String getState() {
        return state;
    }

    /** The {@code acr_values} of the request this answers, or null if none was sent. */
    public String getAcrValues() {
        return acrValues;
    }

    /** The {@code max_age} of the request this answers, or null if none was sent. */
    public Integer getMaxAge() {
        return maxAge;
    }

    private static String message(String error, String errorDescription) {
        if (errorDescription != null && !errorDescription.isBlank()) {
            return error + ": " + errorDescription;
        }
        return error != null ? error : "Authorization failed";
    }
}
