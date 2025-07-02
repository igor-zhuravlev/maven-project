package com.epam.learn.ss.exception;

import lombok.Getter;

@Getter
public class SongAlreadyExistsException extends RuntimeException {

    private final Integer id;

    public SongAlreadyExistsException(Integer id) {
        this.id = id;
    }

}
