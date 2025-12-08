package com.momon.backend.service;

import com.momon.backend.entity.User;
import com.momon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User getOrCreateUser(String deviceId) {
        return userRepository.findByDeviceId(deviceId)
                .orElseGet(() -> {
                    log.info("Creating new user with deviceId: {}", deviceId);
                    User newUser = new User(deviceId);
                    return userRepository.save(newUser);
                });
    }
}
