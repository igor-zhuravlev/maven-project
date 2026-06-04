package com.epam.learn.rs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Value("${app.services.song-service-url}")
    private String songServerUrl;

    @LoadBalanced
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient songClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
            .baseUrl(songServerUrl)
            .build();
    }

}
