package com.mindhub.userservice.service.impl;

import com.mindhub.userservice.models.UserEntity;
import com.mindhub.userservice.repository.UserRepository;
import com.mindhub.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Mono<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Flux<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Mono<UserEntity> createUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<UserEntity> updateUser(Long id, UserEntity user) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setPassword(user.getPassword());
                    return userRepository.save(existingUser);
                });
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }
}