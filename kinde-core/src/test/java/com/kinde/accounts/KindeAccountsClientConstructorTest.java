package com.kinde.accounts;

import com.kinde.KindeClientSession;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KindeAccountsClientConstructorTest {

    @Test
    void constructor_nullKindeClientSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                     () -> new KindeAccountsClient((KindeClientSession) null, true));
    }
}
