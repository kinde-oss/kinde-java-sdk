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
     * step-up when it returns false. Refreshing tokens never changes the answer.
     */
    public AuthenticationContext getAuthenticationContext() {
        return getAuthenticationContext(true);
    }

    /**
     * @param trustAuthTime pass {@code false} when the Kinde environment does not support
     *        step-up ({@code KindeClientSession.isStepUpSupported()}); {@code auth_time} may
     *        then be the token's issue time, so it is left out
     */
    public AuthenticationContext getAuthenticationContext(boolean trustAuthTime) {
        return AuthenticationContext.fromClaims(
                idToken == null ? null : idToken::getClaim,
                accessToken == null ? null : accessToken::getClaim,
                trustAuthTime);
    }
}
