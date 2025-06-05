package com.kinde;


import com.kinde.authorization.AuthorizationUrl;
import com.kinde.token.KindeToken;
import com.kinde.token.KindeTokens;
import com.kinde.user.UserInfo;

import java.net.URL;
import java.util.List;
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
}
