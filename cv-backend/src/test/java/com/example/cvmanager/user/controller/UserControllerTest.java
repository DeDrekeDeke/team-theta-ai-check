package com.example.cvmanager.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cvmanager.auth.security.JwtService;
import com.example.cvmanager.common.exception.ForbiddenException;
import com.example.cvmanager.common.exception.GlobalExceptionHandler;
import com.example.cvmanager.common.security.AdminAccessService;
import com.example.cvmanager.user.dto.UserResponse;
import com.example.cvmanager.user.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminAccessService adminAccessService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void createUserReturnsCreatedResponse() throws Exception {
        when(userService.createUser(any())).thenReturn(new UserResponse(
                4L,
                "carol@example.com",
                "Carol Candidate",
                false,
                LocalDateTime.of(2026, 6, 10, 12, 0)));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "carol@example.com",
                                  "displayName": "Carol Candidate",
                                  "password": "carol123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("carol@example.com"))
                .andExpect(jsonPath("$.displayName").value("Carol Candidate"))
                .andExpect(jsonPath("$.admin").value(false))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void createUserRejectsNonAdminCaller() throws Exception {
        doThrow(new ForbiddenException("Admin access is required", "ADMIN_REQUIRED"))
                .when(adminAccessService).requireAdmin(isNull());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "carol@example.com",
                                  "displayName": "Carol Candidate",
                                  "password": "carol123"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ADMIN_REQUIRED"));
    }
}
