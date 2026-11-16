package com.kinde.stepup;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication contexts that Kinde recognises in {@code acr_values} and returns in the
 * {@code acr} claim (RFC 9470 step-up authentication).
 *
 * <p>Kinde omits the {@code acr} claim for a single-factor sign-in, so an absent {@code acr}
 * means "neither {@link #MFA} nor {@link #PHR}".
 */
public enum AcrValue {

    /** A second factor was verified. */
    MFA("mfa", 1),

    /** Phishing-resistant: a passkey with user verification. Also meets {@link #MFA}. */
    PHR("phr", 2);

    // Values from other providers that Kinde reads as mfa.
    private static final Map<String, AcrValue> ALIASES = Map.of(
            "http://schemas.openid.net/pape/policies/2007/06/multi-factor", MFA,
            "urn:okta:loa:2fa:any", MFA);

    private final String value;
    private final int rank;

    AcrValue(String value, int rank) {
        this.value = value;
        this.rank = rank;
    }

    /** The value sent in {@code acr_values} and found in the {@code acr} claim. */
    public String value() {
        return value;
    }

    /**
     * Whether an authentication performed at this context meets {@code required}.
     * {@link #PHR} meets {@link #MFA}; {@link #MFA} does not meet {@link #PHR}.
     */
    public boolean satisfies(AcrValue required) {
        return required == null || this.rank >= required.rank;
    }

    /**
     * Parses a single {@code mfa} / {@code phr} value, case-insensitively.
     *
     * @return the matching value, or empty for anything else (including aliases)
     */
    public static Optional<AcrValue> parse(String value) {
        if (value == null) {
            return Optional.empty();
        }
        String lower = value.trim().toLowerCase(Locale.ROOT);
        for (AcrValue acr : values()) {
            if (acr.value.equals(lower)) {
                return Optional.of(acr);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the strongest context Kinde would apply for a space-separated {@code acr_values}
     * string. Kinde applies the highest recognised value and ignores the rest. Aliases that
     * Kinde reads as {@code mfa} are recognised here.
     *
     * @return {@link #PHR}, {@link #MFA}, or empty when nothing is recognised
     */
    public static Optional<AcrValue> strongest(String acrValues) {
        if (acrValues == null || acrValues.isBlank()) {
            return Optional.empty();
        }
        AcrValue best = null;
        for (String part : acrValues.trim().split("\\s+")) {
            AcrValue candidate = parse(part).orElse(ALIASES.get(part.toLowerCase(Locale.ROOT)));
            if (candidate != null && (best == null || candidate.rank > best.rank)) {
                best = candidate;
            }
        }
        return Optional.ofNullable(best);
    }

    @Override
    public String toString() {
        return value;
    }
}
