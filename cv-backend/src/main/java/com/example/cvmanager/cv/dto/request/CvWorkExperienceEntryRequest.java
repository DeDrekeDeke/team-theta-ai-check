package com.example.cvmanager.cv.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CvWorkExperienceEntryRequest(
        Long id,

        @NotBlank(message = "Employer is required")
        @Size(max = 255, message = "Employer must be 255 characters or fewer")
        String employer,

        @NotBlank(message = "Job title is required")
        @Size(max = 255, message = "Job title must be 255 characters or fewer")
        String jobTitle,

        @Size(max = 255, message = "Location must be 255 characters or fewer")
        String location,

        LocalDate startDate,

        LocalDate endDate,

        @Size(max = 2000, message = "Work experience description must be 2000 characters or fewer")
        String description,

        int displayOrder
) {
}
