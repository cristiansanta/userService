package com.mindhub.userservice.service;

import com.mindhub.userservice.models.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserEntity> getUserById(Long id);
    Flux<UserEntity> getAllUsers();
    Mono<UserEntity> createUser(UserEntity user);
    Mono<UserEntity> updateUser(Long id, UserEntity user);
    Mono<Void> deleteUser(Long id);
}