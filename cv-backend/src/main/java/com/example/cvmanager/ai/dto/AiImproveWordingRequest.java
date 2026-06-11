package com.example.cvmanager.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiImproveWordingRequest(
        @NotBlank(message = "Section is required")
        @Size(max = 64, message = "Section must be 64 characters or fewer")
        String section,

        @NotBlank(message = "Target key is required")
        @Size(max = 255, message = "Target key must be 255 characters or fewer")
        String targetKey,

        @NotBlank(message = "Text is required")
        @Size(max = 2000, message = "Text must be 2000 characters or fewer")
        String text
) {}
