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
     * Return an instance of the
     * @return
     */
    KindeClientSession clientSession();


    /**
     * Return an instance of the token factory
     *
     * @return
     */
    KindeTokenFactory tokenFactory();
}
