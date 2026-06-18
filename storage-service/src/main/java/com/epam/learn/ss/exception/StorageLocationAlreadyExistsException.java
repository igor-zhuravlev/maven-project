package com.epam.learn.ss.exception;

import lombok.Getter;

@Getter
public class StorageLocationAlreadyExistsException extends RuntimeException {

    private final String bucket;
    private final String path;

    public StorageLocationAlreadyExistsException(String bucket, String path) {
        this.bucket = bucket;
        this.path = path;
    }

}
