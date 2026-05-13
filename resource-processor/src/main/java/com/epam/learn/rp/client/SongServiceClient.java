package com.epam.learn.rp.client;

import com.epam.learn.rp.dto.MetadataDto;
import com.epam.learn.rp.exception.SongServiceClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SongServiceClient {

    private final RestClient gatewayClient;

    @Retryable(
        retryFor = {ResourceAccessException.class, SongServiceClientException.class},
        maxAttemptsExpression = "${app.retry.max-attempts}",
        backoff = @Backoff(
            delayExpression = "${app.retry.initial-interval}",
            multiplierExpression = "${app.retry.multiplier}",
            maxDelayExpression = "${app.retry.max-interval}"
        )
    )
    public void createSong(final MetadataDto metadataDto) {
        gatewayClient.post()
            .uri("/songs")
            .body(metadataDto)
            .retrieve()
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new SongServiceClientException(
                    new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8)
                );
            })
            .toBodilessEntity();
    }

}
