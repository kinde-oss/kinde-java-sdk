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

    AuthorizationUrl createOrg(String orgName);

    AuthorizationUrl register();

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
