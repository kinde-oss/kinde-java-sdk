package com.kinde.token;

import com.kinde.KindeClient;
import com.kinde.KindeTokenFactory;
import com.kinde.client.KindeClientGuiceModule;
import com.kinde.client.KindeClientGuiceTestModule;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class KindTokenFactoryImplTest {

    @BeforeEach
    public void setup() {
        KindeGuiceSingleton.getInstance(
                new KindeClientGuiceTestModule(),
                new KindeTokenGuiceTestModule());
        KindeEnvironmentSingleton.init(KindeEnvironmentSingleton.State.TEST);
    }

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testAccessTokenTest() {
        TestTokenGenerator testTokenGenerator = KindeGuiceSingleton.getInstance().getInjector().getInstance(TestTokenGenerator.class);
        KindeClient kindeClient = KindeGuiceSingleton.getInstance().getInjector()
                .createChildInjector(new KindeClientGuiceModule(new HashMap<>())).getInstance(KindeClient.class);
        KindeTokenFactory kindeTokenFactory = kindeClient.tokenFactory();
        KindeToken kindeToken = kindeTokenFactory.parse(testTokenGenerator.generateAccessToken());
        assertTrue( kindeToken instanceof AccessToken );
        assertTrue( kindeToken.valid() );
    }

    @Test
    public void testIdTokenTest() {
        TestTokenGenerator testTokenGenerator = KindeGuiceSingleton.getInstance().getInjector().getInstance(TestTokenGenerator.class);
        KindeClient kindeClient = KindeGuiceSingleton.getInstance().getInjector()
                .createChildInjector(new KindeClientGuiceModule(new HashMap<>())).getInstance(KindeClient.class);
        KindeTokenFactory kindeTokenFactory = kindeClient.tokenFactory();
        KindeToken kindeToken = kindeTokenFactory.parse(testTokenGenerator.generateIdToken());
        assertTrue( kindeToken instanceof IDToken );
        assertTrue( kindeToken.valid() );
    }

    @Test
    public void testRefreshTokenTest() {
        TestTokenGenerator testTokenGenerator = KindeGuiceSingleton.getInstance().getInjector().getInstance(TestTokenGenerator.class);
        KindeClient kindeClient = KindeGuiceSingleton.getInstance().getInjector()
                .createChildInjector(new KindeClientGuiceModule(new HashMap<>())).getInstance(KindeClient.class);
        KindeTokenFactory kindeTokenFactory = kindeClient.tokenFactory();
        KindeToken kindeToken = kindeTokenFactory.parse(testTokenGenerator.generateRefreshToken());
        assertTrue( kindeToken instanceof RefreshToken );
        assertTrue( kindeToken.valid() );
    }
}
