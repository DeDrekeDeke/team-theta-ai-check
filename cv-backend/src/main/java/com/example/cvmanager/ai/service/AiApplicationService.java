package com.example.cvmanager.ai.service;

import com.example.cvmanager.ai.dto.AiSuggestionResponse;
import com.example.cvmanager.ai.model.AiActionType;
import com.example.cvmanager.ai.model.AiSuggestion;
import com.example.cvmanager.ai.repository.AiSuggestionRepository;
import com.example.cvmanager.admin.service.AdminSettingsService;
import com.example.cvmanager.auth.security.AuthenticatedUser;
import com.example.cvmanager.common.exception.BadRequestException;
import com.example.cvmanager.cv.model.Cv;
import com.example.cvmanager.cv.service.CvService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiApplicationService {

    private final CvService cvService;
    private final AiService aiService;
    private final AiSuggestionRepository aiSuggestionRepository;
    private final AdminSettingsService adminSettingsService;

    public AiApplicationService(
            CvService cvService,
            AiService aiService,
            AiSuggestionRepository aiSuggestionRepository,
            AdminSettingsService adminSettingsService) {
        this.cvService = cvService;
        this.aiService = aiService;
        this.aiSuggestionRepository = aiSuggestionRepository;
        this.adminSettingsService = adminSettingsService;
    }

    @Transactional
    public AiSuggestionResponse improveSummary(AuthenticatedUser user, Long cvId) {
        if (!adminSettingsService.isAiToolsetEnabled()) {
            throw new BadRequestException("AI toolset usage is disabled", "AI_DISABLED");
        }

        Cv cv = cvService.findAuthorizedCv(user, cvId);
        String source = cvService.getUploadedHtml(user, cvId);
        String suggestedText = aiService.suggest(AiActionType.IMPROVE_SUMMARY, source);

        AiSuggestion suggestion = new AiSuggestion(
                cv,
                AiActionType.IMPROVE_SUMMARY,
                source,
                suggestedText,
                "PENDING");

        return toResponse(aiSuggestionRepository.save(suggestion));
    }

    @Transactional(readOnly = true)
    public List<AiSuggestionResponse> listSuggestions(AuthenticatedUser user, Long cvId) {
        cvService.findAuthorizedCv(user, cvId);
        return aiSuggestionRepository.findByCvIdOrderByCreatedAtDesc(cvId).stream()
                .map(this::toResponse)
                .toList();
    }

    private AiSuggestionResponse toResponse(AiSuggestion suggestion) {
        return new AiSuggestionResponse(
                suggestion.getId(),
                suggestion.getCv().getId(),
                suggestion.getActionType(),
                suggestion.getOriginalText(),
                suggestion.getSuggestedText(),
                suggestion.getStatus(),
                suggestion.getCreatedAt());
    }
}
