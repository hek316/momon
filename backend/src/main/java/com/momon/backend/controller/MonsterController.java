package com.momon.backend.controller;

import com.momon.backend.dto.MonsterResponse;
import com.momon.backend.service.MonsterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monsters")
@RequiredArgsConstructor
@Slf4j
public class MonsterController {

    private final MonsterService monsterService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MonsterResponse> createMonster(
        @RequestHeader("X-Device-ID") String deviceId,
        @RequestPart("image") MultipartFile image,
        @RequestPart("text") String emotionText
    ) {
        log.info("📥 POST /api/v1/monsters");
        log.info("   - Device ID: {}", deviceId);
        log.info("   - Image: {}", image.getOriginalFilename());
        log.info("   - Emotion text: {}", emotionText);

        // Validation
        if (deviceId == null || deviceId.isBlank()) {
            log.warn("   ❌ Missing X-Device-ID header");
            return ResponseEntity.badRequest().build();
        }

        if (image == null || image.isEmpty()) {
            log.warn("   ❌ Missing or empty image");
            return ResponseEntity.badRequest().build();
        }

        if (emotionText == null || emotionText.isBlank()) {
            log.warn("   ❌ Missing emotion text");
            return ResponseEntity.badRequest().build();
        }

        if (emotionText.length() > 100) {
            log.warn("   ❌ Emotion text too long: {} characters", emotionText.length());
            return ResponseEntity.badRequest().build();
        }

        try {
            MonsterResponse response = monsterService.createMonster(deviceId, image, emotionText);
            log.info("   ✅ Monster created: {}", response.id());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("   ❌ Failed to create monster", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MonsterResponse>> getMonsters(
        @RequestHeader("X-Device-ID") String deviceId
    ) {
        log.info("📥 GET /api/v1/monsters");
        log.info("   - Device ID: {}", deviceId);

        if (deviceId == null || deviceId.isBlank()) {
            log.warn("   ❌ Missing X-Device-ID header");
            return ResponseEntity.badRequest().build();
        }

        try {
            List<MonsterResponse> monsters = monsterService.getMonstersByDeviceId(deviceId);
            log.info("   ✅ Returning {} monsters", monsters.size());
            return ResponseEntity.ok(monsters);
        } catch (Exception e) {
            log.error("   ❌ Failed to fetch monsters", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonsterResponse> getMonster(@PathVariable Long id) {
        log.info("📥 GET /api/v1/monsters/{}", id);

        try {
            MonsterResponse monster = monsterService.getMonsterById(id);
            log.info("   ✅ Returning monster: {}", monster.name());
            return ResponseEntity.ok(monster);
        } catch (IllegalArgumentException e) {
            log.warn("   ❌ Monster not found: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("   ❌ Failed to fetch monster", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
