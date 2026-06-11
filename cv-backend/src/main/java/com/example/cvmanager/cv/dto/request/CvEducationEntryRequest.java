package com.example.cvmanager.cv.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CvEducationEntryRequest(
        Long id,

        @NotBlank(message = "Institution is required")
        @Size(max = 255, message = "Institution must be 255 characters or fewer")
        String institution,

        @Size(max = 255, message = "Degree must be 255 characters or fewer")
        String degree,

        @Size(max = 255, message = "Field of study must be 255 characters or fewer")
        String fieldOfStudy,

        LocalDate startDate,

        LocalDate endDate,

        @Size(max = 2000, message = "Education description must be 2000 characters or fewer")
        String description,

        int displayOrder
) {
}
