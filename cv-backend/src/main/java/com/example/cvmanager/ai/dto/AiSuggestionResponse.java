package com.example.cvmanager.ai.dto;

import com.example.cvmanager.ai.model.AiActionType;
import java.time.LocalDateTime;

public record AiSuggestionResponse(
        Long id,
        Long cvId,
        AiActionType actionType,
        String targetKey,
        String originalText,
        String suggestedText,
        String status,
        LocalDateTime createdAt) {
}
