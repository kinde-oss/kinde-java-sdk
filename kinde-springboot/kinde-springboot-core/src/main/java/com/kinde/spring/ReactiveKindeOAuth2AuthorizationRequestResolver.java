package com.kinde.spring;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers.withPkce;

/**
 * Reactive (WebFlux) counterpart of {@link KindeOAuth2AuthorizationRequestResolver}: adds
 * Kinde-specific authorize URL parameters on top of the default PKCE-enabled resolver.
 *
 * <p>{@code invitation_code} (with {@code is_invitation=true}) and {@code connection_id} are
 * forwarded from the originating request. For step-up authentication (RFC 9470),
 * {@code acr_values} ({@code mfa} or {@code phr}) and {@code max_age} (seconds) are validated
 * and forwarded, so {@code /oauth2/authorization/kinde?acr_values=mfa&max_age=300} asks an
 * already signed-in user for a stronger or more recent authentication. Invalid step-up values
 * are rejected with HTTP 400: Kinde would silently ignore an unknown {@code acr_values} and
 * read a malformed {@code max_age} as "re-authenticate".
 */
public class ReactiveKindeOAuth2AuthorizationRequestResolver implements ServerOAuth2AuthorizationRequestResolver {

    private final DefaultServerOAuth2AuthorizationRequestResolver defaultResolver;

    public ReactiveKindeOAuth2AuthorizationRequestResolver(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
        this.defaultResolver.setAuthorizationRequestCustomizer(withPkce());
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange) {
        return defaultResolver.resolve(exchange)
                .map(authorizationRequest -> addKindeParameters(exchange, authorizationRequest));
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange, String clientRegistrationId) {
        return defaultResolver.resolve(exchange, clientRegistrationId)
                .map(authorizationRequest -> addKindeParameters(exchange, authorizationRequest));
    }

    private static OAuth2AuthorizationRequest addKindeParameters(
            ServerWebExchange exchange, OAuth2AuthorizationRequest authorizationRequest) {
        try {
            return KindeOAuth2AuthorizationRequestResolver.withKindeParameters(
                    authorizationRequest, name -> exchange.getRequest().getQueryParams().getFirst(name));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
