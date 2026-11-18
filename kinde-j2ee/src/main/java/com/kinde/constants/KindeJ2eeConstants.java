package com.kinde.constants;

public class KindeJ2eeConstants {
    public final static String KINDE_TOKENS = "KINDE_TOKENS";
    /** Session attribute holding the principal and tokens of the current sign-in as one {@link com.kinde.principal.KindeAuthentication}. */
    public final static String KINDE_AUTHENTICATION = "KINDE_AUTHENTICATION";
    public final static String AUTHORIZATION_URL = "AuthorizationUrl";
    public final static String AUTHENTICATED_USER = "authenticatedUser";
    public final static String POST_LOGIN_URL = "post_login_url";
    /** Request attribute holding the KindeCallbackException when Kinde returned an OAuth error to the callback. */
    public final static String KINDE_CALLBACK_ERROR = "kindeCallbackError";
    /** Query and init parameter: the authentication context a step-up requires ("mfa" or "phr"). */
    public final static String ACR_VALUES = "acr_values";
    /** Query and init parameter: the maximum age, in seconds, of the last authentication a step-up requires. */
    public final static String MAX_AGE = "max_age";
}
