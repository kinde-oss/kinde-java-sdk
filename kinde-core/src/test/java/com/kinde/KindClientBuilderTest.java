package com.kinde;

import com.kinde.client.KindeClientGuiceTestModule;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class KindClientBuilderTest {

    /**
     * Create the test case
     *
     */
    @BeforeEach
    public void setUp()
    {
        KindeGuiceSingleton.init(new KindeClientGuiceTestModule());
        KindeEnvironmentSingleton.init(KindeEnvironmentSingleton.State.TEST);
    }

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testBuilderBasicTest() {
        KindeClientBuilder kindeClientBuilder1 = KindeClientBuilder.builder();
        KindeClientBuilder kindeClientBuilder2 = KindeClientBuilder.builder();
        assertTrue( kindeClientBuilder1 != kindeClientBuilder2 );
        KindeClient kindeClient = kindeClientBuilder1
                .domain("test")
                .clientId("test")
                .clientSecret("test")
                .audience("test")
                .scopes("test,test2")
                .addScope("test3")
                .logoutRedirectUri("https://kinde.com")
                .openidEndpoint("https://kinde.com")
                .openidEndpoint("https://kinde.com")
                .authorizationEndpoint("https://kinde.com")
                .tokenEndpoint("https://kinde.com")
                .logoutEndpoint("https://kinde.com")
                .protocol("https")
                .build();
        assertTrue( kindeClient != null );
    }

}
