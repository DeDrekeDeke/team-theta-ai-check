package com.example.cvmanager.cv.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CvLanguageRequest(
        Long id,

        @NotBlank(message = "Language name is required")
        @Size(max = 255, message = "Language name must be 255 characters or fewer")
        String name,

        @Size(max = 255, message = "Language proficiency must be 255 characters or fewer")
        String proficiency,

        int displayOrder
) {
}
