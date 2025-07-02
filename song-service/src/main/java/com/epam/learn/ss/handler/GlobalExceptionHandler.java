package com.epam.learn.ss.handler;

import com.epam.learn.ss.dto.ErrorDto;
import com.epam.learn.ss.exception.DeleteInvalidCsvException;
import com.epam.learn.ss.exception.SongAlreadyExistsException;
import com.epam.learn.ss.exception.SongNotFoundException;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        final Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        final Map<String, Object> response = new HashMap<>();
        response.put("errorMessage", "Validation error");
        response.put("details", fieldErrors);
        response.put("errorCode", "400");
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    @ExceptionHandler(SongNotFoundException.class)
    public ResponseEntity<ErrorDto> handleSongNotFound(SongNotFoundException ex) {
        final String message = String.format("Song metadata for ID=%d not found", ex.getId());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorDto(message, "404"));
    }

    @ExceptionHandler(SongAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleSongAlreadyExists(SongAlreadyExistsException ex) {
        final String message = String.format("Metadata for resource ID=%d already exists", ex.getId());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorDto(message, "409"));
    }

    @ExceptionHandler(DeleteInvalidCsvException.class)
    public ResponseEntity<ErrorDto> handleDeleteInvalidCsv(DeleteInvalidCsvException ex) {
        final String message = ex.getViolations().stream()
            .findFirst()
            .map(ConstraintViolation::getMessage)
            .orElse("Unknown");
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDto(message, "400"));
    }

}
