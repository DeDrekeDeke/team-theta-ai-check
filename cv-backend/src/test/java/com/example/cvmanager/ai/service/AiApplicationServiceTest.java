package com.example.cvmanager.ai.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.cvmanager.admin.service.AdminSettingsService;
import com.example.cvmanager.ai.repository.AiSuggestionRepository;
import com.example.cvmanager.auth.security.AuthenticatedUser;
import com.example.cvmanager.common.exception.BadRequestException;
import com.example.cvmanager.cv.service.CvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AiApplicationServiceTest {

    private CvService cvService;
    private AiService aiService;
    private AiSuggestionRepository aiSuggestionRepository;
    private AdminSettingsService adminSettingsService;
    private AiApplicationService aiApplicationService;

    @BeforeEach
    void setUp() {
        cvService = mock(CvService.class);
        aiService = mock(AiService.class);
        aiSuggestionRepository = mock(AiSuggestionRepository.class);
        adminSettingsService = mock(AdminSettingsService.class);
        aiApplicationService = new AiApplicationService(
                cvService,
                aiService,
                aiSuggestionRepository,
                adminSettingsService);
    }

    @Test
    void improveSummaryRejectsWhenAiToolsetIsDisabled() {
        when(adminSettingsService.isAiToolsetEnabled()).thenReturn(false);
        AuthenticatedUser user = new AuthenticatedUser(1L, "alice@example.com", "Alice Student", "USER");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> aiApplicationService.improveSummary(user, 10L));

        assertEquals("AI_DISABLED", exception.getCode());
        verifyNoInteractions(cvService, aiService, aiSuggestionRepository);
    }
}
