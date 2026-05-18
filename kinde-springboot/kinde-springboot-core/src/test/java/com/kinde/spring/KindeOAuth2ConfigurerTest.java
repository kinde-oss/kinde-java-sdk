package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * Spring Security 7 removed the chained {@code HttpSecurity#oauth2Login()/logout()/tokenEndpoint()...}
 * DSL in favour of {@link org.springframework.security.config.Customizer}-based lambdas. The pre-upgrade
 * version of this test mocked those removed methods to walk through {@link KindeOAuth2Configurer#init}.
 * <p>
 * With the upgrade we limit the unit test to the property-validation branches of {@code init} that
 * don't touch the (now lambda-only) DSL on a mocked {@link HttpSecurity}; full coverage of the DSL
 * branches is left to integration tests against a real {@code HttpSecurity}.
 */
public class KindeOAuth2ConfigurerTest {

    @Test
    public void initWithoutKindeOAuth2PropertiesIsNoop() {
        KindeOAuth2Configurer configurer = new KindeOAuth2Configurer();
        HttpSecurity httpSecurity = Mockito.mock(HttpSecurity.class);
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        when(httpSecurity.getSharedObject(ApplicationContext.class)).thenReturn(context);
        when(context.getBeansOfType(KindeOAuth2Properties.class)).thenReturn(Collections.emptyMap());

        configurer.init(httpSecurity);
    }

    @Test
    public void initWithoutKindeRegistrationIsNoop() {
        KindeOAuth2Configurer configurer = new KindeOAuth2Configurer();
        HttpSecurity httpSecurity = Mockito.mock(HttpSecurity.class);
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        when(httpSecurity.getSharedObject(ApplicationContext.class)).thenReturn(context);

        KindeOAuth2Properties kindeOAuth2Properties = Mockito.mock(KindeOAuth2Properties.class);
        Map<String, KindeOAuth2Properties> kindePropsMap = new HashMap<>();
        kindePropsMap.put(KindeOAuth2Properties.class.getName(), kindeOAuth2Properties);
        when(context.getBeansOfType(KindeOAuth2Properties.class)).thenReturn(kindePropsMap);

        // No OAuth2ClientProperties beans available -> short-circuits before touching the DSL.
        when(context.getBeansOfType(OAuth2ClientProperties.class)).thenReturn(Collections.emptyMap());

        configurer.init(httpSecurity);
    }
}
