package com.kinde;

import com.kinde.authorization.AuthorizationUrl;
import com.kinde.config.KindeConfig;
import com.kinde.token.KindeToken;

public interface KindeClient {

    /**
     * This is a client session for a given access token provided by an end user.
     *
     * @param code The code from the server, used by the server to validate and generate a new
     * @return
     */
    KindeClientSession initClientSession(String code, AuthorizationUrl authorizationUrl);

    /**
     * This is a client session for a given access token provided by an end user.
     *
     * @param kindeToken Either a refresh token used to generate a new token, or an access token used for role information
     * @return
     */
    KindeClientSession initClientSession(KindeToken kindeToken);

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


    /**
     * The configuration for this client.
     *
     * @return KindeConfig object
     */
    KindeConfig kindeConfig();
}
