package com.taskmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.taskmanager.model.AiGenerateResponse;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class GeminiAiService {

    private static final String FALLBACK_SUGGESTIONS = """
            1. Revise Java OOP concepts
            2. Practice Spring Boot REST APIs
            3. Learn Hibernate mappings
            4. Solve coding problems
            5. Review JWT Authentication
            """;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final String baseUrl;

    public GeminiAiService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.api.model:gemini-1.5-flash}") String model,
            @Value("${gemini.api.base-url:https://generativelanguage.googleapis.com/v1beta}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = normalizeApiKey(apiKey);
        this.model = model;
        this.baseUrl = baseUrl;
    }

    public AiGenerateResponse generateSuggestions(String goal) {
        if (!StringUtils.hasText(goal)) {
            throw new IllegalArgumentException("Goal is required");
        }

        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("Gemini API key is not configured");
        }

        String prompt = """
                You are a productivity assistant inside a task manager app.
                Generate 3 to 5 concise, actionable subtasks or productivity suggestions for this goal:
                "%s"

                Rules:
                - Return only the suggestions.
                - Put each suggestion on a separate line.
                - Do not add introductions, markdown headings, or explanations.
                - Keep each suggestion under 12 words.
                """.formatted(goal.trim());

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(Map.of("text", prompt))
                        )
                ),
                "generationConfig", Map.of(
                        "temperature", 0.4,
                        "maxOutputTokens", 256
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        String url = "%s/models/%s:generateContent".formatted(baseUrl, model);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String rawText = extractText(response.getBody());
            List<String> suggestions = parseSuggestions(rawText);

            if (suggestions.isEmpty()) {
                throw new IllegalStateException("Gemini returned an empty response");
            }

            return new AiGenerateResponse(suggestions, rawText);
        } catch (Exception e) {
            return new AiGenerateResponse(parseSuggestions(FALLBACK_SUGGESTIONS), FALLBACK_SUGGESTIONS);
        }
    }

    private String extractText(String responseBody) {
        if (!StringUtils.hasText(responseBody)) {
            return "";
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                return "";
            }

            JsonNode parts = candidates.get(0).path("content").path("parts");
            StringBuilder output = new StringBuilder();

            if (parts.isArray()) {
                for (JsonNode part : parts) {
                    String text = part.path("text").asText("");
                    if (StringUtils.hasText(text)) {
                        output.append(text).append(System.lineSeparator());
                    }
                }
            }

            return output.toString().trim();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to parse Gemini response", ex);
        }
    }

    private List<String> parseSuggestions(String rawText) {
        List<String> suggestions = new ArrayList<>();

        if (!StringUtils.hasText(rawText)) {
            return suggestions;
        }

        for (String line : rawText.split("\\R")) {
            String cleaned = line
                    .replaceFirst("^\\s*[-*•]\\s*", "")
                    .replaceFirst("^\\s*\\d+[.)]\\s*", "")
                    .trim();

            if (StringUtils.hasText(cleaned)) {
                suggestions.add(cleaned);
            }

            if (suggestions.size() == 5) {
                break;
            }
        }

        return suggestions;
    }

    private String normalizeApiKey(String rawApiKey) {
        if (!StringUtils.hasText(rawApiKey)) {
            return "";
        }

        return rawApiKey.trim().replaceAll("^['\"]|['\"]$", "");
    }
}
