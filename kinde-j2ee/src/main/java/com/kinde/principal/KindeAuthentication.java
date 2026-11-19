package com.kinde.principal;

import com.kinde.token.KindeTokens;

import java.security.Principal;
import java.util.Objects;

/**
 * The principal and the tokens of one sign-in, stored in the session as a single attribute
 * ({@link com.kinde.constants.KindeJ2eeConstants#KINDE_AUTHENTICATION}). Reading this one
 * attribute gives a consistent pair even while a callback in the same session replaces it,
 * which reading the principal and token attributes one by one does not.
 */
public final class KindeAuthentication {

    private final Principal principal;
    private final KindeTokens tokens;

    public KindeAuthentication(Principal principal, KindeTokens tokens) {
        this.principal = Objects.requireNonNull(principal, "principal");
        this.tokens = Objects.requireNonNull(tokens, "tokens");
    }

    public Principal getPrincipal() {
        return principal;
    }

    public KindeTokens getTokens() {
        return tokens;
    }
}
