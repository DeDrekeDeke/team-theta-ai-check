package com.example.cvmanager.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(max = 100) String displayName,
        @Size(min = 8, max = 60) @Pattern(regexp = ".*\\S.*") String password,
        boolean admin) {
}
