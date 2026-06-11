package com.example.cvmanager.auth.security;

public record AuthenticatedUser(
        Long userId,
        String email,
        String displayName,
        String role) {

    public boolean admin() {
        return "ADMIN".equals(role);
    }
}
