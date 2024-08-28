package com.mindhub.userservice.handlers;

public class NotFoundUser extends RuntimeException {
    public NotFoundUser(Long id) {
        super("User with ID " + id + " not found");
    }
}