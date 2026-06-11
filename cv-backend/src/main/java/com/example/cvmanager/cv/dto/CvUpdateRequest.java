package com.example.cvmanager.cv.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CvUpdateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be 255 characters or less")
        String title,

        @NotBlank(message = "CV summary is required")
        @Size(max = 500, message = "CV summary must be 500 characters or less")
        String summary
) {}

