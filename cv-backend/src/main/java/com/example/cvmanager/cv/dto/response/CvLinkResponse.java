package com.example.cvmanager.cv.dto.response;

public record CvLinkResponse(
        Long id,
        String label,
        String url,
        int displayOrder
) {
}
