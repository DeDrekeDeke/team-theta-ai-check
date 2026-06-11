package com.example.cvmanager.cv.dto.response;

public record CvLanguageResponse(
        Long id,
        String name,
        String proficiency,
        int displayOrder
) {
}
