package com.example.cvmanager.cv.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cv")
public record CvStorageProperties(String uploadDir) {

    public CvStorageProperties {
        if (uploadDir == null || uploadDir.isBlank()) {
            uploadDir = "uploads";
        }
    }
}
