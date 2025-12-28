package com.momon.backend.service;

import com.momon.backend.dto.MonsterCharacteristics;
import org.springframework.web.multipart.MultipartFile;

public interface AIService {
    MonsterCharacteristics analyzeImage(MultipartFile image, String emotionText);
    String generateMonsterImage(String prompt);
}
