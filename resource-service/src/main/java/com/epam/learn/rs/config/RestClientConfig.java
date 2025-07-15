package com.epam.learn.rs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${GATEWAY_SERVER_URL:http://localhost:8080}")
    private String gatewayServerUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
            .baseUrl(gatewayServerUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

}
