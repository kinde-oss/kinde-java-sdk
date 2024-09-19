package com.kinde.spring.env;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.client.OidcMetaData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.*;
import java.util.stream.Collectors;

public class KindeOAuth2PropertiesMappingEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final DeferredLog log = new DeferredLog();



    public static final String KINDE_OAUTH_PREFIX = "kinde.oauth2.";
    public static final String KINDE_OAUTH_DOMAIN = KINDE_OAUTH_PREFIX + "domain";
    public static final String KINDE_OAUTH_CLIENT_ID = KINDE_OAUTH_PREFIX + "client-id";
    public static final String KINDE_OAUTH_CLIENT_SECRET = KINDE_OAUTH_PREFIX + "client-secret";
    public static final String KINDE_OAUTH_SCOPES = KINDE_OAUTH_PREFIX + "scopes"; // array vs string*/

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // initialize the KindeClient
        KindeClientBuilder kindeClientBuilder = KindeClientBuilder.builder();
        String domain = environment.getProperty(KINDE_OAUTH_DOMAIN);
        if (domain != null) {
            kindeClientBuilder.domain(domain);
            kindeClientBuilder.clientId(environment.getProperty(KINDE_OAUTH_CLIENT_ID));
            kindeClientBuilder.clientSecret(environment.getProperty(KINDE_OAUTH_CLIENT_SECRET));
            kindeClientBuilder.scopes(environment.getProperty(KINDE_OAUTH_SCOPES));
        }
        KindeClient kindeClient = kindeClientBuilder.build();

        environment.getPropertySources().addLast(remappedKindeToStandardOAuthPropertySource(environment));
        environment.getPropertySources().addLast(remappedKindeOAuth2ScopesPropertySource(environment));
        // default scopes, as of Spring Security 5.4 default scopes are no longer added, this restores that functionality
        environment.getPropertySources().addLast(defaultKindeScopesSource(environment, kindeClient));
        // okta's endpoints can be resolved from an issuer
        environment.getPropertySources().addLast(kindeStaticDiscoveryPropertySource(environment, kindeClient));
        // Auth0 does not have an introspection endpoint
        environment.getPropertySources().addLast(kindeOpaqueTokenPropertySource(environment, kindeClient));
        environment.getPropertySources().addLast(kindeRedirectUriPropertySource(environment));
        environment.getPropertySources().addLast(kindeForcePkcePropertySource(environment, kindeClient));

        if (application != null) {
            // This is required as EnvironmentPostProcessors are run before logging system is initialized
            application.addInitializers(ctx -> log.replayTo(KindeOAuth2PropertiesMappingEnvironmentPostProcessor.class));
        }
    }

    private PropertySource<?> kindeForcePkcePropertySource(ConfigurableEnvironment environment, KindeClient kindeClient) {
        Map<String, Object> props = new HashMap<>();
        // need to re-evaluate this mapping
        props.put("spring.security.oauth2.client.registration.kinde.client-authentication-method", "none");

        return new ConditionalMapPropertySource("kinde-pkce-for-public-clients", props, environment, KINDE_OAUTH_DOMAIN, KINDE_OAUTH_CLIENT_ID) {
            @Override
            public boolean containsProperty(String name) {
                return super.containsProperty(name)
                    && !environment.containsProperty("spring.security.oauth2.client.registration.kinde.client-secret");
            }
        };
    }

    private PropertySource defaultKindeScopesSource(Environment environment, KindeClient kindeClient) {
        Map<String, Object> props = new HashMap<>();
        props.put("spring.security.oauth2.client.registration.kinde.scope",kindeClient.kindeConfig().scopes().stream().collect(Collectors.joining(",")));
        return new ConditionalMapPropertySource("default-scopes", props, environment, KINDE_OAUTH_DOMAIN, KINDE_OAUTH_CLIENT_ID);
    }

    private PropertySource remappedKindeToStandardOAuthPropertySource(Environment environment) {
        Map<String, String> aliasMap = new HashMap<>();

        aliasMap.put("spring.security.oauth2.client.registration.kinde.client-id", KINDE_OAUTH_CLIENT_ID);
        aliasMap.put("spring.security.oauth2.client.registration.kinde.client-secret", KINDE_OAUTH_CLIENT_SECRET);

        return new RemappedPropertySource("kinde-to-oauth2", aliasMap, environment);
    }

    private PropertySource remappedKindeOAuth2ScopesPropertySource(Environment environment) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.security.oauth2.client.registration.kinde.scope", "${" + KINDE_OAUTH_SCOPES + "}");
        return new KindeScopesPropertySource("okta-scope-remaper", properties, environment);
    }

    private PropertySource kindeRedirectUriPropertySource(Environment environment) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.security.oauth2.client.registration.kinde.redirect-uri", "{baseUrl}${kinde.oauth2.redirect-uri}");
        return new ConditionalMapPropertySource("kinde-redirect-uri-helper", properties, environment, "kinde.oauth2.redirect-uri");
    }

    private PropertySource kindeStaticDiscoveryPropertySource(Environment environment, KindeClient kindeClient) {
        OidcMetaData oidcMetaData = kindeClient.oidcMetaData();

        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.security.oauth2.resourceserver.jwt.issuer-uri", "${" + KINDE_OAUTH_DOMAIN + "}");
        properties.put("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", oidcMetaData.getJwkUrl().toString());
        properties.put("spring.security.oauth2.client.provider.kinde.authorization-uri", oidcMetaData.getOpMetadata().getAuthorizationEndpointURI().toString());
        properties.put("spring.security.oauth2.client.provider.kinde.token-uri", oidcMetaData.getOpMetadata().getTokenEndpointURI().toString());
        properties.put("spring.security.oauth2.client.provider.kinde.user-info-uri", oidcMetaData.getOpMetadata().getUserInfoEndpointURI().toString());
        properties.put("spring.security.oauth2.client.provider.kinde.jwk-set-uri", oidcMetaData.getJwkUrl().toString());
        properties.put("spring.security.oauth2.client.provider.kinde.issuer-uri", "${" + KINDE_OAUTH_DOMAIN + "}"); // required for OIDC logout

        return new ConditionalMapPropertySource("kinde-static-discovery", properties, environment, KINDE_OAUTH_DOMAIN);
    }

    private PropertySource kindeOpaqueTokenPropertySource(Environment environment, KindeClient kindeClient) {

        OidcMetaData oidcMetaData = kindeClient.oidcMetaData();
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.security.oauth2.resourceserver.opaque-token.client-id", "${" + KINDE_OAUTH_CLIENT_ID + "}");
        properties.put("spring.security.oauth2.resourceserver.opaque-token.client-secret", "${" + KINDE_OAUTH_CLIENT_SECRET + "}");
        properties.put("spring.security.oauth2.resourceserver.opaque-token.introspection-uri", oidcMetaData.getOpMetadata().getIntrospectionEndpointURI().toString());

        return new ConditionalMapPropertySource("kinde-opaque-token", properties, environment, KINDE_OAUTH_DOMAIN, KINDE_OAUTH_CLIENT_SECRET);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 1;
    }

    private static class ConditionalMapPropertySource extends MapPropertySource {

        private final Environment environment;
        private final List<String> conditionalProperties;

        private ConditionalMapPropertySource(String name, Map<String, Object> source, Environment environment, String... conditionalProperties) {
            super(name, source);
            this.environment = environment;
            this.conditionalProperties = Arrays.asList(conditionalProperties);
        }

        @Override
        public Object getProperty(String name) {

            return containsProperty(name)
                ? super.getProperty(name)
                : null;
        }

        @Override
        public boolean containsProperty(String name) {
            return super.containsProperty(name)
                && conditionalProperties.stream().allMatch(environment::containsProperty);
        }
    }

    private static class KindeScopesPropertySource extends MapPropertySource {

        private final Environment environment;

        private KindeScopesPropertySource(String name, Map<String, Object> source, Environment environment) {
            super(name, source);
            this.environment = environment;
        }

        @Override
        public Object getProperty(String name) {

            if (containsProperty(name)) {
                return Binder.get(environment).bind(KINDE_OAUTH_SCOPES, Bindable.setOf(String.class)).orElse(null);
            }
            return null;
        }
    }
}
