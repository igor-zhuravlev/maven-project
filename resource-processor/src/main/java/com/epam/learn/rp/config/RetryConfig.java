package com.epam.learn.rp.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableConfigurationProperties(RetryProps.class)
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate(RetryProps props) {
        return RetryTemplate.builder()
            .maxAttempts(props.maxAttempts())
            .exponentialBackoff(props.initialInterval(), props.multiplier(), props.maxInterval())
            .build();
    }

}
