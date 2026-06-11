package com.example.cvmanager.config.controller;

import com.example.cvmanager.admin.service.AdminSettingsService;
import com.example.cvmanager.config.dto.AppConfigResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app-config")
public class AppConfigController {

    private final AdminSettingsService adminSettingsService;

    public AppConfigController(AdminSettingsService adminSettingsService) {
        this.adminSettingsService = adminSettingsService;
    }

    @GetMapping
    public AppConfigResponse getAppConfig() {
        return new AppConfigResponse(
                adminSettingsService.getApplicationDisplayName(),
                adminSettingsService.isAiToolsetEnabled());
    }
}
