package com.mindhub.userservice.controller;

import com.mindhub.userservice.models.UserEntity;
import com.mindhub.userservice.service.UserService;
import com.mindhub.userservice.handlers.NotFoundUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Mono<UserEntity> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .switchIfEmpty(Mono.error(new NotFoundUser(id)));
    }

    @GetMapping
    public Flux<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserEntity> createUser(@RequestBody UserEntity user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public Mono<UserEntity> updateUser(@PathVariable Long id, @RequestBody UserEntity user) {
        return userService.updateUser(id, user)
                .switchIfEmpty(Mono.error(new NotFoundUser(id)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id)
                .switchIfEmpty(Mono.error(new NotFoundUser(id)));
    }
}