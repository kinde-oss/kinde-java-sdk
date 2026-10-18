package com.kinde;

import com.kinde.admin.KindeAdminSessionImpl;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;

import static org.junit.jupiter.api.Assertions.*;

class KindeAdminSessionBuilderTest {

    @Test
    void buildsSessionWithoutClientAndCreatesDefaultApiClient() {
        KindeAdminSession session = KindeAdminSessionBuilder.builder().build();

        assertInstanceOf(KindeAdminSessionImpl.class, session);
        assertNotNull(session.initClient());
    }
}
