package com.kinde.principal;

import com.kinde.user.UserInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class KindePrincipalTest {

    @Test
    void exposesNameRolesAndUserInfo() {
        UserInfo userInfo = mock(UserInfo.class);
        KindePrincipal principal = new KindePrincipal("user@example.com", List.of("admin", "read"), userInfo);

        assertEquals("user@example.com", principal.getName());
        assertEquals(List.of("admin", "read"), principal.roles());
        assertTrue(principal.hasRole("admin"));
        assertFalse(principal.hasRole("write"));
        assertSame(userInfo, principal.getUserInfo());
    }
}
