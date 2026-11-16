package com.kinde.authorization;

import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import lombok.Getter;

import java.net.URL;

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

    public AuthorizationUrl(URL url, CodeVerifier codeVerifier) {
        this(url, codeVerifier, null, null, null);
    }

    public AuthorizationUrl(URL url, CodeVerifier codeVerifier, String state) {
        this(url, codeVerifier, state, null, null);
    }

    public AuthorizationUrl(URL url, CodeVerifier codeVerifier, String state, String acrValues, Integer maxAge) {
        this.url = url;
        this.codeVerifier = codeVerifier;
        this.state = state;
        this.acrValues = acrValues;
        this.maxAge = maxAge;
    }

    /**
     * Returns a copy without the {@code state} and code verifier, to keep once the callback
     * has been handled. No later callback can match it, so a response cannot be replayed.
     */
    public AuthorizationUrl consumed() {
        return new AuthorizationUrl(url, null, null, acrValues, maxAge);
    }
}
