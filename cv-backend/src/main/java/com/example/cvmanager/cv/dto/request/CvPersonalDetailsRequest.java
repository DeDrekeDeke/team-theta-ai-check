package com.example.cvmanager.cv.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record CvPersonalDetailsRequest(
        @Size(max = 255, message = "Full name must be 255 characters or fewer")
        String fullName,

        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must be 255 characters or fewer")
        String email,

        @Size(max = 255, message = "Phone must be 255 characters or fewer")
        String phone,

        @Size(max = 255, message = "Location must be 255 characters or fewer")
        String location,

        @Size(max = 255, message = "Headline must be 255 characters or fewer")
        String headline
) {
}
