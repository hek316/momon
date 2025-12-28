package com.momon.backend.dto;

import com.momon.backend.entity.Monster;

import java.time.LocalDateTime;

public record MonsterResponse(
    Long id,
    String imageUrl,
    String name,
    String description,
    LocalDateTime createdAt
) {
    public static MonsterResponse from(Monster monster) {
        return new MonsterResponse(
            monster.getId(),
            monster.getImageUrl(),
            monster.getName(),
            monster.getDescription(),
            monster.getCreatedAt()
        );
    }
}
