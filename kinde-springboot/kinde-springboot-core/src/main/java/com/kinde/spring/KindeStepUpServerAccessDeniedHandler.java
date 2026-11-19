package com.kinde.spring;

import com.kinde.stepup.StepUp;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.web.access.server.BearerTokenServerAccessDeniedHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive (WebFlux) counterpart of {@link KindeStepUpAccessDeniedHandler}: answers an
 * {@link InsufficientUserAuthenticationException} with the step-up challenge from RFC 9470,
 * HTTP 401 and
 * {@code WWW-Authenticate: Bearer error="insufficient_user_authentication", acr_values="...", max_age="..."}.
 * Every other access denial goes to the delegate, by default Spring's
 * {@link BearerTokenServerAccessDeniedHandler}.
 */
public class KindeStepUpServerAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final ServerAccessDeniedHandler delegate;

    public KindeStepUpServerAccessDeniedHandler() {
        this(new BearerTokenServerAccessDeniedHandler());
    }

    public KindeStepUpServerAccessDeniedHandler(ServerAccessDeniedHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        InsufficientUserAuthenticationException stepUp = find(denied);
        if (stepUp == null) {
            return delegate.handle(exchange, denied);
        }
        return Mono.defer(() -> {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().set(HttpHeaders.WWW_AUTHENTICATE,
                    StepUp.insufficientUserAuthenticationChallenge(stepUp.getRequiredAcr(), stepUp.getRequiredMaxAge()));
            return exchange.getResponse().setComplete();
        });
    }

    private static InsufficientUserAuthenticationException find(Throwable throwable) {
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            if (t instanceof InsufficientUserAuthenticationException) {
                return (InsufficientUserAuthenticationException) t;
            }
            if (t.getCause() == t) {
                break;
            }
        }
        return null;
    }
}
