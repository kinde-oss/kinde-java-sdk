package com.kinde.spring;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.stream.Collectors;

final class WebClientUtil {

    private static final String USER_AGENT = "KINDE";

    private WebClientUtil() {}

    static WebClient createWebClient() {
       return WebClient.builder()
            .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
            .build();
    }
}
