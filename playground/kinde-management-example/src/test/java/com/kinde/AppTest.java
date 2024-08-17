package com.kinde;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Ignore;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.ApplicationsApi;



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
                .build();
        KindeAdminSession kindeAdminSession = KindeAdminSessionBuilder.builder().client(kindeClient).build();
        ApiClient apiClient = kindeAdminSession.initClient();
        ApplicationsApi applicationsApi = new ApplicationsApi(apiClient);
        applicationsApi.getApplications(null,null,null);
    }
}
