package com.example.cvmanager.cv.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CvUpdateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be 255 characters or less")
        String title,

        @NotBlank(message = "CV summary is required")
        @Size(max = 500, message = "CV summary must be 500 characters or less")
        String summary,

        @Valid
        CvPersonalDetailsRequest personalDetails,

        @Valid
        List<CvEducationEntryRequest> educationEntries,

        @Valid
        List<CvWorkExperienceEntryRequest> workExperienceEntries,

        @Valid
        List<CvSkillRequest> skills,

        @Valid
        List<CvLanguageRequest> languages,

        @Valid
        List<CvLinkRequest> links
) {}

