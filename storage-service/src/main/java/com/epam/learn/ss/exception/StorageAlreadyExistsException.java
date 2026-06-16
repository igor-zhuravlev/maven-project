package com.epam.learn.ss.exception;

import lombok.Getter;

@Getter
public class StorageAlreadyExistsException extends RuntimeException {

    private final String storageType;

    public StorageAlreadyExistsException(String storageType) {
        this.storageType = storageType;
    }

}
