package com.example.cvmanager.admin.controller;

import com.example.cvmanager.admin.dto.AppSettingResponse;
import com.example.cvmanager.admin.dto.AppSettingUpdateRequest;
import com.example.cvmanager.admin.service.AdminSettingsService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/settings")
public class AdminSettingsController {

    private final AdminSettingsService adminSettingsService;

    public AdminSettingsController(AdminSettingsService adminSettingsService) {
        this.adminSettingsService = adminSettingsService;
    }

    @GetMapping
    public List<AppSettingResponse> listSettings() {
        return adminSettingsService.listSettings();
    }

    @PutMapping("/{key}")
    public AppSettingResponse updateSetting(
            @PathVariable String key,
            @Valid @RequestBody AppSettingUpdateRequest request) {
        return adminSettingsService.updateSetting(key, request);
    }
}
