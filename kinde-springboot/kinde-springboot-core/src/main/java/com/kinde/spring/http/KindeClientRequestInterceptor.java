package com.kinde.spring.http;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public final class KindeClientRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final String KINDE_CLIENT_HEADER = "Kinde-SDK";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add(KINDE_CLIENT_HEADER, "Spring-Java/2.0.1");
        return execution.execute(request, body);
    }
}
