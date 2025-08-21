package com.kinde.servlet;

import com.kinde.AppTest;
import com.kinde.client.KindeClientGuiceTestModule;
import com.kinde.token.KindeTokenGuiceTestModule;
import com.kinde.guice.KindeGuiceSingleton;
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
        // Initialize Guice with test modules
        KindeGuiceSingleton.init(
                new KindeClientGuiceTestModule(),
                new KindeTokenGuiceTestModule());
        
        KindeSingleton singleton = KindeSingleton.getInstance();
        assertTrue( true );
    }
}
