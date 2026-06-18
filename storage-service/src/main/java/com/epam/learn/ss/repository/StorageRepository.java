package com.epam.learn.ss.repository;

import com.epam.learn.ss.entity.Storage;
import com.epam.learn.ss.entity.StorageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Integer> {
    boolean existsByStorageType(StorageType storageType);
    boolean existsByBucketAndPath(String bucket, String path);
}
