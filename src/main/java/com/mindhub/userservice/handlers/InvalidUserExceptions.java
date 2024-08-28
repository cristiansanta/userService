package com.mindhub.userservice.handlers;

public class InvalidUserExceptions extends RuntimeException {
    public InvalidUserExceptions(String message) {
        super(message);
    }
}