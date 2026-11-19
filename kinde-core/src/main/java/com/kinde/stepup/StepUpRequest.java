package com.kinde.stepup;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * What a step-up authentication request asks Kinde for: a stronger context
 * ({@code acr_values}), a more recent authentication ({@code max_age}), or both.
 *
 * <pre>{@code
 * StepUpRequest.builder().acr(AcrValue.MFA).maxAge(300).build();
 * }</pre>
 */
public final class StepUpRequest {

    private final String acrValues;
    private final Integer maxAge;
    private final Map<String, String> parameters;

    private StepUpRequest(String acrValues, Integer maxAge, Map<String, String> parameters) {
        this.acrValues = acrValues;
        this.maxAge = maxAge;
        this.parameters = parameters;
    }

    /** A request for the given context. */
    public static StepUpRequest of(AcrValue acr) {
        return builder().acr(acr).build();
    }

    /** A request for the given context, authenticated at most {@code maxAge} seconds ago. */
    public static StepUpRequest of(AcrValue acr, int maxAge) {
        return builder().acr(acr).maxAge(maxAge).build();
    }

    /** A request for an authentication at most {@code maxAge} seconds old, with no context requirement. */
    public static StepUpRequest ofMaxAge(int maxAge) {
        return builder().maxAge(maxAge).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /** The normalized, space-separated {@code acr_values}, if a context is required. */
    public Optional<String> getAcrValues() {
        return Optional.ofNullable(acrValues);
    }

    /** The {@code max_age} in seconds, if recency is required. */
    public Optional<Integer> getMaxAge() {
        return Optional.ofNullable(maxAge);
    }

    /** Additional authorization parameters to send (for example {@code org_code}). Never null. */
    public Map<String, String> getParameters() {
        return parameters;
    }

    public static final class Builder {
        private final Set<AcrValue> acr = new LinkedHashSet<>();
        private Integer maxAge;
        private final Map<String, String> parameters = new LinkedHashMap<>();

        private Builder() {
        }

        /** Requires the given context(s). Kinde applies the strongest one. */
        public Builder acr(AcrValue... values) {
            if (values != null) {
                for (AcrValue value : values) {
                    if (value != null) {
                        this.acr.add(value);
                    }
                }
            }
            return this;
        }

        /**
         * Requires the context(s) in a space-separated {@code acr_values} string.
         *
         * @throws IllegalArgumentException if it contains anything other than {@code mfa} or {@code phr}
         */
        public Builder acrValues(String acrValues) {
            for (String part : StepUp.normalizeAcrValues(acrValues).split(" ")) {
                AcrValue.parse(part).ifPresent(this.acr::add);
            }
            return this;
        }

        /**
         * Requires an authentication at most {@code maxAge} seconds old. {@code 0} forces a
         * fresh verification.
         *
         * @throws IllegalArgumentException if {@code maxAge} is negative
         */
        public Builder maxAge(int maxAge) {
            this.maxAge = StepUp.normalizeMaxAge(maxAge);
            return this;
        }

        /** Adds an authorization parameter, such as {@code org_code} or {@code lang}. */
        public Builder parameter(String name, String value) {
            if (name != null && value != null) {
                this.parameters.put(name, value);
            }
            return this;
        }

        /** Adds authorization parameters. */
        public Builder parameters(Map<String, String> parameters) {
            if (parameters != null) {
                parameters.forEach(this::parameter);
            }
            return this;
        }

        public StepUpRequest build() {
            String acrValues = acr.isEmpty()
                    ? null
                    : acr.stream().map(AcrValue::value).collect(Collectors.joining(" "));
            return new StepUpRequest(
                    acrValues,
                    maxAge,
                    Collections.unmodifiableMap(new LinkedHashMap<>(parameters)));
        }
    }
}
