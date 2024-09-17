package com.kinde.spring;


import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.*;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;


public class WebClientUtilTest {


    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void testWebClientHeaders() {
        AtomicReference<HttpHeaders> headers = new AtomicReference<>();
        WebClient client = WebClientUtil.createWebClient().mutate()
                // mock out the response, we just want the headers
                .exchangeFunction((request) -> {
                        headers.set(request.headers());
                        return Mono.just(ClientResponse.create(HttpStatus.OK).build());
                    }
                ).build();

        client.get()
                .uri("http://foo.example.com/")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(headers.get().getFirst(HttpHeaders.USER_AGENT));
    }
}
