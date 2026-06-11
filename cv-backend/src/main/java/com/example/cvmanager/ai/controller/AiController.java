package com.example.cvmanager.ai.controller;

import com.example.cvmanager.ai.dto.AiImproveWordingRequest;
import com.example.cvmanager.ai.dto.AiSuggestionResponse;
import com.example.cvmanager.ai.service.AiApplicationService;
import com.example.cvmanager.auth.security.AuthenticatedUser;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cvs/{cvId}/ai-actions")
public class AiController {

    private final AiApplicationService aiApplicationService;

    public AiController(AiApplicationService aiApplicationService) {
        this.aiApplicationService = aiApplicationService;
    }

    @GetMapping("/suggestions")
    public List<AiSuggestionResponse> listSuggestions(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long cvId) {
        return aiApplicationService.listSuggestions(user, cvId);
    }

    @PostMapping("/improve-summary")
    @ResponseStatus(HttpStatus.CREATED)
    public AiSuggestionResponse improveSummary(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long cvId) {
        return aiApplicationService.improveSummary(user, cvId);
    }

    @PostMapping("/improve-wording")
    @ResponseStatus(HttpStatus.CREATED)
    public AiSuggestionResponse improveWording(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long cvId,
            @Valid @RequestBody AiImproveWordingRequest request) {
        return aiApplicationService.improveWording(user, cvId, request);
    }

    @PostMapping("/suggestions/{suggestionId}/accept")
    public AiSuggestionResponse acceptSuggestion(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long cvId,
            @PathVariable Long suggestionId) {
        return aiApplicationService.acceptSuggestion(user, cvId, suggestionId);
    }

    @PostMapping("/suggestions/{suggestionId}/decline")
    public AiSuggestionResponse declineSuggestion(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long cvId,
            @PathVariable Long suggestionId) {
        return aiApplicationService.declineSuggestion(user, cvId, suggestionId);
    }
}
