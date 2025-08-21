package com.kinde.token;

import com.google.inject.AbstractModule;
import com.kinde.token.TestKeyGenerator;
import com.kinde.token.TestKeyGeneratorImpl;
import com.kinde.token.TestTokenGenerator;
import com.kinde.token.TestTokenGeneratorImpl;

public class KindeTokenGuiceTestModule extends AbstractModule {
    @Override
    protected void configure() {
        // Bind test implementations for token generation
        bind(TestKeyGenerator.class).to(TestKeyGeneratorImpl.class);
        bind(TestTokenGenerator.class).to(TestTokenGeneratorImpl.class);
    }
}
