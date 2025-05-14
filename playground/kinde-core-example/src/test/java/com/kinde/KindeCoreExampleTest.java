package com.kinde;

import com.kinde.token.KindeTokens;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Ignore;

public class KindeCoreExampleTest extends TestCase {

    public KindeCoreExampleTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(KindeCoreExampleTest.class);
    }

    @Ignore
    public void testApp() {
        System.out.println("Test the kinde builder");
        KindeClient kindeClient = KindeClientBuilder
                .builder()
                .build();
        KindeClientSession kindeClientSession = kindeClient.clientSession();
        System.out.println(kindeClientSession.authorizationUrl());
        KindeTokens kindeTokens = kindeClientSession.retrieveTokens();
        KindeTokenFactory kindeTokenFactory = kindeClient.tokenFactory();
        kindeTokenFactory.parse(kindeTokens.getAccessToken().token());

        assertNotNull(kindeClient);
        assertNotNull(kindeClientSession);
        assertNotNull(kindeTokens);
        assertNotNull(kindeTokens.getAccessToken());
        assertNotNull(kindeTokenFactory);
        assertNotNull(kindeTokenFactory.parse(kindeTokens.getAccessToken().token()));
    }
}
