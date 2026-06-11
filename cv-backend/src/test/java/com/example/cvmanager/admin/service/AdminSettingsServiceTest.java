package com.example.cvmanager.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cvmanager.admin.dto.AppSettingResponse;
import com.example.cvmanager.admin.dto.AppSettingUpdateRequest;
import com.example.cvmanager.admin.model.AppSetting;
import com.example.cvmanager.admin.model.AppSettingDefinition;
import com.example.cvmanager.admin.repository.AppSettingRepository;
import com.example.cvmanager.common.exception.BadRequestException;
import com.example.cvmanager.common.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminSettingsServiceTest {

    private AppSettingRepository appSettingRepository;
    private AdminSettingsService adminSettingsService;

    @BeforeEach
    void setUp() {
        appSettingRepository = mock(AppSettingRepository.class);
        adminSettingsService = new AdminSettingsService(appSettingRepository);
    }

    @Test
    void listSettingsReturnsOnlyManagedSettings() {
        when(appSettingRepository.findAllById(AppSettingDefinition.keys())).thenReturn(List.of(
                new AppSetting("application.displayName", "Hiring Portal", "Display name"),
                new AppSetting("ai.toolsetEnabled", "false", "AI enabled")));

        List<AppSettingResponse> settings = adminSettingsService.listSettings();

        assertEquals(2, settings.size());
        assertEquals("application.displayName", settings.get(0).key());
        assertEquals("Hiring Portal", settings.get(0).value());
        assertEquals("STRING", settings.get(0).valueType());
        assertEquals("ai.toolsetEnabled", settings.get(1).key());
        assertEquals("false", settings.get(1).value());
        assertEquals("BOOLEAN", settings.get(1).valueType());
        verify(appSettingRepository).findAllById(List.of("application.displayName", "ai.toolsetEnabled"));
    }

    @Test
    void updateSettingNormalizesBooleanValues() {
        AppSetting setting = new AppSetting("ai.toolsetEnabled", "true", "AI enabled");
        when(appSettingRepository.findById("ai.toolsetEnabled")).thenReturn(Optional.of(setting));
        when(appSettingRepository.save(any(AppSetting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppSettingResponse response = adminSettingsService.updateSetting(
                "ai.toolsetEnabled",
                new AppSettingUpdateRequest(" FALSE "));

        assertEquals("false", response.value());
        assertEquals("false", setting.getValue());
    }

    @Test
    void updateSettingRejectsInvalidBooleanValues() {
        AppSetting setting = new AppSetting("ai.toolsetEnabled", "true", "AI enabled");
        when(appSettingRepository.findById("ai.toolsetEnabled")).thenReturn(Optional.of(setting));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> adminSettingsService.updateSetting(
                        "ai.toolsetEnabled",
                        new AppSettingUpdateRequest("sometimes")));

        assertEquals("SETTING_INVALID_VALUE", exception.getCode());
    }

    @Test
    void updateSettingRejectsUnknownKeys() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> adminSettingsService.updateSetting(
                        "admin.email",
                        new AppSettingUpdateRequest("admin@example.com")));

        assertEquals("SETTING_NOT_FOUND", exception.getCode());
    }
}
