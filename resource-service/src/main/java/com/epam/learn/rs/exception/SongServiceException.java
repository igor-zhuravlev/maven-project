package com.epam.learn.rs.exception;

public class SongServiceException extends RuntimeException {

    public SongServiceException(String response) {
        super(response);
    }

}
