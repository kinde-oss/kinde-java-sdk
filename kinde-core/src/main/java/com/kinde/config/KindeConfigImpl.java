package com.kinde.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kinde.authorization.AuthorizationType;
import com.kinde.guice.KindeAnnotations;

import java.util.List;
import java.util.Map;

@Singleton
public class KindeConfigImpl implements KindeConfig {

    private Map<String, Object> parameters;

    @Inject
    public KindeConfigImpl(@KindeAnnotations.ClientConfigParameters Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String domain() {
        return (String)this.parameters.get(KindeParameters.DOMAIN.getValue());
    }

    @Override
    public String redirectUri() {
        return (String)this.parameters.get(KindeParameters.REDIRECT_URI.getValue());
    }

    @Override
    public String logoutRedirectUri() {
        return (String)this.parameters.get(KindeParameters.LOGOUT_REDIRECT_URI.getValue());
    }

    @Override
    public String openidEndpoint() {
        return (String)this.parameters.get(KindeParameters.OPENID_ENDPOINT.getValue());
    }

    @Override
    public String authorizationEndpoint() {
        return (String)this.parameters.get(KindeParameters.AUTHORIZATION_ENDPOINT.getValue());
    }

    @Override
    public String tokenEndpoint() {
        return (String)this.parameters.get(KindeParameters.TOKEN_ENDPOINT.getValue());
    }

    @Override
    public String logoutEndpoint() {
        return (String)this.parameters.get(KindeParameters.LOGOUT_ENDPOINT.getValue());
    }

    @Override
    public String clientId() {
        return (String)this.parameters.get(KindeParameters.CLIENT_ID.getValue());
    }

    @Override
    public String clientSecret() {
        return (String)this.parameters.get(KindeParameters.CLIENT_SECRET.getValue());
    }

    @Override
    public AuthorizationType grantType() {
        return (AuthorizationType)this.parameters.get(KindeParameters.GRANT_TYPE.getValue());
    }

    @Override
    public List<String> scopes() {
        return (List<String>)this.parameters.get(KindeParameters.SCOPES.getValue());
    }

    @Override
    public String protocol() {
        return (String)this.parameters.get(KindeParameters.PROTOCOL.getValue());
    }

    public List<String> audience() {
        return (List<String>)this.parameters.get(KindeParameters.AUDIENCE.getValue());
    }

    @Override
    public Map<String, Object> parameters() {
        return this.parameters;
    }

    @Override
    public String getStringValue(String key) {
        return (String)this.parameters.get(key);
    }

    @Override
    public Long getLongValue(String key) {
        return (Long)this.parameters.get(key);
    }

    @Override
    public Object getValue(String key) {
        return this.parameters.get(key);
    }
}
