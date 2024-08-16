package com.kinde;


import com.kinde.authorization.AuthorizationUrl;
import com.kinde.token.KindeToken;
import com.kinde.user.UserInfo;

import java.util.List;

public interface KindeClientSession {

    List<KindeToken> retrieveTokens();

    AuthorizationUrl authorizationUrl();

    UserInfo retrieveUserInfo();
}
