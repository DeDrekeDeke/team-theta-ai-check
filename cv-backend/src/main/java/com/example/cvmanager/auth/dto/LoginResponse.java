package com.example.cvmanager.auth.dto;

public record LoginResponse(
        Long userId,
        String email,
        String displayName,
        boolean admin,
        String token) {
}
