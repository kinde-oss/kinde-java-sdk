package com.kinde.servlet;

import com.kinde.AppTest;
import com.kinde.client.KindeJ2eeGuiceTestModule;
import com.kinde.token.KindeTokenGuiceTestModule;
import com.kinde.guice.KindeEnvironmentSingleton;
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
        KindeEnvironmentSingleton.init(KindeEnvironmentSingleton.State.TEST);
        KindeGuiceSingleton.init(
                new KindeJ2eeGuiceTestModule(),
                new KindeTokenGuiceTestModule());
        try {
            KindeSingleton singleton = KindeSingleton.getInstance();
            assertTrue( true );
        } finally {
            KindeGuiceSingleton.fin();
            KindeEnvironmentSingleton.fin();
        }
    }
}
