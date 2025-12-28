package com.momon.backend.service;

import com.momon.backend.dto.MonsterCharacteristics;
import com.momon.backend.dto.MonsterResponse;
import com.momon.backend.entity.Monster;
import com.momon.backend.entity.User;
import com.momon.backend.repository.MonsterRepository;
import com.momon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonsterService {

    private final AIService aiService;
    private final S3Service s3Service;
    private final MonsterRepository monsterRepository;
    private final UserRepository userRepository;

    @Transactional
    public MonsterResponse createMonster(String deviceId, MultipartFile image, String emotionText) {
        log.info("🎨 Creating monster for device: {}", deviceId);
        log.info("   - Image: {} ({} bytes)", image.getOriginalFilename(), image.getSize());
        log.info("   - Emotion text: {}", emotionText);

        // 1. Ensure user exists (upsert pattern)
        User user = userRepository.findById(deviceId)
            .orElseGet(() -> createNewUser(deviceId));
        user.setLastSeenAt(LocalDateTime.now());
        userRepository.save(user);

        // 2. Analyze image and emotion with AI
        MonsterCharacteristics characteristics = aiService.analyzeImage(image, emotionText);
        log.info("   - Generated characteristics: {}", characteristics.getName());

        // 3. Generate monster image
        String aiImageUrl = aiService.generateMonsterImage(characteristics.getImagePrompt());
        log.info("   - AI image URL: {}", aiImageUrl);

        // 4. Upload to S3 for permanent storage
        String s3ImageUrl = s3Service.uploadMonsterImage(aiImageUrl);
        log.info("   - S3 image URL: {}", s3ImageUrl);

        // 5. Save to database
        Monster monster = Monster.builder()
            .user(user)
            .name(characteristics.getName())
            .description(characteristics.getDescription())
            .imageUrl(s3ImageUrl)
            .prompt(emotionText)
            .build();

        Monster saved = monsterRepository.save(monster);
        log.info("✅ Monster created successfully: {} (ID: {})", saved.getName(), saved.getId());

        return MonsterResponse.from(saved);
    }

    private User createNewUser(String deviceId) {
        log.info("👤 Creating new user: {}", deviceId);
        User user = User.builder()
            .deviceId(deviceId)
            .firstSeenAt(LocalDateTime.now())
            .lastSeenAt(LocalDateTime.now())
            .build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<MonsterResponse> getMonstersByDeviceId(String deviceId) {
        log.info("📋 Fetching monsters for device: {}", deviceId);
        List<Monster> monsters = monsterRepository.findByUserDeviceIdOrderByCreatedAtDesc(deviceId);
        log.info("   - Found {} monsters", monsters.size());
        return monsters.stream()
            .map(MonsterResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public MonsterResponse getMonsterById(Long id) {
        log.info("🔍 Fetching monster by ID: {}", id);
        Monster monster = monsterRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("   - Monster not found: {}", id);
                return new IllegalArgumentException("Monster not found: " + id);
            });
        log.info("   - Found: {}", monster.getName());
        return MonsterResponse.from(monster);
    }
}
