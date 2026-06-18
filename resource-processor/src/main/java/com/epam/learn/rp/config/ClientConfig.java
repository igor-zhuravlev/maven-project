package com.epam.learn.rp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Value("${app.services.resource-service-url}")
    private String resourceServerUrl;

    @Value("${app.services.song-service-url}")
    private String songServerUrl;

    @LoadBalanced
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient resourceClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
            .baseUrl(resourceServerUrl)
            .build();
    }

    @Bean
    public RestClient songClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
            .baseUrl(songServerUrl)
            .build();
    }

}
