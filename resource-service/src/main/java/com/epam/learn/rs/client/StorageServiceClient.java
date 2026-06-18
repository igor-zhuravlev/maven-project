package com.epam.learn.rs.client;

import com.epam.learn.rs.dto.StorageDto;
import com.epam.learn.rs.entity.StorageType;
import com.epam.learn.rs.exception.StorageNotFoundException;
import com.epam.learn.rs.exception.StorageServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StorageServiceClient {

    private static final List<StorageDto> FALLBACK_STORAGES = List.of(
        new StorageDto(1, StorageType.STAGING, "resources-staging", "/files"),
        new StorageDto(2, StorageType.PERMANENT, "resources-permanent", "/files")
    );

    private final RestClient storageClient;

    @CircuitBreaker(
        name = "storageService",
        fallbackMethod = "getStorageFallback"
    )
    public StorageDto getStorage(final StorageType storageType) {
        List<StorageDto> storages = storageClient.get()
            .uri("/storages")
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new StorageServiceException(
                    new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8)
                );
            })
            .body(new ParameterizedTypeReference<List<StorageDto>>() {});

        if (storages == null) {
            throw new StorageServiceException("StorageService returned empty response");
        }

        return storages.stream()
            .filter(storage -> storage.storageType() == storageType)
            .findFirst()
            .orElseThrow(() ->
                new StorageNotFoundException("Storage not found: " + storageType)
            );
    }

    private StorageDto getStorageFallback(final StorageType storageType, final Throwable throwable) {
        log.info(
            "Storage Service is unavailable or returned an error. Using fallback storage config :: storageType: {}, reason: {}",
            storageType,
            throwable.toString()
        );
        return FALLBACK_STORAGES.stream()
            .filter(storage -> storage.storageType() == storageType)
            .findFirst()
            .orElseThrow(() ->
                new StorageNotFoundException("Fallback storage not found: " + storageType)
            );
    }

}
