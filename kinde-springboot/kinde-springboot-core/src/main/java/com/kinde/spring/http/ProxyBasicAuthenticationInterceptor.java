package com.kinde.spring.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Adds an HTTP {@code Proxy-Authorization: Basic <base64(user:pass)>} header to the outgoing
 * request. Unlike {@link org.springframework.http.client.support.BasicAuthenticationInterceptor},
 * which targets origin-server auth via the {@code Authorization} header, this interceptor is
 * intended for authenticating against an upstream HTTP proxy and therefore uses
 * {@link HttpHeaders#PROXY_AUTHORIZATION} (RFC 7235 §4.4). Routing proxy credentials through the
 * origin-server {@code Authorization} header is wrong both functionally (the proxy cannot read it)
 * and from a confidentiality standpoint (the credentials would leak to the origin server).
 */
public final class ProxyBasicAuthenticationInterceptor implements ClientHttpRequestInterceptor {

    private final String authorization;

    public ProxyBasicAuthenticationInterceptor(String username, String password) {
        this.authorization = "Basic " + Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().set(HttpHeaders.PROXY_AUTHORIZATION, authorization);
        return execution.execute(request, body);
    }
}
