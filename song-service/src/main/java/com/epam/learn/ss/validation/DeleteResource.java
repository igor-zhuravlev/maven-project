package com.epam.learn.ss.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DeleteResourceValidator.class)
public @interface DeleteResource {
    String message() default "Invalid value";
    int maxLength() default 200;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
