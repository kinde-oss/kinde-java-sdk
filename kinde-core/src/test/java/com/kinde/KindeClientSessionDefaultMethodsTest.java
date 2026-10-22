package com.kinde;

import com.kinde.authorization.AuthorizationUrl;
import com.kinde.entitlements.KindeEntitlements;
import com.kinde.token.KindeTokens;
import com.kinde.user.UserInfo;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KindeClientSessionDefaultMethodsTest {

    @Test
    void optionalParametersDelegateToBasicOperations() {
        StubSession session = new StubSession();

        session.login("invite", "");
        session.createOrg("Example", "invite", " ");
        session.register("invite", "");

        assertEquals(1, session.logins);
        assertEquals(1, session.organizations);
        assertEquals(1, session.registrations);
    }

    @Test
    void rejectsUnsupportedConnectionIds() {
        StubSession session = new StubSession();

        assertThrows(UnsupportedOperationException.class, () -> session.login(null, "connection"));
        assertThrows(UnsupportedOperationException.class, () -> session.createOrg("Example", null, "connection"));
        assertThrows(UnsupportedOperationException.class, () -> session.register(null, "connection"));
        assertEquals(0, session.logins);
        assertEquals(0, session.organizations);
        assertEquals(0, session.registrations);
    }

    @Test
    void unsupportedOptionalOperationsFailExplicitly() {
        StubSession session = new StubSession();

        assertThrows(UnsupportedOperationException.class, () -> session.handleInvitation("invite"));
        assertThrows(UnsupportedOperationException.class, session::entitlements);
        assertNull(session.getDomain());
        assertNull(session.getAccessToken());
    }

    private static final class StubSession implements KindeClientSession {
        private int logins;
        private int organizations;
        private int registrations;

        @Override
        public KindeTokens retrieveTokens() {
            return null;
        }

        @Override
        public AuthorizationUrl authorizationUrl() {
            return null;
        }

        @Override
        public AuthorizationUrl authorizationUrlWithParameters(Map<String, String> parameters) {
            return null;
        }

        @Override
        public AuthorizationUrl login() {
            logins++;
            return null;
        }

        @Override
        public AuthorizationUrl createOrg(String orgName) {
            organizations++;
            return null;
        }

        @Override
        public AuthorizationUrl register() {
            registrations++;
            return null;
        }

        @Override
        public AuthorizationUrl logout() {
            return null;
        }

        @Override
        public AuthorizationUrl generatePortalUrl(String domain, String returnUrl, String subNav) {
            return null;
        }

        @Override
        public UserInfo retrieveUserInfo() {
            return null;
        }
    }
}
