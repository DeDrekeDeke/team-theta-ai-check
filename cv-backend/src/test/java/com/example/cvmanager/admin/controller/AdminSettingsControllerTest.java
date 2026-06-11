package com.example.cvmanager.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cvmanager.admin.dto.AppSettingResponse;
import com.example.cvmanager.admin.service.AdminSettingsService;
import com.example.cvmanager.auth.security.JwtService;
import com.example.cvmanager.common.exception.ForbiddenException;
import com.example.cvmanager.common.exception.GlobalExceptionHandler;
import com.example.cvmanager.common.security.AdminAccessService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminSettingsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AdminSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminSettingsService adminSettingsService;

    @MockitoBean
    private AdminAccessService adminAccessService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void listSettingsReturnsManagedSettings() throws Exception {
        when(adminSettingsService.listSettings()).thenReturn(List.of(
                new AppSettingResponse(
                        "application.displayName",
                        "CV Manager",
                        "STRING",
                        "Application display name",
                        "Display name shown in the application UI.")));

        mockMvc.perform(get("/api/admin/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("application.displayName"))
                .andExpect(jsonPath("$[0].value").value("CV Manager"))
                .andExpect(jsonPath("$[0].valueType").value("STRING"));
    }

    @Test
    void updateSettingReturnsUpdatedSetting() throws Exception {
        when(adminSettingsService.updateSetting(any(), any())).thenReturn(new AppSettingResponse(
                "ai.toolsetEnabled",
                "false",
                "BOOLEAN",
                "AI toolset usage enabled",
                "Controls whether AI-assisted CV actions can run."));

        mockMvc.perform(put("/api/admin/settings/ai.toolsetEnabled")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": "false"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("ai.toolsetEnabled"))
                .andExpect(jsonPath("$.value").value("false"))
                .andExpect(jsonPath("$.valueType").value("BOOLEAN"));
    }

    @Test
    void updateSettingRejectsNonAdminCaller() throws Exception {
        doThrow(new ForbiddenException("Admin access is required", "ADMIN_REQUIRED"))
                .when(adminAccessService).requireAdmin(isNull());

        mockMvc.perform(put("/api/admin/settings/ai.toolsetEnabled")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": "false"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ADMIN_REQUIRED"));
    }
}
