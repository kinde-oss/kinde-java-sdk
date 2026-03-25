package com.kinde;

import com.kinde.authorization.AuthorizationUrl;
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

    @Ignore
    public void testInvitationCodeWithLogin() {
        System.out.println("Test invitation code with login");
        KindeClient kindeClient = KindeClientBuilder
                .builder()
                .build();
        KindeClientSession kindeClientSession = kindeClient.clientSession();

        AuthorizationUrl loginWithInvite = kindeClientSession.login("inv_example123");
        System.out.println("\nLogin with invitation code:");
        System.out.println("  URL: " + loginWithInvite.getUrl());

        AuthorizationUrl registerWithInvite = kindeClientSession.register("inv_example456");
        System.out.println("\nRegister with invitation code:");
        System.out.println("  URL: " + registerWithInvite.getUrl());

        AuthorizationUrl handleDirect = kindeClientSession.handleInvitation("inv_example789");
        System.out.println("\nHandle invitation directly:");
        System.out.println("  URL: " + handleDirect.getUrl());

        AuthorizationUrl normalLogin = kindeClientSession.login();
        System.out.println("\nNormal login (no invitation):");
        System.out.println("  URL: " + normalLogin.getUrl());
    }
}
