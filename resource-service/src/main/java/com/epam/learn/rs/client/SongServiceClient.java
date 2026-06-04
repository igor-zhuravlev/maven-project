package com.epam.learn.rs.client;

import com.epam.learn.rs.exception.SongServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SongServiceClient {

    private final RestClient songClient;

    public void deleteSongsByIds(final String ids) {
        songClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path("/songs")
                .queryParam("id", ids)
                .build()
            )
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new SongServiceException(
                    new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8)
                );
            })
            .toBodilessEntity();
    }

}
