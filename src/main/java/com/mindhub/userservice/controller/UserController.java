package com.mindhub.userservice.controller;

import com.mindhub.userservice.handlers.ErrorResponse;
import com.mindhub.userservice.models.UserEntity;
import com.mindhub.userservice.service.UserService;
import com.mindhub.userservice.handlers.NotFoundUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "The User API")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get a user by ID", description = "Returns a single user")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    public Mono<UserEntity> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .switchIfEmpty(Mono.error(new NotFoundUser(id)));
    }

    @Operation(summary = "Get all users", description = "Returns a list of all users")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    public Flux<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Create a new user", description = "Creates a new user and returns the created user")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserEntity> createUser(@RequestBody UserEntity user) {
        return userService.createUser(user);
    }

    @Operation(summary = "Update a user", description = "Updates an existing user and returns the updated user")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PutMapping("/{id}")
    public Mono<UserEntity> updateUser(@PathVariable Long id, @RequestBody UserEntity user) {
        return userService.updateUser(id, user)
                .switchIfEmpty(Mono.error(new NotFoundUser(id)));
    }

    @Operation(summary = "Delete a user", description = "Deletes a user")
    @ApiResponse(responseCode = "204", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ErrorResponse>> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id)
                .map(deleted -> {
                    if (deleted) {
                        return ResponseEntity.noContent().<ErrorResponse>build();
                    } else {
                        ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND,
                                "User with ID " + id + " not found",
                                LocalDateTime.now()
                        );
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    }
                });
    }
}