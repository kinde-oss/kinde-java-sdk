package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class KindeOAuth2ConfigurerTest {

    @Test
    public void testInit() throws Exception {
        KindeOAuth2Configurer kindeOAuth2Configurer = new KindeOAuth2Configurer();
        HttpSecurity httpSecurity = Mockito.mock(HttpSecurity.class);
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        when(httpSecurity.getSharedObject(ApplicationContext.class)).thenReturn(context);
        kindeOAuth2Configurer.init(httpSecurity);
        KindeOAuth2Properties kindeOAuth2Properties = Mockito.mock(KindeOAuth2Properties.class);
        Map<String, KindeOAuth2Properties> kindeOAuth2PropertiesMap = new HashMap<>();
        kindeOAuth2PropertiesMap.put(KindeOAuth2Properties.class.getName(),kindeOAuth2Properties);
        when(context.getBeansOfType(KindeOAuth2Properties.class)).thenReturn(kindeOAuth2PropertiesMap);
        when(context.getBean(KindeOAuth2Properties.class)).thenReturn(kindeOAuth2Properties);
        kindeOAuth2Configurer.init(httpSecurity);

    }
}
