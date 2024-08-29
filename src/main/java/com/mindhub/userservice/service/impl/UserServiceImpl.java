package com.mindhub.userservice.service.impl;

import com.mindhub.userservice.models.UserEntity;
import com.mindhub.userservice.repository.UserRepository;
import com.mindhub.userservice.service.UserService;
import com.mindhub.userservice.utils.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public Mono<UserEntity> getUserById(Long id) {
        return userRepository.findById(id)
                .doOnSubscribe(subscription -> logger.info("Retrieving user with id: {}", id))
                .doOnSuccess(user -> {
                    if (user != null) {
                        logger.debug("Retrieved user: {}", user.getName());
                    } else {
                        logger.debug("No user found with id: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error retrieving user with id {}: {}", id, error.getMessage()));
    }

    @Override
    public Flux<UserEntity> getAllUsers() {
        return userRepository.findAll()
                .doOnSubscribe(subscription -> logger.info("Retrieving all users"))
                .doOnNext(user -> logger.debug("Retrieved user: {}", user.getName()))
                .doOnComplete(() -> logger.info("Completed retrieving all users"))
                .doOnError(error -> logger.error("Error retrieving users: {}", error.getMessage()));
    }

    @Override
    public Mono<UserEntity> createUser(UserEntity user) {
        return Mono.just(user)
                .doOnNext(UserValidator::validateUser)
                .flatMap(userRepository::save)
                .doOnSubscribe(subscription -> logger.info("Creating new user"))
                .doOnSuccess(savedUser -> logger.info("User created successfully: {}", savedUser.getName()))
                .doOnError(error -> logger.error("Error creating user: {}", error.getMessage()));
    }

    @Override
    public Mono<UserEntity> updateUser(Long id, UserEntity user) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setPassword(user.getPassword());
                    return Mono.just(existingUser)
                            .doOnNext(UserValidator::validateUser)
                            .flatMap(userRepository::save);
                })
                .doOnSubscribe(subscription -> logger.info("Updating user with id: {}", id))
                .doOnSuccess(updatedUser -> logger.info("User updated successfully: {}", updatedUser.getName()))
                .doOnError(error -> logger.error("Error updating user with id {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Boolean> deleteUser(Long id) {
        return userRepository.findById(id)
                .flatMap(user -> userRepository.delete(user).thenReturn(true))
                .switchIfEmpty(Mono.just(false))
                .doOnSubscribe(subscription -> logger.info("Deleting user with id: {}", id))
                .doOnSuccess(deleted -> {
                    if (deleted) {
                        logger.info("User deleted successfully with id: {}", id);
                    } else {
                        logger.info("User not found for deletion with id: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error deleting user with id {}: {}", id, error.getMessage()));
    }
}