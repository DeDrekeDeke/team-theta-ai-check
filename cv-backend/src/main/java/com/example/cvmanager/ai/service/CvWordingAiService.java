package com.example.cvmanager.ai.service;

import com.example.cvmanager.ai.model.AiActionType;
import com.example.cvmanager.common.exception.BadRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CvWordingAiService implements AiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CvWordingAiService.class);
    private static final String DEFAULT_MODEL = "gpt-5.5";
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public CvWordingAiService(
            RestClient.Builder restClientBuilder,
            @Value("${app.ai.openai.api-key:}") String apiKey,
            @Value("${app.ai.openai.model:gpt-5.5}") String model,
            @Value("${app.ai.openai.base-url:https://api.openai.com/v1}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.model = StringUtils.hasText(model) ? model : DEFAULT_MODEL;
    }

    @Override
    public String suggest(AiActionType actionType, String input) {
        if (StringUtils.hasText(apiKey)) {
            try {
                return suggestWithOpenAi(actionType, input);
            } catch (RestClientException | IllegalStateException exception) {
                LOGGER.warn("OpenAI CV wording suggestion failed", exception);
                throw new BadRequestException("AI provider failed to generate a suggestion", "AI_PROVIDER_FAILED");
            }
        }
        return suggestWithMock(input);
    }

    private String suggestWithOpenAi(AiActionType actionType, String input) {
        JsonNode response = restClient.post()
                .uri("/responses")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody(actionType, input))
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new IllegalStateException("OpenAI response was empty");
        }

        String outputText = extractOutputText(response);
        if (!StringUtils.hasText(outputText)) {
            throw new IllegalStateException("OpenAI response did not include text output");
        }
        return outputText.trim();
    }

    private Map<String, Object> requestBody(AiActionType actionType, String input) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("instructions", instructionsFor(actionType));
        body.put("input", "Current CV text:\n" + input);
        body.put("max_output_tokens", 360);
        body.put("store", false);
        body.put("text", Map.of(
                "format", Map.of("type", "text"),
                "verbosity", "low"));
        return body;
    }

    private String instructionsFor(AiActionType actionType) {
        if (actionType == AiActionType.IMPROVE_SUMMARY) {
            return "Improve the wording of this CV summary. Preserve the user's facts, do not invent experience, "
                    + "keep the result under 500 characters, and return only the rewritten summary text.";
        }
        if (actionType == AiActionType.IMPROVE_EDUCATION) {
            return "Improve the wording of this CV education description. Preserve the user's facts, "
                    + "do not invent achievements, and return only the rewritten description text.";
        }
        if (actionType == AiActionType.IMPROVE_WORK_EXPERIENCE) {
            return "Improve the wording of this CV work experience description. Preserve the user's facts, "
                    + "make the language more achievement-oriented without inventing results, and return only the rewritten description text.";
        }
        if (actionType == AiActionType.IMPROVE_SKILLS) {
            return "Improve the wording of this CV skill entry. Preserve the listed skill and proficiency facts, "
                    + "do not add new skills, and return only the rewritten skill entry text.";
        }
        return "Improve the wording of this CV section. Preserve the user's facts, do not invent experience, "
                + "and return only the rewritten text.";
    }

    private String extractOutputText(JsonNode response) {
        JsonNode directOutputText = response.path("output_text");
        if (directOutputText.isTextual() && StringUtils.hasText(directOutputText.asText())) {
            return directOutputText.asText();
        }

        for (JsonNode outputItem : response.path("output")) {
            for (JsonNode contentItem : outputItem.path("content")) {
                JsonNode text = contentItem.path("text");
                if ("output_text".equals(contentItem.path("type").asText()) && text.isTextual()) {
                    return text.asText();
                }
            }
        }
        return "";
    }

    private String suggestWithMock(String input) {
        String normalized = WHITESPACE_PATTERN.matcher(input.trim()).replaceAll(" ");
        String rewritten = normalized
                .replaceAll("(?i)\\bi am\\b", "I am")
                .replaceAll("(?i)\\bworked on\\b", "contributed to")
                .replaceAll("(?i)\\bhelped with\\b", "supported")
                .replaceAll("(?i)\\bgood at\\b", "skilled in")
                .replaceAll("(?i)\\bthings\\b", "initiatives");

        return ensureSentence(capitalizeFirst(rewritten));
    }

    private String capitalizeFirst(String value) {
        if (value.isEmpty()) {
            return value;
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    private String ensureSentence(String value) {
        if (value.endsWith(".") || value.endsWith("!") || value.endsWith("?")) {
            return value;
        }
        return value + ".";
    }
}
