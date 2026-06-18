package com.epam.learn.rs.dto;

import com.epam.learn.rs.entity.StorageType;

public record StorageDto(Integer id, StorageType storageType, String bucket, String path) {
}
