package com.momon.backend.service;

import com.momon.backend.entity.User;
import com.momon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User getOrCreateUser(String deviceId) {
        // deviceId is the primary key, so use findById
        return userRepository.findById(deviceId)
                .map(user -> {
                    // Update lastSeenAt for existing user
                    user.setLastSeenAt(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .orElseGet(() -> {
                    log.info("Creating new user with deviceId: {}", deviceId);
                    User newUser = User.builder()
                            .deviceId(deviceId)
                            .lastSeenAt(LocalDateTime.now())
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
