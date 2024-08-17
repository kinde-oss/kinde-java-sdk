package com.kinde;

import com.google.inject.Injector;
import com.kinde.authorization.AuthorizationType;
import com.kinde.client.KindeClientGuiceModule;
import com.kinde.config.KindeParameters;
import com.kinde.guice.KindeGuiceSingleton;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

import java.security.InvalidParameterException;
import java.util.*;

@Slf4j
public class KindeClientBuilder {

    private Map<String, Object> parameters;

    /**
     * Private constructor to prevent new initiation
     */
    private KindeClientBuilder() {
        this.parameters = new HashMap<>();
        Dotenv dotenv = Dotenv.load();
        setParameterFromEnvironmental(KindeParameters.DOMAIN,dotenv);
        setParameterFromEnvironmental(KindeParameters.REDIRECT_URI,dotenv);
        setParameterFromEnvironmental(KindeParameters.LOGOUT_REDIRECT_URI,dotenv);
        setParameterFromEnvironmental(KindeParameters.OPENID_ENDPOINT,dotenv);
        setParameterFromEnvironmental(KindeParameters.AUTHORIZATION_ENDPOINT,dotenv);
        setParameterFromEnvironmental(KindeParameters.TOKEN_ENDPOINT,dotenv);
        setParameterFromEnvironmental(KindeParameters.LOGOUT_ENDPOINT,dotenv);
        setParameterFromEnvironmental(KindeParameters.CLIENT_ID,dotenv);
        setParameterFromEnvironmental(KindeParameters.CLIENT_SECRET,dotenv);
        setParameterFromEnvironmental(KindeParameters.GRANT_TYPE,dotenv);
        setParameterFromEnvironmental(KindeParameters.SCOPES,dotenv);
        setParameterFromEnvironmental(KindeParameters.PROTOCOL,dotenv);
        setParameterFromEnvironmental(KindeParameters.AUDIENCE,dotenv);
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

    public KindeClientBuilder grantType(AuthorizationType authorizationType) {
        this.parameters.put(KindeParameters.GRANT_TYPE.getValue(),authorizationType);
        return this;
    }

    public KindeClientBuilder scopes(String scope) {
        this.parameters.put(KindeParameters.SCOPES.getValue(), Arrays.asList(scope.split(",")));
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

    public KindeClientBuilder audience(String audiences) {
        this.parameters.put(KindeParameters.AUDIENCE.getValue(), Arrays.asList(audiences.split(",")));
        return this;
    }

    public KindeClientBuilder addAudience(String audience) {
        List.class.cast(this.parameters.computeIfAbsent(KindeParameters.AUDIENCE.getValue(), k -> new ArrayList<String>()))
                .add(audience);
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

    private void setParameterFromEnvironmental(KindeParameters parameters,Dotenv dotenv) {
        log.debug("Before setting the parameters: {}",dotenv.get(parameters.getValue()));
        if (dotenv.get(parameters.getValue()) != null) {
            log.info("Set the parameter: {}",parameters.getValue());
            this.parameters.put(parameters.getValue(),parameters.getMapper().apply(dotenv.get(parameters.getValue())));
        }
    }
}
