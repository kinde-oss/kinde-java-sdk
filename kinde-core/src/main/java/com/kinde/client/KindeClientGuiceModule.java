package com.kinde.client;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kinde.config.KindeConfig;
import com.kinde.config.KindeConfigImpl;
import com.kinde.config.KindeParameters;
import com.kinde.KindeClient;
import com.kinde.client.oidc.OidcMetaDataImpl;
import com.kinde.guice.KindeAnnotations;
import com.kinde.guice.KindeEnvironmentSingleton;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KindeClientGuiceModule extends AbstractModule {

    private Map<String, Object> parameters;


    @Provides
    @KindeAnnotations.ClientConfigParameters
    public Map<String, Object> provideParameters() {
        return this.parameters;
    }

    @Override
    protected void configure() {
        if (KindeEnvironmentSingleton.getInstance().getState() == KindeEnvironmentSingleton.State.ACTIVE) {
            bind(OidcMetaData.class).to(OidcMetaDataImpl.class);
        }
        bind(KindeConfig.class).to(KindeConfigImpl.class);
        bind(KindeClient.class).to(KindeClientImpl.class);

    }
}
