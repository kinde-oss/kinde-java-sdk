package com.kinde.client;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.KindeTokenFactory;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import com.kinde.token.AccessToken;
import com.kinde.token.TestKeyGenerator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;

public class KindClientImplTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public KindClientImplTest(String testName )
    {
        super( testName );
        KindeGuiceSingleton.getInstance(new KindeClientGuiceTestModule());
        KindeEnvironmentSingleton.init(KindeEnvironmentSingleton.State.TEST);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( KindClientImplTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testClient() {
        KindeClient kindeClient = KindeGuiceSingleton.getInstance().getInjector()
                .createChildInjector(new KindeClientGuiceModule(new HashMap<>())).getInstance(KindeClient.class);
        KindeClientSession kindeClientSession = kindeClient.clientSession();
    }

    /**
     * Rigourous Test :-)
     */
    public void testClientToken() {
        KindeClient kindeClient = KindeGuiceSingleton.getInstance().getInjector()
                .createChildInjector(new KindeClientGuiceModule(new HashMap<>())).getInstance(KindeClient.class);
        KindeClientSession kindeClientSession = kindeClient.initClientSession(AccessToken.init("TEST",true));
    }

    /**
     * Rigourous Test :-)
     */
    public void testToken() {
        KindeClient kindeClient = KindeGuiceSingleton.getInstance().getInjector()
                .createChildInjector(new KindeClientGuiceModule(new HashMap<>())).getInstance(KindeClient.class);
        KindeTokenFactory kindeTokenFactory = kindeClient.tokenFactory();
    }
}
