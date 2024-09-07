package com.mindhub.userservice.service.impl;

import com.mindhub.userservice.dto.TaskDTO;
import com.mindhub.userservice.dto.UserDTO;
import com.mindhub.userservice.handlers.NotFoundUser;
import com.mindhub.userservice.models.UserEntity;
import com.mindhub.userservice.repository.UserRepository;
import com.mindhub.userservice.service.UserService;
import com.mindhub.userservice.utils.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private WebClient webClient = WebClient.builder().baseUrl("http://localhost:8082/api/tasks").build();


    @Override
    public Mono<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).flatMap(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            return getTasksFromUser(user.getEmail())
                    .collectList()
                    .flatMap(taskDTOs -> {
                        userDTO.setTasks(taskDTOs);
                        return Mono.just(userDTO);
                    });
        });
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
    public Mono<Void> deleteUser(Long id) {
        return userRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return userRepository.deleteById(id);
                    } else {
                        return Mono.error(new NotFoundUser(id));
                    }
                })
                .doOnSubscribe(subscription -> logger.info("Deleting user with id: {}", id))
                .doOnSuccess(v -> logger.info("User deleted successfully with id: {}", id))
                .doOnError(error -> logger.error("Error deleting user with id {}: {}", id, error.getMessage()));
    }
    public Flux<TaskDTO> getTasksFromUser(String email) {
        return webClient.get()
                .uri("/user/" + email)
                .retrieve()
                .bodyToFlux(TaskDTO.class);
    }
}