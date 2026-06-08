package com.example.cvmanager.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String displayName,
        boolean admin,
        LocalDateTime createdAt) {
}
