package com.example.cvmanager.cv.dto.response;

import java.time.LocalDate;

public record CvEducationEntryResponse(
        Long id,
        String institution,
        String degree,
        String fieldOfStudy,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        int displayOrder
) {
}
