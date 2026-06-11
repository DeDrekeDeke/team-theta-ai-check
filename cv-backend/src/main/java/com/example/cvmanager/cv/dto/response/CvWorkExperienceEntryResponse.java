package com.example.cvmanager.cv.dto.response;

import java.time.LocalDate;

public record CvWorkExperienceEntryResponse(
        Long id,
        String employer,
        String jobTitle,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        int displayOrder
) {
}
