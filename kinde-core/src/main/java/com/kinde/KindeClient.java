package com.kinde;

import com.kinde.authorization.AuthorizationUrl;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.token.KindeToken;

public interface KindeClient {

    /**
     * This is a client session for a given access token provided by an end user.
     *
     * @param code The code from the server, used by the server to validate and generate a new
     * @param authorizationUrl The url that was generated when the login request was made.
     * @return A reference to the kinde client session for the code.
     */
    KindeClientSession initClientSession(String code, AuthorizationUrl authorizationUrl);

    /**
     * This is a client session for a given access token provided by an end user.
     *
     * @param kindeToken Either a refresh token used to generate a new token, or an access token used for role information
     * @return A reference to the kinde client session for the given token.
     */
    KindeClientSession initClientSession(KindeToken kindeToken);

    /**
     * Return an instance of the client session for the service that instanciated this client. This is an M2M session.
     * @return A session for the client. This is an M2M session or a session used to perform tasks against the API.
     */
    KindeClientSession clientSession();


    /**
     * Return an instance of the token factory, used to validate tokens supplied to it.
     *
     * @return A reference to the kinde token factory, used to validated and parse tokens.
     */
    KindeTokenFactory tokenFactory();


    /**
     * The configuration for this client.
     *
     * @return KindeConfig object
     */
    KindeConfig kindeConfig();

    /**
     * A reference to the OIDC meta data. This can be used to determine the configuration on the kinde server.
     *
     * @return An OIDC meta data object, retrieved from Kinde.
     */
    OidcMetaData oidcMetaData();
}
