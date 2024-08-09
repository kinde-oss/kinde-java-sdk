package com.kinde;

import com.google.inject.Injector;
import com.kinde.client.KindeClientGuiceModule;
import com.kinde.guice.KindeGuiceSingleton;

import java.security.InvalidParameterException;
import java.util.*;

public class KindeClientBuilder {

    private Map<String, Object> parameters;

    /**
     * Private constructor to prevent new initiation
     */
    private KindeClientBuilder() {
        this.parameters = new HashMap<>();
    }

    /**
     * The builder
     *
     * @return
     */
    public static KindeClientBuilder builder() {
        return new KindeClientBuilder();
    }


    public KindeClientBuilder domain(String domain) {
        this.parameters.put(KindeParameters.DOMAIN.getValue(),domain);
        return this;
    }

    public KindeClientBuilder redirectUri(String redirectUri) {
        this.parameters.put(KindeParameters.REDIRECT_URI.getValue(),redirectUri);
        return this;
    }

    public KindeClientBuilder logoutRedirectUri(String logoutRedirectUri) {
        this.parameters.put(KindeParameters.LOGOUT_REDIRECT_URI.getValue(),logoutRedirectUri);
        return this;
    }

    public KindeClientBuilder openidEndpoint(String openidEndpoint) {
        this.parameters.put(KindeParameters.OPENID_ENDPOINT.getValue(),openidEndpoint);
        return this;
    }

    public KindeClientBuilder authorizationEndpoint(String authorizationEndpoint) {
        this.parameters.put(KindeParameters.AUTHORIZATION_ENDPOINT.getValue(),authorizationEndpoint);
        return this;
    }

    public KindeClientBuilder tokenEndpoint(String tokenEndpoint) {
        this.parameters.put(KindeParameters.TOKEN_ENDPOINT.getValue(),tokenEndpoint);
        return this;
    }

    public KindeClientBuilder logoutEndpoint(String logoutEndpoint) {
        this.parameters.put(KindeParameters.TOKEN_ENDPOINT.getValue(),logoutEndpoint);
        return this;
    }

    public KindeClientBuilder clientId(String clientId) {
        this.parameters.put(KindeParameters.CLIENT_ID.getValue(),clientId);
        return this;
    }

    public KindeClientBuilder clientSecret(String clientSecret) {
        this.parameters.put(KindeParameters.CLIENT_SECRET.getValue(),clientSecret);
        return this;
    }

    public KindeClientBuilder grantType(String grantType) {
        this.parameters.put(KindeParameters.GRANT_TYPE.getValue(),grantType);
        return this;
    }

    public KindeClientBuilder scopes(String domain) {
        this.parameters.put(KindeParameters.SCOPES.getValue(), Arrays.asList(domain.split(",")));
        return this;
    }

    public KindeClientBuilder addScope(String scope) {
        List.class.cast(this.parameters.computeIfAbsent(KindeParameters.SCOPES.getValue(), k -> new ArrayList<String>()))
                .add(scope);
        return this;
    }

    public KindeClientBuilder protocol(String protocol) {
        this.parameters.put(KindeParameters.PROTOCOL.getValue(),protocol);
        return this;
    }


    public KindeClient build() {
        Set keys = this.parameters.keySet();
        if (!keys.containsAll(
                Arrays.asList(KindeParameters.CLIENT_ID.getValue(),
                        KindeParameters.CLIENT_SECRET.getValue(),
                        KindeParameters.DOMAIN.getValue()))) {
            throw new InvalidParameterException("The required parameters were not set");
        }
        // create a child injector for the scope of this client
        Injector injector = KindeGuiceSingleton.getInstance().getInjector().createChildInjector(new KindeClientGuiceModule(this.parameters));
        return injector.getInstance(KindeClient.class);
    }
}
