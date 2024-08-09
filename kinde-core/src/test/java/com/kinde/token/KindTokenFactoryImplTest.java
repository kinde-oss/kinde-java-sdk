package com.kinde.token;

import com.kinde.KindeClient;
import com.kinde.KindeTokenFactory;
import com.kinde.client.KindeClientGuiceModule;
import com.kinde.client.KindeClientGuiceTestModule;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;

public class KindTokenFactoryImplTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public KindTokenFactoryImplTest(String testName )
    {
        super( testName );
        KindeGuiceSingleton.getInstance(
                new KindeClientGuiceTestModule(),
                new KindeTokenGuiceTestModule());
        KindeEnvironmentSingleton.init(KindeEnvironmentSingleton.State.TEST);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( KindTokenFactoryImplTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testToken() {
        TestTokenGenerator testTokenGenerator = KindeGuiceSingleton.getInstance().getInjector().getInstance(TestTokenGenerator.class);
        KindeClient kindeClient = KindeGuiceSingleton.getInstance().getInjector()
                .createChildInjector(new KindeClientGuiceModule(new HashMap<>())).getInstance(KindeClient.class);
        KindeTokenFactory kindeTokenFactory = kindeClient.tokenFactory();
    }
}
