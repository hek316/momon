package com.momon.backend.controller;

import com.momon.backend.entity.User;
import com.momon.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/monsters")
@RequiredArgsConstructor
public class MonsterController {

    private final UserService userService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testDeviceId(
            @RequestHeader("X-Device-ID") String deviceId
    ) {
        log.info("Received request with Device ID: {}", deviceId);

        User user = userService.getOrCreateUser(deviceId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("deviceId", deviceId);
        response.put("userId", user.getId());
        response.put("createdAt", user.getCreatedAt());

        return ResponseEntity.ok(response);
    }
}
