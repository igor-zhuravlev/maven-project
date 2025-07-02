package com.epam.learn.rs.handler;

import com.epam.learn.rs.dto.ErrorDto;
import com.epam.learn.rs.exception.InvalidResourceIdException;
import com.epam.learn.rs.exception.ResourceNotFoundException;
import com.epam.learn.rs.exception.SongServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFound(ResourceNotFoundException ex) {
        final String message = String.format("Resource with ID=%d not found", ex.getId());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorDto(message, "404"));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorDto> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        final String invalidContentType = ex.getContentType() != null
            ? ex.getContentType().toString()
            : "unknown";
        final String message = String.format("Invalid file format: '%s'. Only MP3 files are allowed", invalidContentType);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDto(message, "400"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        final String value = ex.getValue() != null ? ex.getValue().toString() : "null";
        final String message = String.format("Invalid value '%s' for ID. Must be a positive integer", value);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDto(message, "400"));
    }

    @ExceptionHandler(InvalidResourceIdException.class)
    public ResponseEntity<ErrorDto> handleInvalidResourceId(InvalidResourceIdException ex) {
        final String message = String.format("Invalid value '%s' for ID. Must be a positive integer", ex.getValue());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDto(message, "400"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().getFirst();
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDto(fieldError.getDefaultMessage(), "400"));
    }

    @ExceptionHandler(SongServiceException.class)
    public ResponseEntity<ErrorDto> handleSongService(SongServiceException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDto("Song service error", "400"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleInternalError(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorDto("An internal error occurred", "500"));
    }

}
