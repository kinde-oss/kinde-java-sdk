package com.kinde.config;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class KindeParametersTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public KindeParametersTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( KindeParametersTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testKindParameterConstants() {
        String domain1 = KindeParameters.DOMAIN.getValue();
        String domain2 = KindeParameters.DOMAIN.getValue();
        assertTrue( domain1.equals(domain2) );
    }
}
