package com.example.cvmanager.admin.dto;

public record AppSettingResponse(
        String key,
        String value,
        String valueType,
        String label,
        String description) {
}
