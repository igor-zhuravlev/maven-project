package com.epam.learn.rp.service.impl;

import com.epam.learn.rp.dto.MetadataDto;
import com.epam.learn.rp.event.ResourceUploadedEvent;
import com.epam.learn.rp.mapper.ResourceMetadataMapper;
import com.epam.learn.rp.service.ResourceMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ResourceMetadataServiceImpl implements ResourceMetadataService {

    private final RetryTemplate retryTemplate;
    private final RestClient gatewayClient;
    private final ResourceMetadataMapper resourceMetadataMapper;

    @Override
    public void handle(final ResourceUploadedEvent event) {
        final byte[] audio = retryTemplate.execute(context -> gatewayClient.get()
            .uri("/resource-service/resources/{id}", event.resourceId())
            .accept(MediaType.valueOf("audio/mpeg"))
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new RuntimeException(new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
            })
            .body(byte[].class));

        final MetadataDto metadataDto = resourceMetadataMapper.mapToMetadataDto(event.resourceId(), audio);

        retryTemplate.execute(context -> gatewayClient.post()
            .uri("/song-service/songs")
            .body(metadataDto)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new RuntimeException(new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
            })
            .toBodilessEntity());
    }

}
