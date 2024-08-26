package com.kinde.client;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.KindeTokenFactory;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import com.kinde.token.AccessToken;
import com.kinde.token.IDToken;
import com.kinde.token.jwt.JwtGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class KindClientImplTest {

    @BeforeEach
    public void setup() {
        KindeGuiceSingleton.init(new KindeClientGuiceTestModule());
        KindeEnvironmentSingleton.init(KindeEnvironmentSingleton.State.TEST);
    }

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testClient() {
        KindeClient kindeClient = KindeGuiceSingleton.getInstance().getInjector()
                .createChildInjector(new KindeClientGuiceModule(new HashMap<>())).getInstance(KindeClient.class);
        KindeClientSession kindeClientSession = kindeClient.clientSession();
    }

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testClientTokenTest() {
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
        assertNotNull(kindeClient);
        assertNotNull(kindeClientSession);
    }


    /**
     * Rigourous Test :-)
     */
    @Test
    public void testClientCodeTest() {
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
        assertNotNull(kindeClient);
        assertNotNull(kindeClientSession);
    }

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testToken() {
        KindeClient kindeClient = KindeGuiceSingleton.getInstance().getInjector()
                .createChildInjector(new KindeClientGuiceModule(new HashMap<>())).getInstance(KindeClient.class);
        KindeTokenFactory kindeTokenFactory = kindeClient.tokenFactory();
        assertNotNull(kindeClient);
        assertNotNull(kindeTokenFactory);
    }
}
