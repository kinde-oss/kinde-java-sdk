package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.mockito.Mockito.when;

public class KindeJwtAuthenticationConverterTest {

    @Test
    public void testKindeJwtAuthenticationConverterFirstConstructor() throws Exception {
        KindeJwtAuthenticationConverter kindeJwtAuthenticationConverter = new KindeJwtAuthenticationConverter("test");
    }

    @Test
    public void testKindeJwtAuthenticationConverterSecondConstructor() throws Exception {
        KindeOAuth2Properties httpSecurity = Mockito.mock(KindeOAuth2Properties.class);

        when(httpSecurity.getPermissionsClaim()).thenReturn("test");
        KindeJwtAuthenticationConverter kindeJwtAuthenticationConverter = new KindeJwtAuthenticationConverter(httpSecurity);
    }
}
