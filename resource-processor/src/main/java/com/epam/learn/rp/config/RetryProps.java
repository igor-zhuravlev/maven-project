package com.epam.learn.rp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.retry")
public record RetryProps(
    int maxAttempts,
    long initialInterval,
    double multiplier,
    long maxInterval
) {
}
