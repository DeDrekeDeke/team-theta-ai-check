package com.example.cvmanager.common.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String message,
        String code,
        List<String> details,
        Instant timestamp) {

    public static ErrorResponse of(String message, String code, List<String> details) {
        return new ErrorResponse(message, code, details, Instant.now());
    }
}
