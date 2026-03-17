package com.kinde.accounts;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kinde.KindeClientSession;

/**
 * Regression test for the @Provides-based KindeClientSession binding in KindeAccountsClient.
 * Verifies that construction via the manual path does not trigger member injection
 * (which would call getAccessToken() and cause Guice MissingImplementation / infinite recursion).
 */
@ExtendWith(MockitoExtension.class)
class KindeAccountsClientGuiceBindingTest {

    @Mock
    private KindeClientSession mockSession;

    @Test
    void construction_doesNotCallGetAccessToken() {
        KindeAccountsClient client = new KindeAccountsClient(mockSession, true);

        assertNotNull(client);
        verify(mockSession, never()).getAccessToken();
    }

    @Test
    void constructedClient_canCallManagerMethod_withoutGuiceErrors() {
        when(mockSession.getAccessToken()).thenReturn("test-token");

        KindeAccountsClient client = new KindeAccountsClient(mockSession, true);

        assertDoesNotThrow(() -> {
            try {
                client.getEntitlements();
            } catch (RuntimeException e) {
                if (e.getCause() instanceof java.util.concurrent.ExecutionException) {
                    // API call failure is expected (no real server) — 
                    // the point is that no Guice/recursion error occurred
                    return;
                }
                throw e;
            }
        });
    }
}
