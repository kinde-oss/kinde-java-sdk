package com.kinde;

import com.kinde.authorization.AuthorizationUrl;
import com.kinde.entitlements.KindeEntitlements;
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

    AuthorizationUrl createOrg(String orgName);

    default AuthorizationUrl createOrg(String orgName, String invitationCode) {
        return createOrg(orgName);
    }

    AuthorizationUrl register();

    default AuthorizationUrl register(String invitationCode) {
        return register();
    }

    default AuthorizationUrl handleInvitation(String invitationCode) {
        throw new UnsupportedOperationException("handleInvitation is not supported by this implementation");
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
}
