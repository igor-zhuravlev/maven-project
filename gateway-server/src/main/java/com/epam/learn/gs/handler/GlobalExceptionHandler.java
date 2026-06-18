package com.epam.learn.gs.handler;

import com.epam.learn.gs.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<ResponseEntity<ErrorDto>> handleNoResourceFoundException(NoResourceFoundException e) {
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorDto("Bad Gateway", "502"))
        );
    }

    @ExceptionHandler({ServiceUnavailableException.class, NotFoundException.class})
    public Mono<ResponseEntity<ErrorDto>> handleServiceUnavailableException(Exception e) {
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorDto("Service Unavailable", "503"))
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorDto>> handleInternalServerErrorException(Exception e) {
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Internal Server Error", "500"))
        );
    }

}
