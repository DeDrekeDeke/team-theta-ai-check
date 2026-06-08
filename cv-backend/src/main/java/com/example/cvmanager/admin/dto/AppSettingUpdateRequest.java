package com.example.cvmanager.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AppSettingUpdateRequest(@NotBlank String value) {
}
