package com.example.cvmanager.cv.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record CvLinkRequest(
        Long id,

        @NotBlank(message = "Link label is required")
        @Size(max = 255, message = "Link label must be 255 characters or fewer")
        String label,

        @NotBlank(message = "Link URL is required")
        @Size(max = 1000, message = "Link URL must be 1000 characters or fewer")
        @URL(message = "Link URL must be valid")
        String url,

        int displayOrder
) {
}
