package com.kinde.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kinde.guice.KindeAnnotations;

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
