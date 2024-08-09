package com.kinde.token;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RefreshTokenTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RefreshTokenTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( RefreshTokenTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testAccessToken() {
        KindeToken kindeToken1 = RefreshToken.init("test",false);
        assertTrue( kindeToken1.token().equals("test") );
        assertTrue( kindeToken1.valid() == false );

        KindeToken kindeToken2 = RefreshToken.init("test2",true);
        assertTrue( kindeToken2.token().equals("test2") );
        assertTrue( kindeToken2.valid() );
    }
}
