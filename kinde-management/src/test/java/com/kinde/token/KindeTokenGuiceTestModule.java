package test.java.com.kinde.token;

import com.google.inject.AbstractModule;
import test.java.com.kinde.token.TestKeyGenerator;
import test.java.com.kinde.token.TestKeyGeneratorImpl;
import test.java.com.kinde.token.TestTokenGenerator;
import test.java.com.kinde.token.TestTokenGeneratorImpl;

public class KindeTokenGuiceTestModule extends AbstractModule {
    @Override
    protected void configure() {
        // Bind test implementations for token generation
        bind(TestKeyGenerator.class).to(TestKeyGeneratorImpl.class);
        bind(TestTokenGenerator.class).to(TestTokenGeneratorImpl.class);
    }
}
