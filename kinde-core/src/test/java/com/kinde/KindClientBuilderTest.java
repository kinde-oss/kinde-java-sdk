package com.kinde;

import com.kinde.client.KindeClientGuiceTestModule;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class KindClientBuilderTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public KindClientBuilderTest( String testName )
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
        return new TestSuite( KindClientBuilderTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testBuilderBasicTest() {
        KindeClientBuilder kindeClientBuilder1 = KindeClientBuilder.builder();
        KindeClientBuilder kindeClientBuilder2 = KindeClientBuilder.builder();
        assertTrue( kindeClientBuilder1 != kindeClientBuilder2 );
        KindeClient kindeClient = kindeClientBuilder1.domain("test").clientId("test").clientSecret("test").build();
        assertTrue( kindeClient != null );
    }
}
