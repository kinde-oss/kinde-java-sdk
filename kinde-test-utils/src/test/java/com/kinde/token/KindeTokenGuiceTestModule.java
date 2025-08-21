package com.kinde.token;

import com.google.inject.AbstractModule;

/**
 * Guice test module for token-related test bindings.
 * This module provides test implementations for token generation and key generation.
 * 
 * This is a shared test utility to eliminate duplication across multiple modules.
 */
public class KindeTokenGuiceTestModule extends AbstractModule {
    
    private final boolean includeKeyGenerator;
    
    /**
     * Creates a test module with only token generator bindings (simple variant).
     */
    public KindeTokenGuiceTestModule() {
        this(false);
    }
    
    /**
     * Creates a test module with optional key generator bindings.
     * 
     * @param includeKeyGenerator whether to include TestKeyGenerator bindings
     */
    public KindeTokenGuiceTestModule(boolean includeKeyGenerator) {
        this.includeKeyGenerator = includeKeyGenerator;
    }
    
    @Override
    protected void configure() {
        // Always bind test implementations for token generation
        bind(TestTokenGenerator.class).to(TestTokenGeneratorImpl.class);
        
        // Optionally bind test implementations for key generation
        if (includeKeyGenerator) {
            bind(TestKeyGenerator.class).to(TestKeyGeneratorImpl.class);
        }
    }
}
