package com.kinde;


import com.kinde.authorization.AuthorizationUrl;
import com.kinde.token.KindeTokens;
import com.kinde.user.UserInfo;

import java.util.Map;

public interface KindeClientSession {

    KindeTokens retrieveTokens();

    AuthorizationUrl authorizationUrl();

    AuthorizationUrl authorizationUrlWithParameters(Map<String, String> parameters);

    AuthorizationUrl login();

    AuthorizationUrl createOrg(String orgName, String pricingTableKey, String planInterest);

    AuthorizationUrl register(String pricingTableKey, String plan_interest);

    AuthorizationUrl logout() throws Exception;

    UserInfo retrieveUserInfo();
}
