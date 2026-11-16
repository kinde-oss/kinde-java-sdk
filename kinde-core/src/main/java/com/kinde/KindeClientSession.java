package com.kinde;

import com.kinde.authorization.AuthorizationUrl;
import com.kinde.entitlements.KindeEntitlements;
import com.kinde.stepup.StepUpRequest;
import com.kinde.token.KindeTokens;
import com.kinde.user.UserInfo;

import java.util.Map;

public interface KindeClientSession {

    KindeTokens retrieveTokens();

    AuthorizationUrl authorizationUrl();

    AuthorizationUrl authorizationUrlWithParameters(Map<String, String> parameters);

    AuthorizationUrl login();

    default AuthorizationUrl login(String invitationCode) {
        return login();
    }

    /**
     * Starts a login flow, optionally including an invitation code and/or connection ID.
     * A connection ID skips Kinde's identity picker and sends the user to a specific
     * authentication method (for example a social or enterprise connection).
     *
     * @param invitationCode optional invitation code; ignored when null or blank
     * @param connectionId optional connection ID; ignored when null or blank
     * @return the authorization URL to redirect the user to
     * @throws UnsupportedOperationException if {@code connectionId} is non-blank and this
     *         implementation does not support connection IDs
     */
    default AuthorizationUrl login(String invitationCode, String connectionId) {
        requireConnectionIdSupport(connectionId);
        return login(invitationCode);
    }

    AuthorizationUrl createOrg(String orgName);

    default AuthorizationUrl createOrg(String orgName, String invitationCode) {
        return createOrg(orgName);
    }

    /**
     * Starts a create-organization flow, optionally including an invitation code and/or connection ID.
     *
     * @param orgName the organization name; must be non-blank
     * @param invitationCode optional invitation code; ignored when null or blank
     * @param connectionId optional connection ID; ignored when null or blank
     * @return the authorization URL to redirect the user to
     * @throws UnsupportedOperationException if {@code connectionId} is non-blank and this
     *         implementation does not support connection IDs
     */
    default AuthorizationUrl createOrg(String orgName, String invitationCode, String connectionId) {
        requireConnectionIdSupport(connectionId);
        return createOrg(orgName, invitationCode);
    }

    AuthorizationUrl register();

    default AuthorizationUrl register(String invitationCode) {
        return register();
    }

    /**
     * Starts a registration flow, optionally including an invitation code and/or connection ID.
     *
     * @param invitationCode optional invitation code; ignored when null or blank
     * @param connectionId optional connection ID; ignored when null or blank
     * @return the authorization URL to redirect the user to
     * @throws UnsupportedOperationException if {@code connectionId} is non-blank and this
     *         implementation does not support connection IDs
     */
    default AuthorizationUrl register(String invitationCode, String connectionId) {
        requireConnectionIdSupport(connectionId);
        return register(invitationCode);
    }

    default AuthorizationUrl handleInvitation(String invitationCode) {
        throw new UnsupportedOperationException("handleInvitation is not supported by this implementation");
    }

    /**
     * Starts a step-up authentication (RFC 9470): asks Kinde for a stronger or more recent
     * authentication before a sensitive action. Kinde collects only what is missing and the
     * user keeps their session.
     *
     * <p>This is a normal interactive authorization request with {@code acr_values} and/or
     * {@code max_age}. Redirect the user to the returned URL and keep the returned object
     * until the callback. The tokens from that callback replace the current ones; refreshing
     * the old tokens keeps the old context.
     *
     * @param request what to require, see {@link StepUpRequest}
     * @return the authorization URL to redirect the user to
     * @throws IllegalArgumentException if the request asks for neither a context nor a
     *         maximum age, a value is invalid, or {@code prompt=none} is passed (Kinde can't
     *         collect a factor without showing a screen)
     * @throws com.kinde.exceptions.StepUpNotSupportedException if the environment doesn't
     *         advertise step-up; Kinde would ignore the parameters there
     * @throws UnsupportedOperationException if this implementation does not support step-up
     */
    default AuthorizationUrl stepUp(StepUpRequest request) {
        throw new UnsupportedOperationException("stepUp is not supported by this implementation");
    }

    /**
     * Whether the Kinde environment supports step-up authentication, that is whether its
     * OpenID discovery document has {@code acr_values_supported}. Without support, Kinde
     * ignores {@code acr_values} and {@code max_age}, tokens carry no {@code acr} or
     * {@code amr}, and {@code auth_time} may be the token's issue time.
     */
    default boolean isStepUpSupported() {
        return false;
    }

    AuthorizationUrl logout() throws Exception;

    AuthorizationUrl generatePortalUrl(String domain, String returnUrl, String subNav);

    UserInfo retrieveUserInfo();

    /**
     * Gets the entitlements functionality for this session.
     *
     * @return The KindeEntitlements instance
     * @throws UnsupportedOperationException if entitlements functionality is not implemented
     */
    default KindeEntitlements entitlements() {
        throw new UnsupportedOperationException("entitlements() not implemented");
    }

    /**
     * Gets the domain for this session.
     *
     * @return The domain string, or null if not available
     */
    default String getDomain() {
        return null;
    }

    /**
     * Gets the access token for this session.
     *
     * @return The access token string, or null if not available
     */
    default String getAccessToken() {
        return null;
    }

    private static void requireConnectionIdSupport(String connectionId) {
        if (connectionId != null && !connectionId.isBlank()) {
            throw new UnsupportedOperationException("connectionId is not supported by this implementation");
        }
    }
}
