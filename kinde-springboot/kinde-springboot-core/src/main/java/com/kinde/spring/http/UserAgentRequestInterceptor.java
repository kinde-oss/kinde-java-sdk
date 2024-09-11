package com.kinde.spring.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.stream.Collectors;

public final class UserAgentRequestInterceptor implements ClientHttpRequestInterceptor {

    private final static String USER_AGENT_VALUE = "Kinde-Client";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add(HttpHeaders.USER_AGENT, USER_AGENT_VALUE);
        return execution.execute(request, body);
    }
}