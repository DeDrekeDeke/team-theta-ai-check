package com.example.cvmanager.ai.service;

import com.example.cvmanager.ai.dto.AiImproveWordingRequest;
import com.example.cvmanager.ai.dto.AiSuggestionResponse;
import com.example.cvmanager.ai.model.AiActionType;
import com.example.cvmanager.ai.model.AiSuggestion;
import com.example.cvmanager.ai.repository.AiSuggestionRepository;
import com.example.cvmanager.admin.service.AdminSettingsService;
import com.example.cvmanager.auth.security.AuthenticatedUser;
import com.example.cvmanager.common.exception.BadRequestException;
import com.example.cvmanager.common.exception.NotFoundException;
import com.example.cvmanager.cv.model.Cv;
import com.example.cvmanager.cv.service.CvService;
import java.util.Locale;
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
        requireAiEnabled();
        Cv cv = cvService.findAuthorizedCv(user, cvId);
        return createSuggestion(cv, AiActionType.IMPROVE_SUMMARY, "summary", cv.getSummary());
    }

    @Transactional
    public AiSuggestionResponse improveWording(AuthenticatedUser user, Long cvId, AiImproveWordingRequest request) {
        requireAiEnabled();
        Cv cv = cvService.findAuthorizedCv(user, cvId);
        AiActionType actionType = actionTypeForSection(request.section());
        return createSuggestion(cv, actionType, request.targetKey(), request.text());
    }

    @Transactional(readOnly = true)
    public List<AiSuggestionResponse> listSuggestions(AuthenticatedUser user, Long cvId) {
        cvService.findAuthorizedCv(user, cvId);
        return aiSuggestionRepository.findByCvIdOrderByCreatedAtDesc(cvId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AiSuggestionResponse acceptSuggestion(AuthenticatedUser user, Long cvId, Long suggestionId) {
        cvService.findAuthorizedCv(user, cvId);
        AiSuggestion suggestion = findSuggestion(cvId, suggestionId);
        requirePending(suggestion);
        suggestion.accept();
        return toResponse(aiSuggestionRepository.save(suggestion));
    }

    @Transactional
    public AiSuggestionResponse declineSuggestion(AuthenticatedUser user, Long cvId, Long suggestionId) {
        cvService.findAuthorizedCv(user, cvId);
        AiSuggestion suggestion = findSuggestion(cvId, suggestionId);
        requirePending(suggestion);
        suggestion.decline();
        return toResponse(aiSuggestionRepository.save(suggestion));
    }

    private void requireAiEnabled() {
        if (!adminSettingsService.isAiToolsetEnabled()) {
            throw new BadRequestException("AI toolset usage is disabled", "AI_DISABLED");
        }
    }

    private AiSuggestionResponse createSuggestion(Cv cv, AiActionType actionType, String targetKey, String source) {
        String sourceText = requireSourceText(source);
        String suggestedText = aiService.suggest(actionType, sourceText);

        AiSuggestion suggestion = new AiSuggestion(
                cv,
                actionType,
                normalizeTargetKey(targetKey),
                sourceText,
                suggestedText,
                AiSuggestion.STATUS_PENDING);

        return toResponse(aiSuggestionRepository.save(suggestion));
    }

    private String requireSourceText(String source) {
        if (source == null || source.isBlank()) {
            throw new BadRequestException("Add text before running the AI wording action", "AI_SOURCE_TEXT_REQUIRED");
        }
        return source.trim();
    }

    private String normalizeTargetKey(String targetKey) {
        return targetKey.trim();
    }

    private void requirePending(AiSuggestion suggestion) {
        if (!AiSuggestion.STATUS_PENDING.equals(suggestion.getStatus())) {
            throw new BadRequestException("AI suggestion has already been reviewed", "AI_SUGGESTION_ALREADY_REVIEWED");
        }
    }

    private AiActionType actionTypeForSection(String section) {
        String normalizedSection = section.trim().toLowerCase(Locale.ROOT);
        if ("summary".equals(normalizedSection)) {
            return AiActionType.IMPROVE_SUMMARY;
        }
        if ("education".equals(normalizedSection)) {
            return AiActionType.IMPROVE_EDUCATION;
        }
        if ("work".equals(normalizedSection)
                || "workexperience".equals(normalizedSection)
                || "work_experience".equals(normalizedSection)
                || "work experience".equals(normalizedSection)) {
            return AiActionType.IMPROVE_WORK_EXPERIENCE;
        }
        if ("skills".equals(normalizedSection)) {
            return AiActionType.IMPROVE_SKILLS;
        }
        throw new BadRequestException("This AI action is not supported for that CV section", "AI_SECTION_UNSUPPORTED");
    }

    private AiSuggestion findSuggestion(Long cvId, Long suggestionId) {
        return aiSuggestionRepository.findByIdAndCvId(suggestionId, cvId)
                .orElseThrow(() -> new NotFoundException("AI suggestion not found", "AI_SUGGESTION_NOT_FOUND"));
    }

    private AiSuggestionResponse toResponse(AiSuggestion suggestion) {
        return new AiSuggestionResponse(
                suggestion.getId(),
                suggestion.getCv().getId(),
                suggestion.getActionType(),
                suggestion.getTargetKey(),
                suggestion.getOriginalText(),
                suggestion.getSuggestedText(),
                suggestion.getStatus(),
                suggestion.getCreatedAt());
    }
}
