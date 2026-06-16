package com.epam.learn.ss.handler;

import com.epam.learn.ss.dto.ErrorDto;
import com.epam.learn.ss.exception.StorageAlreadyExistsException;
import com.epam.learn.ss.exception.StorageLocationAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StorageAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleStorageAlreadyExistsException(StorageAlreadyExistsException e) {
        final String message = "Storage `%s` already exists".formatted(e.getStorageType());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorDto(message, "409"));
    }

    @ExceptionHandler(StorageLocationAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleStorageLocationAlreadyExistsException(StorageLocationAlreadyExistsException e) {
        final String message = "Storage with bucket `%s` and path `%s` already exists"
            .formatted(e.getBucket(), e.getPath());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorDto(message, "409"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDto("Validation errors in the request body", "400"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDto("Validation errors in the request", "400"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleInternalError(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorDto("An error occurred on the server", "500"));
    }

}
