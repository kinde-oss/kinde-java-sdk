package com.kinde.token;

import com.google.inject.AbstractModule;

/**
 * Base Guice test module for Kinde client test bindings.
 * This module provides common test implementations that are shared across all modules.
 * 
 * Subclasses should extend this and add their module-specific bindings.
 */
public abstract class BaseKindeClientGuiceTestModule extends AbstractModule {

    @Override
    protected void configure() {
        // Common bindings shared across all modules
        bind(TestKeyGenerator.class).to(TestKeyGeneratorImpl.class);
        
        // Subclasses should call super.configure() and add their specific bindings
        configureModuleSpecificBindings();
    }
    
    /**
     * Override this method to add module-specific bindings.
     * This method is called after the common bindings are configured.
     */
    protected abstract void configureModuleSpecificBindings();
}
