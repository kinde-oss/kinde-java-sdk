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
        // Only bind OidcMetaData if we're not in TEST mode (to avoid conflicts with test modules)
        // Check if we're in a test environment by looking for test modules in the classpath
        if (KindeEnvironmentSingleton.getInstance().getState() == KindeEnvironmentSingleton.State.ACTIVE && 
            !isTestEnvironment()) {
            bind(OidcMetaData.class).to(OidcMetaDataImpl.class);
        }
        bind(KindeConfig.class).to(KindeConfigImpl.class);
        bind(KindeClient.class).to(KindeClientImpl.class);
    }
    
    /**
     * Checks if we're running in a test environment.
     * This is a simple heuristic to avoid binding conflicts with test modules.
     * 
     * @return true if we're in a test environment
     */
    private boolean isTestEnvironment() {
        try {
            // Check if we're running in a test context by looking for JUnit classes
            Class.forName("org.junit.jupiter.api.Test");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
