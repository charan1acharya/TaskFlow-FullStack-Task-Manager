package com.taskmanager.service;

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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.taskmanager.model.AiGenerateResponse;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class GrokAiService implements AiSuggestionProvider {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final TaskSuggestionParser suggestionParser;
    private final String apiKey;
    private final String model;
    private final String baseUrl;

    public GrokAiService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            TaskSuggestionParser suggestionParser,
            @Value("${grok.api.key:}") String apiKey,
            @Value("${grok.api.model:grok-4.3}") String model,
            @Value("${grok.api.base-url:https://api.x.ai/v1}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.suggestionParser = suggestionParser;
        this.apiKey = normalizeApiKey(apiKey);
        this.model = model;
        this.baseUrl = baseUrl;
    }

    @Override
    public String providerName() {
        return "grok";
    }

    @Override
    public AiGenerateResponse generateSuggestions(String goal) {
        if (!StringUtils.hasText(goal)) {
            throw new IllegalArgumentException("Goal is required");
        }

        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("Grok API key is not configured");
        }

        String systemPrompt = """
                You are Grok inside a task manager app.
                Generate 3 to 5 concise, actionable task titles for the user's goal.
                Each suggestion must be useful as a standalone task title.
                Keep each suggestion under 12 words.
                """;

        String userPrompt = "Goal: \"%s\"".formatted(goal.trim());

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", systemPrompt
                        ),
                        Map.of(
                                "role", "user",
                                "content", userPrompt
                        )
                ),
                "response_format", suggestionResponseFormat(),
                "temperature", 0.4,
                "max_tokens", 256,
                "stream", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String url = "%s/chat/completions".formatted(baseUrl);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String rawText = extractText(response.getBody());
            List<String> suggestions = suggestionParser.parse(rawText);

            if (suggestions.isEmpty()) {
                throw new IllegalStateException("Grok returned an empty response");
            }

            return new AiGenerateResponse(suggestions, rawText, "grok", model);
        } catch (HttpStatusCodeException ex) {
            throw new IllegalStateException(buildApiErrorMessage(ex), ex);
        } catch (ResourceAccessException ex) {
            throw new IllegalStateException("Unable to reach Grok. Check your network connection.", ex);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Grok request failed. Please try again.", ex);
        }
    }

    private Map<String, Object> suggestionResponseFormat() {
        return Map.of(
                "type", "json_schema",
                "json_schema", Map.of(
                        "name", "task_suggestions",
                        "schema", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "required", List.of("suggestions"),
                                "properties", Map.of(
                                        "suggestions", Map.of(
                                                "type", "array",
                                                "items", Map.of("type", "string")
                                        )
                                )
                        )
                )
        );
    }

    private String extractText(String responseBody) {
        if (!StringUtils.hasText(responseBody)) {
            return "";
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {
                return "";
            }

            return choices.get(0).path("message").path("content").asText("").trim();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to parse Grok response", ex);
        }
    }

    private String buildApiErrorMessage(HttpStatusCodeException ex) {
        String responseBody = ex.getResponseBodyAsString();
        String fallback = "Grok returned HTTP %d.".formatted(ex.getStatusCode().value());

        if (!StringUtils.hasText(responseBody)) {
            return fallback;
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String message = root.path("error").path("message").asText("");

            if (StringUtils.hasText(message)) {
                return "Grok returned HTTP %d: %s".formatted(ex.getStatusCode().value(), message);
            }
        } catch (Exception ignored) {
            // Fall through to a bounded raw message when Grok returns non-JSON errors.
        }

        String compactBody = responseBody.replaceAll("\\s+", " ").trim();
        if (compactBody.length() > 180) {
            compactBody = compactBody.substring(0, 180) + "...";
        }

        return "Grok returned HTTP %d: %s".formatted(ex.getStatusCode().value(), compactBody);
    }

    private String normalizeApiKey(String rawApiKey) {
        if (!StringUtils.hasText(rawApiKey)) {
            return "";
        }

        return rawApiKey.trim().replaceAll("^['\"]|['\"]$", "");
    }
}
