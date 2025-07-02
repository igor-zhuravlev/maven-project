package com.epam.learn.rs.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final Integer id;

    public ResourceNotFoundException(Integer id) {
        this.id = id;
    }

}
