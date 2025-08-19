package com.kinde.accounts;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KindeAccountsClientConstructorTest {

    @Test
    void constructor_nullKindeClient_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                     () -> new KindeAccountsClient((KindeClient) null));
    }

    @Test
    void constructor_nullKindeClientSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                     () -> new KindeAccountsClient((KindeClientSession) null));
    }
}
