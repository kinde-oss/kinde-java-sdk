package com.kinde;

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
    public void testBuilder() {
        KindeClientBuilder kindeClientBuilder1 = KindeClientBuilder.builder();
        KindeClientBuilder kindeClientBuilder2 = KindeClientBuilder.builder();
        assertTrue( kindeClientBuilder1 != kindeClientBuilder2 );
        KindeClient kindeClient = kindeClientBuilder1.domain("test").clientId("test").clientSecret("test").build();
    }
}
