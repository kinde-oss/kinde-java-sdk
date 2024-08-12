package com.kinde;

import com.kinde.token.KindeToken;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Ignore;

import java.util.List;


/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    @Ignore
    public void testApp() throws Exception {
        System.out.println("Test the kinde builder");
        KindeClient kindeClient = KindeClientBuilder
                .builder()
                    .domain("replace")
                    .clientId("replace")
                    .clientSecret("replace")
                .build();
        KindeClientSession kindeClientSession = kindeClient.clientSession();
        List<KindeToken> tokens = kindeClientSession.retrieveTokens();
        KindeTokenFactory kindeTokenFactory = kindeClient.tokenFactory();
        kindeTokenFactory.parse(tokens.get(0).token());
    }
}
