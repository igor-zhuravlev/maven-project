package com.epam.learn.ss.dto;

import com.epam.learn.ss.entity.StorageType;

public record StorageResponseDto(
    Integer id,
    StorageType storageType,
    String bucket,
    String path
) {
}
