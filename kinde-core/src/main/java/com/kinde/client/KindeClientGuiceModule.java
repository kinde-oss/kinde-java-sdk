package com.kinde.client;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kinde.KindParameters;
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

    @Provides
    @KindeAnnotations.ClientConfigDomain
    public String provideDomain() {
        return (String)this.parameters.get(KindParameters.DOMAIN.getValue());
    }

    @Override
    protected void configure() {
        bind(KindeClient.class).to(KindeClientImpl.class);
        if (KindeEnvironmentSingleton.getInstance().getState() == KindeEnvironmentSingleton.State.ACTIVE) {
            bind(OidcMetaData.class).to(OidcMetaDataImpl.class);
        }
    }
}
