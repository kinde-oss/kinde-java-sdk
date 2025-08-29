package com.kinde.token;

import com.google.inject.AbstractModule;

/**
 * Guice test module for Kinde token test bindings.
 * This module provides test implementations for token-related classes.
 */
public class KindeTokenGuiceTestModule extends AbstractModule {

    @Override
    protected void configure() {
        // Bind test implementations for token generation
        bind(TestTokenGenerator.class).to(TestTokenGeneratorImpl.class);
    }
}
