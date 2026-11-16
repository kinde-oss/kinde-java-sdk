package com.kinde.stepup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Step-up authentication (RFC 9470): parameter names, error codes and the validation shared by
 * the session, servlet and Spring integrations. Everything here is pure (no I/O).
 *
 * <p>Kinde silently ignores an {@code acr_values} it doesn't recognise and treats a malformed
 * {@code max_age} as "re-authenticate". Both would hide a bug, so invalid values are rejected
 * here before anything is sent.
 */
public final class StepUp {

    /** Authorization request parameter: the required authentication context. */
    public static final String ACR_VALUES = "acr_values";

    /** Authorization request parameter: maximum age of the last authentication, in seconds. */
    public static final String MAX_AGE = "max_age";

    /** Callback error: the user cannot meet the requested authentication context. */
    public static final String ERROR_UNMET_AUTHENTICATION_REQUIREMENTS = "unmet_authentication_requirements";

    /** Callback error: a {@code prompt=none} request cannot complete without an interactive login. */
    public static final String ERROR_LOGIN_REQUIRED = "login_required";

    /** {@code WWW-Authenticate} error for an API request whose token's context is too weak or too old. */
    public static final String ERROR_INSUFFICIENT_USER_AUTHENTICATION = "insufficient_user_authentication";

    /** The {@code error_description} Kinde's docs use with {@link #ERROR_INSUFFICIENT_USER_AUTHENTICATION}. */
    public static final String INSUFFICIENT_USER_AUTHENTICATION_DESCRIPTION =
            "A different authentication level is required";

    private static final Pattern DIGITS = Pattern.compile("[0-9]+");

    private StepUp() {
    }

    /**
     * Validates {@code acr_values} strictly and returns the space-separated string to send.
     *
     * @param acrValues {@code "mfa"}, {@code "phr"}, or a space-separated combination
     * @return the lower-cased, de-duplicated value, for example {@code "mfa phr"}
     * @throws IllegalArgumentException if the value is null, blank, or contains anything other
     *         than {@code mfa} or {@code phr}
     */
    public static String normalizeAcrValues(String acrValues) {
        if (acrValues == null || acrValues.isBlank()) {
            throw new IllegalArgumentException("acr_values must not be empty. Expected 'mfa' or 'phr'");
        }
        List<String> normalized = new ArrayList<>();
        for (String part : acrValues.trim().split("\\s+")) {
            AcrValue acr = AcrValue.parse(part).orElseThrow(() -> new IllegalArgumentException(
                    "Unsupported acr value '" + part + "'. Expected 'mfa' or 'phr'"));
            if (!normalized.contains(acr.value())) {
                normalized.add(acr.value());
            }
        }
        return String.join(" ", normalized);
    }

    /**
     * Validates {@code max_age} (seconds since the user last authenticated). {@code 0} is valid
     * and forces a fresh verification.
     *
     * @throws IllegalArgumentException if the value is negative
     */
    public static int normalizeMaxAge(int maxAge) {
        if (maxAge < 0) {
            throw new IllegalArgumentException("max_age must not be negative, got " + maxAge);
        }
        return maxAge;
    }

    /**
     * Normalizes a raw {@code max_age} parameter value (for example from a query string).
     *
     * @return the canonical digits to send, or {@code null} when the value is unset (null or
     *         blank) and the parameter must be omitted
     * @throws IllegalArgumentException if the value is not a non-negative whole number; this
     *         includes {@code "undefined"}, {@code "null"}, {@code "NaN"} and {@code "300.0"}
     */
    public static String normalizeMaxAgeParameter(String maxAge) {
        if (maxAge == null || maxAge.isBlank()) {
            return null;
        }
        String text = maxAge.trim();
        if (!DIGITS.matcher(text).matches()) {
            throw new IllegalArgumentException(
                    "max_age must be a non-negative whole number of seconds, got '" + maxAge + "'");
        }
        try {
            return Integer.toString(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("max_age is too large, got '" + maxAge + "'", e);
        }
    }

    /**
     * Normalizes a raw {@code acr_values} parameter value passed through as a custom
     * authorization parameter. This is a passthrough, so it is lenient: whitespace is
     * collapsed but the values are not restricted to {@code mfa} / {@code phr}.
     *
     * @return the string to send, or {@code null} when the value is unset (null or blank)
     */
    public static String normalizeAcrValuesParameter(String acrValues) {
        if (acrValues == null || acrValues.isBlank()) {
            return null;
        }
        return String.join(" ", acrValues.trim().split("\\s+"));
    }

    /** Whether a space-separated {@code prompt} value contains {@code none}. */
    public static boolean promptIncludesNone(String prompt) {
        if (prompt == null) {
            return false;
        }
        for (String part : prompt.trim().toLowerCase(Locale.ROOT).split("\\s+")) {
            if ("none".equals(part)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Builds the value of the {@code WWW-Authenticate} header an API returns with HTTP 401 when
     * the access token's authentication context is too weak or too old (RFC 9470 section 3).
     *
     * @param acr required context, or null when only recency is required
     * @param maxAge required maximum age in seconds, or null
     */
    public static String insufficientUserAuthenticationChallenge(AcrValue acr, Integer maxAge) {
        StringBuilder challenge = new StringBuilder("Bearer error=\"")
                .append(ERROR_INSUFFICIENT_USER_AUTHENTICATION)
                .append("\", error_description=\"")
                .append(INSUFFICIENT_USER_AUTHENTICATION_DESCRIPTION)
                .append('"');
        if (acr != null) {
            challenge.append(", acr_values=\"").append(acr.value()).append('"');
        }
        if (maxAge != null) {
            challenge.append(", max_age=\"").append(normalizeMaxAge(maxAge)).append('"');
        }
        return challenge.toString();
    }
}
