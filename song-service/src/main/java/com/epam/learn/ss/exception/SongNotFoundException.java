package com.epam.learn.ss.exception;

import lombok.Getter;

@Getter
public class SongNotFoundException extends RuntimeException {

    private final Integer id;

    public SongNotFoundException(Integer id) {
        this.id = id;
    }

}
