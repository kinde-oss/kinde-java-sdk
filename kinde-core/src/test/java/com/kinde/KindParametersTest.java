package com.kinde;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class KindParametersTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public KindParametersTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( KindParametersTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testKindParameterConstants() {
        String domain1 = KindParameters.DOMAIN.getValue();
        String domain2 = KindParameters.DOMAIN.getValue();
        assertTrue( domain1.equals(domain2) );
    }
}
