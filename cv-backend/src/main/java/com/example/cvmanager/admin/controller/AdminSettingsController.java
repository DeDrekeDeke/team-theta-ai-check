package com.example.cvmanager.admin.controller;

import com.example.cvmanager.admin.dto.AppSettingResponse;
import com.example.cvmanager.admin.dto.AppSettingUpdateRequest;
import com.example.cvmanager.admin.service.AdminSettingsService;
import com.example.cvmanager.auth.security.AuthenticatedUser;
import com.example.cvmanager.common.security.AdminAccessService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final AdminAccessService adminAccessService;

    public AdminSettingsController(AdminSettingsService adminSettingsService, AdminAccessService adminAccessService) {
        this.adminSettingsService = adminSettingsService;
        this.adminAccessService = adminAccessService;
    }

    @GetMapping
    public List<AppSettingResponse> listSettings(@AuthenticationPrincipal AuthenticatedUser user) {
        adminAccessService.requireAdmin(user);
        return adminSettingsService.listSettings();
    }

    @PutMapping("/{key}")
    public AppSettingResponse updateSetting(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String key,
            @Valid @RequestBody AppSettingUpdateRequest request) {
        adminAccessService.requireAdmin(user);
        return adminSettingsService.updateSetting(key, request);
    }
}
