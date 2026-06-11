package com.example.cvmanager.cv.dto.response;

public record CvPersonalDetailsResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String location,
        String headline
) {
}
