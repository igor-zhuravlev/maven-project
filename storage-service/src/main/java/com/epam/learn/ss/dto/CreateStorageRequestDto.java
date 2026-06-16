package com.epam.learn.ss.dto;

import com.epam.learn.ss.entity.StorageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStorageRequestDto(
    @NotNull(message = "Storage type is required")
    StorageType storageType,

    @NotBlank(message = "Bucket is required")
    String bucket,

    @NotBlank(message = "Path is required")
    String path
) {
}
