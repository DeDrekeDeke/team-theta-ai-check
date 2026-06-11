package com.example.cvmanager.cv.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CvResponse(
        Long id,
        Long ownerUserId,
        String ownerEmail,
        String title,
        String summary,
        CvPersonalDetailsResponse personalDetails,
        List<CvEducationEntryResponse> educationEntries,
        List<CvWorkExperienceEntryResponse> workExperienceEntries,
        List<CvSkillResponse> skills,
        List<CvLanguageResponse> languages,
        List<CvLinkResponse> links,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime archivedAt) {
}
