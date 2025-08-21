package com.kinde;

import com.kinde.accounts.KindeAccountsClient;
import com.kinde.token.KindeToken;

public interface KindeTokenFactory {

    KindeToken parse(String token);

    /**
     * Parses a token with KindeAccountsClient for hard check functionality.
     * 
     * @param token The token to parse
     * @param accountsClient The KindeAccountsClient for API fallback
     * @return The parsed KindeToken with hard check capabilities
     */
    KindeToken parse(String token, KindeAccountsClient accountsClient);
}
