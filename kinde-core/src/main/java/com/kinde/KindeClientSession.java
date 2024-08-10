package com.kinde;


import com.kinde.authorization.AuthorizationType;
import com.kinde.token.KindeToken;

import java.net.URL;
import java.util.List;

public interface KindeClientSession {

    List<KindeToken> retrieveTokens();

    URL authorizationUrl(AuthorizationType authorizationType);
}
