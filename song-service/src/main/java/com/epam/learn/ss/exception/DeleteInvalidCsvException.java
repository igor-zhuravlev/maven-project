package com.epam.learn.ss.exception;

import com.epam.learn.ss.dto.DeleteSongRequestDto;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Set;

@Getter
public class DeleteInvalidCsvException extends RuntimeException {

    private final Set<ConstraintViolation<DeleteSongRequestDto>> violations;

    public DeleteInvalidCsvException(Set<ConstraintViolation<DeleteSongRequestDto>> violations) {
        this.violations = violations;
    }

}
