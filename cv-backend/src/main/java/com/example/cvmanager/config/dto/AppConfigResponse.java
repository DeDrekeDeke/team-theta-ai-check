package com.example.cvmanager.config.dto;

public record AppConfigResponse(
        String applicationDisplayName,
        boolean aiToolsetEnabled) {
}
