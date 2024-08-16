package com.kinde.client;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.KindeTokenFactory;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import com.kinde.token.AccessToken;
import com.kinde.token.IDToken;
import com.kinde.token.RefreshToken;
import com.kinde.token.TestKeyGenerator;
import com.kinde.token.jwt.JwtGenerator;
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
        try {
            kindeClient.initClientSession(IDToken.init("TEST", true));
            fail("The test failes as an exception is expected");
        } catch (Exception ex) {
            // ignore
        }
        try {
            kindeClient.initClientSession(AccessToken.init("TEST",false));
            fail("The test failes as an exception is expected");
        } catch (Exception ex) {
            // ignore
        }
        KindeClientSession kindeClientSession = kindeClient.initClientSession(AccessToken.init(JwtGenerator.generateAccessToken(),true));
    }

    /**
     * Rigourous Test :-)
     */
    public void testClientCode() {
        KindeClient kindeClient = KindeGuiceSingleton.getInstance().getInjector()
                .createChildInjector(new KindeClientGuiceModule(new HashMap<>())).getInstance(KindeClient.class);
        try {
            kindeClient.initClientSession((String)null,null);
            fail("The test failes as an exception is expected");
        } catch (Exception ex) {
            // ignore
        }
        try {
            kindeClient.initClientSession("",null);
            fail("The test failes as an exception is expected");
        } catch (Exception ex) {
            // ignore
        }
        KindeClientSession kindeClientSession = kindeClient.initClientSession("1234",null);
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
