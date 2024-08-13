package com.kinde.servlet;

import com.kinde.AppTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class KindeSingletonTest extends TestCase  {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public KindeSingletonTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( KindeSingletonTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        KindeSingleton singleton = KindeSingleton.getInstance();
        assertTrue( true );
    }
}
