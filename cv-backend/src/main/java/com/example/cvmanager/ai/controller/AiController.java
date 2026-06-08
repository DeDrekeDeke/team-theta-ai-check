package com.example.cvmanager.ai.controller;

import com.example.cvmanager.ai.dto.AiSuggestionResponse;
import com.example.cvmanager.ai.service.AiApplicationService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public List<AiSuggestionResponse> listSuggestions(@PathVariable Long cvId) {
        return aiApplicationService.listSuggestions(cvId);
    }

    @PostMapping("/improve-summary")
    @ResponseStatus(HttpStatus.CREATED)
    public AiSuggestionResponse improveSummary(@PathVariable Long cvId) {
        return aiApplicationService.improveSummary(cvId);
    }
}
