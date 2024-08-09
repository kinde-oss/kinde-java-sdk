package com.kinde;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class KindeAdminSessionBuilderTest
        extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public KindeAdminSessionBuilderTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( KindeAdminSessionBuilderTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        KindeAdminSession kindeAdminSession1 = KindeAdminSessionBuilder.builder().build();
        KindeAdminSession kindeAdminSession2 = KindeAdminSessionBuilder.builder().build();
        assertTrue( kindeAdminSession1 != kindeAdminSession2 );
    }
}
