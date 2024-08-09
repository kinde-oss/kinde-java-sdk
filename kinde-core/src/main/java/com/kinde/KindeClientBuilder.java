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
        this.parameters.put(KindParameters.DOMAIN.getValue(),domain);
        return this;
    }

    public KindeClientBuilder redirectUri(String redirectUri) {
        this.parameters.put(KindParameters.REDIRECT_URI.getValue(),redirectUri);
        return this;
    }

    public KindeClientBuilder logoutRedirectUri(String logoutRedirectUri) {
        this.parameters.put(KindParameters.LOGOUT_REDIRECT_URI.getValue(),logoutRedirectUri);
        return this;
    }

    public KindeClientBuilder openidEndpoint(String openidEndpoint) {
        this.parameters.put(KindParameters.OPENID_ENDPOINT.getValue(),openidEndpoint);
        return this;
    }

    public KindeClientBuilder authorizationEndpoint(String authorizationEndpoint) {
        this.parameters.put(KindParameters.AUTHORIZATION_ENDPOINT.getValue(),authorizationEndpoint);
        return this;
    }

    public KindeClientBuilder tokenEndpoint(String tokenEndpoint) {
        this.parameters.put(KindParameters.TOKEN_ENDPOINT.getValue(),tokenEndpoint);
        return this;
    }

    public KindeClientBuilder logoutEndpoint(String logoutEndpoint) {
        this.parameters.put(KindParameters.TOKEN_ENDPOINT.getValue(),logoutEndpoint);
        return this;
    }

    public KindeClientBuilder clientId(String clientId) {
        this.parameters.put(KindParameters.CLIENT_ID.getValue(),clientId);
        return this;
    }

    public KindeClientBuilder clientSecret(String clientSecret) {
        this.parameters.put(KindParameters.CLIENT_SECRET.getValue(),clientSecret);
        return this;
    }

    public KindeClientBuilder grantType(String grantType) {
        this.parameters.put(KindParameters.GRANT_TYPE.getValue(),grantType);
        return this;
    }

    public KindeClientBuilder scopes(String domain) {
        this.parameters.put(KindParameters.SCOPES.getValue(), Arrays.asList(domain.split(",")));
        return this;
    }

    public KindeClientBuilder addScope(String scope) {
        List.class.cast(this.parameters.computeIfAbsent(KindParameters.SCOPES.getValue(), k -> new ArrayList<String>()))
                .add(scope);
        return this;
    }

    public KindeClientBuilder protocol(String protocol) {
        this.parameters.put(KindParameters.PROTOCOL.getValue(),protocol);
        return this;
    }


    public KindeClient build() {
        Set keys = this.parameters.keySet();
        if (!keys.containsAll(
                Arrays.asList(KindParameters.CLIENT_ID.getValue(),
                        KindParameters.CLIENT_SECRET.getValue(),
                        KindParameters.DOMAIN.getValue()))) {
            throw new InvalidParameterException("The required parameters were not set");
        }
        // create a child injector for the scope of this client
        Injector injector = KindeGuiceSingleton.getInstance().getInjector().createChildInjector(new KindeClientGuiceModule(this.parameters));
        return injector.getInstance(KindeClient.class);
    }
}
