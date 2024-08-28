package com.mindhub.userservice.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundUser.class)
    public Mono<ResponseEntity<String>> handleNotFoundUser(NotFoundUser ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()));
    }

    @ExceptionHandler(InvalidUserExceptions.class)
    public Mono<ResponseEntity<String>> handleInvalidUserExceptions(InvalidUserExceptions ex) {

        return Mono.just(ResponseEntity.status( HttpStatus.BAD_REQUEST).body(ex.getMessage()));
    }
}