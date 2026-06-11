package com.example.cvmanager.cv.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CvSkillRequest(
        Long id,

        @NotBlank(message = "Skill name is required")
        @Size(max = 255, message = "Skill name must be 255 characters or fewer")
        String name,

        @Size(max = 255, message = "Skill category must be 255 characters or fewer")
        String category,

        @Size(max = 255, message = "Skill proficiency must be 255 characters or fewer")
        String proficiency,

        int displayOrder
) {
}
