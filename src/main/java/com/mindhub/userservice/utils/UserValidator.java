package com.mindhub.userservice.utils;

import com.mindhub.userservice.handlers.InvalidUserExceptions;
import com.mindhub.userservice.models.UserEntity;

public class UserValidator {
    public static void validateUser(UserEntity user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new InvalidUserExceptions("User name cannot be empty");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new InvalidUserExceptions("Invalid email format");
        }
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new InvalidUserExceptions("Password must be at least 8 characters long");
        }
    }
}