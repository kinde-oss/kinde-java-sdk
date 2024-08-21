package com.kinde;

import com.kinde.client.KindeClientGuiceTestModule;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        KindeClient kindeClient = kindeClientBuilder1.domain("test").clientId("test").clientSecret("test").build();
        assertTrue( kindeClient != null );
    }
}
