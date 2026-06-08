package com.example.cvmanager.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.as-is")
public record AsIsSecurityProperties(String demoTokenPrefix) {

    public AsIsSecurityProperties {
        if (demoTokenPrefix == null || demoTokenPrefix.isBlank()) {
            demoTokenPrefix = "demo-token";
        }
    }
}
