package com.epam.learn.ss.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class DeleteResourceValidator implements ConstraintValidator<DeleteResource, String> {

    private static final Pattern VALID_PATTERN = Pattern.compile("^\\d+$");

    private int maxLength;

    @Override
    public void initialize(DeleteResource constraintAnnotation) {
        this.maxLength = constraintAnnotation.maxLength();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        if (value.length() > maxLength) {
            return addConstraintViolation(context, String.format(
                "CSV string is too long: received %d characters, maximum allowed is %d", value.length(), maxLength));
        }

        Optional<String> firstInvalid = findFirstInvalid(value);
        if (firstInvalid.isPresent()) {
            return addConstraintViolation(context, String.format(
                "Invalid ID format: '%s'. Only positive integers are allowed", firstInvalid.get()));
        }

        return true;
    }

    private boolean addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }

    private Optional<String> findFirstInvalid(String value) {
        return Arrays.stream(value.split(","))
            .filter(digit -> !VALID_PATTERN.matcher(digit).matches())
            .findFirst();
    }

}
