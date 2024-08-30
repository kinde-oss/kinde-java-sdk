package com.kinde;


import com.kinde.authorization.AuthorizationUrl;
import com.kinde.token.KindeToken;
import com.kinde.user.UserInfo;

import java.util.List;
import java.util.Map;

public interface KindeClientSession {

    List<KindeToken> retrieveTokens();

    AuthorizationUrl authorizationUrl();

    AuthorizationUrl authorizationUrlWithParameters(Map<String, String> parameters);

    AuthorizationUrl login();

    AuthorizationUrl createOrg(String orgName);

    AuthorizationUrl register();

    UserInfo retrieveUserInfo();
}
