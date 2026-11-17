package com.kinde.token;

import com.kinde.stepup.AuthenticationContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class KindeTokens {
    List<String> scopes;
    IDToken idToken;
    AccessToken accessToken;
    RefreshToken refreshToken;

    /**
     * How the user authenticated, read from the {@code acr}, {@code amr} and {@code auth_time}
     * claims of these tokens (step-up authentication).
     *
     * <p>Use {@link AuthenticationContext#satisfies} before a sensitive action, and start a
     * step-up when it returns false. Refreshing tokens never changes the answer. A token whose
     * signature could not be verified gives an empty context, which meets no requirement.
     *
     * @param trustAuthTime pass {@code KindeClientSession.isStepUpSupported()}. Without step-up
     *        support {@code auth_time} may be the token's issue time, so it is left out and no
     *        {@code max_age} requirement is met.
     */
    public AuthenticationContext getAuthenticationContext(boolean trustAuthTime) {
        if ((idToken != null && !idToken.valid()) || (accessToken != null && !accessToken.valid())) {
            return AuthenticationContext.empty();
        }
        return AuthenticationContext.fromClaims(
                idToken == null ? null : idToken::getClaim,
                accessToken == null ? null : accessToken::getClaim,
                trustAuthTime);
    }
}
