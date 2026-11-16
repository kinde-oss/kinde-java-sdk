package com.kinde.stepup;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * How a user authenticated, read from the {@code acr}, {@code amr} and {@code auth_time} claims
 * of tokens whose signature has been verified.
 *
 * <p>Use {@link #satisfies(AcrValue, Integer)} to decide whether a sensitive action may go
 * ahead. {@code amr} is informational only: Kinde has already folded it into {@code acr}.
 */
public final class AuthenticationContext {

    private static final AuthenticationContext EMPTY =
            new AuthenticationContext(null, Collections.emptyList(), null);

    private final AcrValue acr;
    private final List<String> amr;
    private final Long authTime;

    private AuthenticationContext(AcrValue acr, List<String> amr, Long authTime) {
        this.acr = acr;
        this.amr = amr;
        this.authTime = authTime;
    }

    /** A context that meets no requirement: no signed-in user, or nothing known. */
    public static AuthenticationContext empty() {
        return EMPTY;
    }

    /**
     * Builds the context from decoded token claims.
     *
     * <p>{@code auth_time} is read from the ID token first, because Kinde always sets it there
     * and only sets it on the access token when it can establish it. The access token is the
     * fallback for each claim. Tokens with different {@code sub} claims never describe one
     * authentication, so they give an empty context.
     *
     * @param idTokenClaims claims of the ID token, or null
     * @param accessTokenClaims claims of the access token, or null
     * @param trustAuthTime pass {@code false} when the environment doesn't advertise step-up;
     *        {@code auth_time} may then be the token's issue time, so it is left out
     */
    public static AuthenticationContext fromClaims(
            Map<String, Object> idTokenClaims,
            Map<String, Object> accessTokenClaims,
            boolean trustAuthTime) {
        Function<String, Object> id = idTokenClaims == null ? key -> null : idTokenClaims::get;
        Function<String, Object> access = accessTokenClaims == null ? key -> null : accessTokenClaims::get;
        return fromClaims(id, access, trustAuthTime);
    }

    /**
     * Same as {@link #fromClaims(Map, Map, boolean)}, for claims exposed through a lookup
     * function (for example {@code token::getClaim}).
     */
    public static AuthenticationContext fromClaims(
            Function<String, Object> idTokenClaims,
            Function<String, Object> accessTokenClaims,
            boolean trustAuthTime) {
        Function<String, Object> id = idTokenClaims == null ? key -> null : idTokenClaims;
        Function<String, Object> access = accessTokenClaims == null ? key -> null : accessTokenClaims;

        Object idSub = id.apply("sub");
        Object accessSub = access.apply("sub");
        if (idSub != null && accessSub != null && !idSub.equals(accessSub)) {
            return EMPTY;
        }

        AcrValue acr = claimAcr(id.apply("acr"));
        if (acr == null) {
            acr = claimAcr(access.apply("acr"));
        }

        List<String> amr = claimAmr(id.apply("amr"));
        if (amr == null) {
            amr = claimAmr(access.apply("amr"));
        }

        Long authTime = null;
        if (trustAuthTime) {
            authTime = claimAuthTime(id.apply("auth_time"));
            if (authTime == null) {
                authTime = claimAuthTime(access.apply("auth_time"));
            }
        }

        return new AuthenticationContext(
                acr,
                amr == null ? Collections.emptyList() : Collections.unmodifiableList(amr),
                authTime);
    }

    /** The context the user reached, or empty for a single-factor sign-in. */
    public Optional<AcrValue> getAcr() {
        return Optional.ofNullable(acr);
    }

    /** The authentication methods used (informational only). Never null. */
    public List<String> getAmr() {
        return amr;
    }

    /** When the user last actively authenticated, in seconds since the epoch, if known. */
    public Optional<Long> getAuthTime() {
        return Optional.ofNullable(authTime);
    }

    /**
     * Checks whether this context meets a requirement, using the system clock and no leeway.
     *
     * @see #satisfies(AcrValue, Integer, Clock, long)
     */
    public boolean satisfies(AcrValue requiredAcr, Integer maxAge) {
        return satisfies(requiredAcr, maxAge, Clock.systemUTC(), 0);
    }

    /**
     * Checks whether this context meets a requirement.
     *
     * @param requiredAcr required context, or null for none. {@link AcrValue#PHR} also meets
     *        {@link AcrValue#MFA}. An absent {@code acr} meets no requirement.
     * @param maxAge maximum age of the last authentication in seconds, or null for none. A
     *        missing {@code auth_time} meets no {@code maxAge} requirement. {@code 0} is almost
     *        never met; use a short window such as 60 instead.
     * @param clock the clock to read the current time from
     * @param leewaySeconds clock tolerance added to {@code maxAge}
     * @return true if the requirement is met. When false, start a step-up with the same
     *         arguments; refreshing tokens never changes the answer.
     * @throws IllegalArgumentException if {@code maxAge} is negative
     */
    public boolean satisfies(AcrValue requiredAcr, Integer maxAge, Clock clock, long leewaySeconds) {
        Objects.requireNonNull(clock, "clock");
        if (maxAge != null) {
            StepUp.normalizeMaxAge(maxAge);
        }
        if (requiredAcr != null && (acr == null || !acr.satisfies(requiredAcr))) {
            return false;
        }
        if (maxAge != null) {
            if (authTime == null) {
                return false;
            }
            long now = clock.instant().getEpochSecond();
            return now - authTime <= maxAge + leewaySeconds;
        }
        return true;
    }

    private static AcrValue claimAcr(Object claim) {
        return claim instanceof String ? AcrValue.parse((String) claim).orElse(null) : null;
    }

    private static List<String> claimAmr(Object claim) {
        if (!(claim instanceof Collection)) {
            return null;
        }
        List<String> methods = new ArrayList<>();
        for (Object value : (Collection<?>) claim) {
            if (value instanceof String) {
                methods.add((String) value);
            }
        }
        return methods;
    }

    private static Long claimAuthTime(Object claim) {
        if (claim instanceof Number) {
            return ((Number) claim).longValue();
        }
        if (claim instanceof Date) {
            return ((Date) claim).getTime() / 1000L;
        }
        if (claim instanceof java.time.Instant) {
            return ((java.time.Instant) claim).getEpochSecond();
        }
        return null;
    }

    @Override
    public String toString() {
        return "AuthenticationContext{acr=" + acr + ", amr=" + amr + ", authTime=" + authTime + '}';
    }
}
