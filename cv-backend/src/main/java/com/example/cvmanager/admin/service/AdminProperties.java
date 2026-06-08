package com.example.cvmanager.admin.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(String email, String password) {

    public AdminProperties {
        if (email == null || email.isBlank()) {
            email = "admin@example.com";
        }
        if (password == null || password.isBlank()) {
            password = "admin123";
        }
    }
}
