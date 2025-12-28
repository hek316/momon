package com.momon.backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {
    // AIService implementations are loaded via @ConditionalOnProperty
    // - MockAIService: when ai.mock.enabled=true (default)
    // - RealAIService: when ai.mock.enabled=false (production)
}
