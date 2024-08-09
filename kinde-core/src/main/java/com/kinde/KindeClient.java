package com.kinde;

import com.kinde.token.KindeToken;

public interface KindeClient {

    /**
     * This is a client session for a given access token provided by an end user.
     *
     * @param accessToken
     * @return
     */
    KindeClientSession initClientSession(KindeToken accessToken);

    /**
     *
     * @return
     */
    KindeClientSession clientSession();


    /**
     *
     *
     * @return
     */
    KindeTokenFactory tokenFactory();
}
