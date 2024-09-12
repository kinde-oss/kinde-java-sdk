package com.kinde;

import com.google.inject.Injector;
import com.kinde.authorization.AuthorizationType;
import com.kinde.client.KindeClientGuiceModule;
import com.kinde.config.KindeParameters;
import com.kinde.guice.KindeGuiceSingleton;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

import java.security.InvalidParameterException;
import java.util.*;

@Slf4j
public class KindeClientBuilder {

    private Map<String, Object> parameters = new HashMap<>();
    private Injector injector;

    /**
     * Private constructor to prevent new instantiation
     */
    private KindeClientBuilder() {
        try {
            Dotenv dotenv = Dotenv.load();
            setParameterFromEnvironmental(KindeParameters.DOMAIN, dotenv);
            setParameterFromEnvironmental(KindeParameters.REDIRECT_URI, dotenv);
            setParameterFromEnvironmental(KindeParameters.LOGOUT_REDIRECT_URI, dotenv);
            setParameterFromEnvironmental(KindeParameters.OPENID_ENDPOINT, dotenv);
            setParameterFromEnvironmental(KindeParameters.AUTHORIZATION_ENDPOINT, dotenv);
            setParameterFromEnvironmental(KindeParameters.TOKEN_ENDPOINT, dotenv);
            setParameterFromEnvironmental(KindeParameters.LOGOUT_ENDPOINT, dotenv);
            setParameterFromEnvironmental(KindeParameters.CLIENT_ID, dotenv);
            setParameterFromEnvironmental(KindeParameters.CLIENT_SECRET, dotenv);
            setParameterFromEnvironmental(KindeParameters.GRANT_TYPE, dotenv);
            setParameterFromEnvironmental(KindeParameters.SCOPES, dotenv);
            setParameterFromEnvironmental(KindeParameters.PROTOCOL, dotenv);
            setParameterFromEnvironmental(KindeParameters.AUDIENCE, dotenv);
            setParameterFromEnvironmental(KindeParameters.LANG, dotenv);
            setParameterFromEnvironmental(KindeParameters.ORG_CODE, dotenv);
            setParameterFromEnvironmental(KindeParameters.HAS_SUCCESS_PAGE, dotenv);

        } catch (Exception ex) {
            // ignore exceptions as this means that the DotEnv environment was not configured
        }
        injector = KindeGuiceSingleton.getInstance().getInjector().createChildInjector(new KindeClientGuiceModule(this.parameters));
    }

    /**
     * The builder
     *
     * @return
     */
    public static KindeClientBuilder builder() {
        return new KindeClientBuilder();
    }


    public KindeClientBuilder domain(String domain) {
        if (domain != null) {
            this.parameters.put(KindeParameters.DOMAIN.getValue(), domain);
        }
        return this;
    }

    public KindeClientBuilder redirectUri(String redirectUri) {
        if (redirectUri != null) {
            this.parameters.put(KindeParameters.REDIRECT_URI.getValue(), redirectUri);
        }
        return this;
    }

    public KindeClientBuilder logoutRedirectUri(String logoutRedirectUri) {
        if (logoutRedirectUri != null) {
            this.parameters.put(KindeParameters.LOGOUT_REDIRECT_URI.getValue(), logoutRedirectUri);
        }
        return this;
    }

    public KindeClientBuilder openidEndpoint(String openidEndpoint) {
        if (openidEndpoint != null) {
            this.parameters.put(KindeParameters.OPENID_ENDPOINT.getValue(), openidEndpoint);
        }
        return this;
    }

    public KindeClientBuilder authorizationEndpoint(String authorizationEndpoint) {
        if (authorizationEndpoint != null) {
            this.parameters.put(KindeParameters.AUTHORIZATION_ENDPOINT.getValue(), authorizationEndpoint);
        }
        return this;
    }

    public KindeClientBuilder tokenEndpoint(String tokenEndpoint) {
        if (tokenEndpoint != null) {
            this.parameters.put(KindeParameters.TOKEN_ENDPOINT.getValue(), tokenEndpoint);
        }
        return this;
    }

    public KindeClientBuilder logoutEndpoint(String logoutEndpoint) {
        if (logoutEndpoint != null) {
            this.parameters.put(KindeParameters.LOGOUT_ENDPOINT.getValue(), logoutEndpoint);
        }
        return this;
    }

    public KindeClientBuilder clientId(String clientId) {
        if (clientId != null) {
            this.parameters.put(KindeParameters.CLIENT_ID.getValue(), clientId);
        }
        return this;
    }

    public KindeClientBuilder clientSecret(String clientSecret) {
        if (clientSecret != null) {
            this.parameters.put(KindeParameters.CLIENT_SECRET.getValue(), clientSecret);
        }
        return this;
    }

    public KindeClientBuilder grantType(AuthorizationType authorizationType) {
        if (authorizationType != null) {
            this.parameters.put(KindeParameters.GRANT_TYPE.getValue(), authorizationType);
        }
        return this;
    }

    public KindeClientBuilder scopes(String scope) {
        if (scope != null) {
            this.parameters.put(KindeParameters.SCOPES.getValue(), new ArrayList(Arrays.asList(scope.split(","))));
        }
        return this;
    }

    public KindeClientBuilder addScope(String scope) {
        if (scope != null) {
            List.class.cast(this.parameters.computeIfAbsent(KindeParameters.SCOPES.getValue(), k -> new ArrayList<String>()))
                    .add(scope);
        }
        return this;
    }

    public KindeClientBuilder protocol(String protocol) {
        if (protocol != null) {
            this.parameters.put(KindeParameters.PROTOCOL.getValue(), protocol);
        }
        return this;
    }

    public KindeClientBuilder audience(String audiences) {
        if (audiences != null) {
            this.parameters.put(KindeParameters.AUDIENCE.getValue(), Arrays.asList(audiences.split(",")));
        }
        return this;
    }

    public KindeClientBuilder addAudience(String audience) {
        List.class.cast(this.parameters.computeIfAbsent(KindeParameters.AUDIENCE.getValue(), k -> new ArrayList<String>()))
                .add(audience);
        return this;
    }

    public KindeClientBuilder lang(String lang) {
        if (lang != null) {
            this.parameters.put(KindeParameters.LANG.getValue(), lang);
        }
        return this;
    }

    public KindeClientBuilder orgCode(String orgCode) {
        if (orgCode != null) {
            this.parameters.put(KindeParameters.ORG_CODE.getValue(), orgCode);
        }
        return this;
    }

    public KindeClientBuilder hasSuccessPage(Boolean success) {
        if (success != null) {
            this.parameters.put(KindeParameters.HAS_SUCCESS_PAGE.getValue(), success);
        }
        return this;
    }


    public KindeClient build() {
        Set keys = this.parameters.keySet();
        if (!keys.containsAll(
                Arrays.asList(KindeParameters.CLIENT_ID.getValue(),
                        KindeParameters.CLIENT_SECRET.getValue(),
                        KindeParameters.DOMAIN.getValue()))) {
            throw new InvalidParameterException("The required parameters were not set");
        }
        // create a child injector for the scope of this client
        return injector.getInstance(KindeClient.class);
    }

    private void setParameterFromEnvironmental(KindeParameters parameters,Dotenv dotenv) {
        log.debug("Before setting the parameters: {}",dotenv.get(parameters.getValue()));
        if (dotenv.get(parameters.getValue()) != null) {
            log.info("Set the parameter: {}",parameters.getValue());
            this.parameters.put(parameters.getValue(),parameters.getMapper().apply(dotenv.get(parameters.getValue())));
        }
    }
}
