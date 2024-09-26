package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
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

        OAuth2LoginConfigurer oAuth2LoginConfigurer = Mockito.mock(OAuth2LoginConfigurer.class);
        when(httpSecurity.oauth2Login()).thenReturn(oAuth2LoginConfigurer);
        OAuth2LoginConfigurer.TokenEndpointConfig tokenEndpointConfig = Mockito.mock(OAuth2LoginConfigurer.TokenEndpointConfig.class);
        when(oAuth2LoginConfigurer.tokenEndpoint()).thenReturn(tokenEndpointConfig);
        when(tokenEndpointConfig.accessTokenResponseClient(any())).thenReturn(tokenEndpointConfig);
        when(httpSecurity.getSharedObject(ApplicationContext.class)).thenReturn(context);
        Map<String, KindeOAuth2Properties> kindeOAuth2PropertiesMap = new HashMap<>();
        kindeOAuth2PropertiesMap.put(KindeOAuth2Properties.class.getName(),kindeOAuth2Properties);
        when(context.getBeansOfType(KindeOAuth2Properties.class)).thenReturn(kindeOAuth2PropertiesMap);
        when(context.getBeansOfType(KindeOAuth2Properties.class)).thenReturn(kindeOAuth2PropertiesMap);
        when(context.getBean(KindeOAuth2Properties.class)).thenReturn(kindeOAuth2Properties);
        OAuth2ClientProperties oAuth2ClientProperties = Mockito.mock(OAuth2ClientProperties.class);
        when(context.getBean(OAuth2ClientProperties.class)).thenReturn(oAuth2ClientProperties);
        Map<String, OAuth2ClientProperties> oauth2ClientMap = new HashMap<>();
        oauth2ClientMap.put("kinde",oAuth2ClientProperties);

        when(context.getBeansOfType(OAuth2ClientProperties.class)).thenReturn(oauth2ClientMap);
        Map<String, OAuth2ClientProperties.Provider> providers = new HashMap<>();
        OAuth2ClientProperties.Provider propertiesProvider = Mockito.mock(OAuth2ClientProperties.Provider.class);
        providers.put("kinde",propertiesProvider);
        when(oAuth2ClientProperties.getProvider()).thenReturn(providers);
        OAuth2ClientProperties.Registration propertiesRegistration = Mockito.mock(OAuth2ClientProperties.Registration.class);
        Map<String, OAuth2ClientProperties.Registration> registration = new HashMap<>();
        registration.put("kinde",propertiesRegistration);
        when(oAuth2ClientProperties.getRegistration()).thenReturn(registration);
        when(propertiesProvider.getIssuerUri()).thenReturn("http://kinde.com");
        when(propertiesRegistration.getClientId()).thenReturn("test");

        Environment environment = Mockito.mock(Environment.class);
        when(context.getEnvironment()).thenReturn(environment);

        kindeOAuth2Configurer.init(httpSecurity);

        OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler = Mockito.mock(OidcClientInitiatedLogoutSuccessHandler.class);
        Map<String, OidcClientInitiatedLogoutSuccessHandler> oidcClientInitiatedLogoutSuccessHandlerHashMap = new HashMap<>();
        oidcClientInitiatedLogoutSuccessHandlerHashMap.put("kinde",oidcClientInitiatedLogoutSuccessHandler);
        when(context.getBeansOfType(OidcClientInitiatedLogoutSuccessHandler.class)).thenReturn(oidcClientInitiatedLogoutSuccessHandlerHashMap);
        LogoutConfigurer logoutConfigurer = Mockito.mock(LogoutConfigurer.class);
        when(httpSecurity.logout()).thenReturn(logoutConfigurer);
        when(logoutConfigurer.logoutSuccessHandler(any())).thenReturn(logoutConfigurer);
        kindeOAuth2Configurer.init(httpSecurity);



    }
}
