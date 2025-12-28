package com.momon.backend.service;

import com.momon.backend.dto.MonsterCharacteristics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Random;

@Service
@ConditionalOnProperty(name = "ai.mock.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class MockAIService implements AIService {

    private static final List<MonsterCharacteristics> MOCK_MONSTERS = List.of(
        new MonsterCharacteristics(
            "스파클 슬라임",
            "반짝이는 젤리 같은 몸을 가진 귀여운 몬스터입니다. 빛을 받으면 무지개빛으로 반짝이며, 포근한 성격을 가지고 있어요.",
            "A cute jelly-like monster with sparkling iridescent body, kawaii style, pastel colors"
        ),
        new MonsterCharacteristics(
            "플러피 구름이",
            "하얀 구름처럼 폭신폭신한 털을 가진 몬스터입니다. 항상 미소를 띠고 있으며, 만지면 솜사탕처럼 부드러워요.",
            "A fluffy cloud-like monster with soft white fur, always smiling, dreamy atmosphere"
        ),
        new MonsterCharacteristics(
            "번개 토끼",
            "전기를 품은 빠른 토끼 몬스터입니다. 귀여운 외모와는 달리 번개처럼 빠르게 움직일 수 있어요.",
            "A lightning-fast rabbit monster with electric powers, cute but energetic, electric aura"
        ),
        new MonsterCharacteristics(
            "캔디 드래곤",
            "사탕으로 만들어진 작은 드래곤 몬스터입니다. 달콤한 향기를 뿜으며, 알록달록한 비늘이 특징이에요.",
            "A small candy dragon monster with colorful scales, sweet and adorable, sugar-coated appearance"
        ),
        new MonsterCharacteristics(
            "문라이트 폭스",
            "달빛을 받으면 빛나는 신비로운 여우 몬스터입니다. 밤하늘의 별처럼 반짝이는 털을 가지고 있어요.",
            "A mystical fox monster that glows under moonlight, starry fur pattern, magical atmosphere"
        )
    );

    private final Random random = new Random();

    @Override
    public MonsterCharacteristics analyzeImage(MultipartFile image, String emotionText) {
        log.info("🎭 Mock AI: Analyzing image and emotion text (no cost)");
        log.info("   - Image: {} ({} bytes)", image.getOriginalFilename(), image.getSize());
        log.info("   - Emotion: {}", emotionText);

        // Return random mock monster
        MonsterCharacteristics characteristics = MOCK_MONSTERS.get(random.nextInt(MOCK_MONSTERS.size()));
        log.info("   - Generated: {}", characteristics.getName());

        return characteristics;
    }

    @Override
    public String generateMonsterImage(String prompt) {
        log.info("🎭 Mock AI: Returning sample image URL (no cost)");
        log.info("   - Prompt: {}", prompt);

        // Return placeholder image URL (using placeholder service)
        // In production, replace with actual S3-uploaded sample images
        String sampleImageUrl = "https://placehold.co/1024x1024/FFE5E5/FF6B9D?text=" +
            java.net.URLEncoder.encode("몬스터 샘플", java.nio.charset.StandardCharsets.UTF_8);

        log.info("   - Image URL: {}", sampleImageUrl);

        return sampleImageUrl;
    }
}
