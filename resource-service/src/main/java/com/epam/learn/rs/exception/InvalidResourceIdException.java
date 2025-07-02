package com.epam.learn.rs.exception;

import lombok.Getter;

@Getter
public class InvalidResourceIdException extends RuntimeException {

    private final Object value;

    public InvalidResourceIdException(Object value) {
        this.value = value;
    }

}
