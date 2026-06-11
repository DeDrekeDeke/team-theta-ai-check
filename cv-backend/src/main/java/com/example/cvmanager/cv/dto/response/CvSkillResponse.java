package com.example.cvmanager.cv.dto.response;

public record CvSkillResponse(
        Long id,
        String name,
        String category,
        String proficiency,
        int displayOrder
) {
}
