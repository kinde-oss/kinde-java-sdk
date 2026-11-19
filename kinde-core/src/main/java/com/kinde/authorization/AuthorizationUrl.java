package com.kinde.authorization;

import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import lombok.Getter;

import java.net.URL;
import java.time.Instant;

/**
 * An authorization request that has been built and is waiting for its callback.
 *
 * <p>Keep it (for example in the HTTP session) until the callback arrives: it holds the PKCE
 * code verifier for the token exchange and the {@code state} the callback must echo. Validate
 * the callback with {@link AuthorizationCallback} before using its {@code code}.
 */
@Getter
public class AuthorizationUrl {
    private final URL url;
    private final CodeVerifier codeVerifier;
    /** The {@code state} sent with the request, or null if unknown or already consumed. */
    private final String state;
    /** The {@code acr_values} sent with the request, or null if none (step-up authentication). */
    private final String acrValues;
    /** The {@code max_age} sent with the request, or null if none (step-up authentication). */
    private final Integer maxAge;
    /**
     * When the request was built, in seconds since the epoch. An authentication performed after
     * this moment is fresh for this request, whatever its {@code max_age}.
     */
    private final Long requestedAt;

    public AuthorizationUrl(URL url, CodeVerifier codeVerifier) {
        this(url, codeVerifier, null, null, null);
    }

    public AuthorizationUrl(URL url, CodeVerifier codeVerifier, String state) {
        this(url, codeVerifier, state, null, null);
    }

    public AuthorizationUrl(URL url, CodeVerifier codeVerifier, String state, String acrValues, Integer maxAge) {
        this(url, codeVerifier, state, acrValues, maxAge, Instant.now().getEpochSecond());
    }

    public AuthorizationUrl(URL url, CodeVerifier codeVerifier, String state, String acrValues, Integer maxAge,
                            Long requestedAt) {
        this.url = url;
        this.codeVerifier = codeVerifier;
        this.state = state;
        this.acrValues = acrValues;
        this.maxAge = maxAge;
        this.requestedAt = requestedAt;
    }

    /**
     * The {@code max_age} a callback for this request must meet, at {@code nowEpochSeconds}.
     * An authentication performed after the request was sent is fresh for it, so the allowed
     * age is never less than the time since the request: with {@code max_age=0} the user's
     * fresh verification still passes when the callback arrives a few seconds later.
     *
     * @return the allowed age in seconds, or null when the request has no {@code max_age}
     */
    public Integer effectiveMaxAge(long nowEpochSeconds) {
        return effectiveMaxAge(maxAge, requestedAt, nowEpochSeconds);
    }

    /**
     * The larger of {@code maxAge} and the time since {@code requestedAt}.
     *
     * @param maxAge the required maximum age in seconds, or null for none
     * @param requestedAt when the request was sent, in seconds since the epoch, or null if unknown
     * @return the allowed age in seconds, or null when {@code maxAge} is null
     */
    public static Integer effectiveMaxAge(Integer maxAge, Long requestedAt, long nowEpochSeconds) {
        if (maxAge == null) {
            return null;
        }
        if (requestedAt == null) {
            return maxAge;
        }
        long sinceRequest = Math.max(0L, nowEpochSeconds - requestedAt);
        return (int) Math.min(Integer.MAX_VALUE, Math.max(maxAge.longValue(), sinceRequest));
    }

    /**
     * Returns a copy without the {@code state} and code verifier, to keep once the callback
     * has been handled. No later callback can match it, so a response cannot be replayed.
     */
    public AuthorizationUrl consumed() {
        return new AuthorizationUrl(url, null, null, acrValues, maxAge, requestedAt);
    }
}
