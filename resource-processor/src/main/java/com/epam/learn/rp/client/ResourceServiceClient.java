package com.epam.learn.rp.client;

import com.epam.learn.rp.exception.ResourceServiceClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ResourceServiceClient {

    private final RestClient gatewayClient;

    @Retryable(
        retryFor = {ResourceAccessException.class, ResourceServiceClientException.class},
        maxAttemptsExpression = "${app.retry.max-attempts}",
        backoff = @Backoff(
            delayExpression = "${app.retry.initial-interval}",
            multiplierExpression = "${app.retry.multiplier}",
            maxDelayExpression = "${app.retry.max-interval}"
        )
    )
    public byte[] downloadResource(final Integer id) {
        return gatewayClient.get()
            .uri("/resource-service/resources/{id}", id)
            .accept(MediaType.valueOf("audio/mpeg"))
            .retrieve()
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new ResourceServiceClientException(
                    new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8)
                );
            })
            .body(byte[].class);
    }

}
