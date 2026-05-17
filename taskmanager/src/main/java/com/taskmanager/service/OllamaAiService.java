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
public class OllamaAiService implements AiSuggestionProvider {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final TaskSuggestionParser suggestionParser;
    private final String model;
    private final String baseUrl;

    public OllamaAiService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            TaskSuggestionParser suggestionParser,
            @Value("${ollama.api.model:phi3:latest}") String model,
            @Value("${ollama.api.base-url:http://localhost:11434}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.suggestionParser = suggestionParser;
        this.model = model;
        this.baseUrl = trimTrailingSlash(baseUrl);
    }

    @Override
    public String providerName() {
        return "ollama";
    }

    @Override
    public AiGenerateResponse generateSuggestions(String goal) {
        if (!StringUtils.hasText(goal)) {
            throw new IllegalArgumentException("Goal is required");
        }

        if (!StringUtils.hasText(model)) {
            throw new IllegalStateException("Ollama model is not configured");
        }

        String systemPrompt = """
                You are a productivity assistant inside a task manager app.
                Generate 3 to 5 concise, actionable task titles for the user's goal.
                Return only JSON shaped like {"suggestions":["Task one","Task two"]}.
                Keep each suggestion under 12 words.
                """;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", "Goal: \"%s\"".formatted(goal.trim()))
                ),
                "format", "json",
                "options", Map.of("temperature", 0.4),
                "stream", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "%s/api/chat".formatted(baseUrl);
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
                throw new IllegalStateException("Ollama returned an empty response");
            }

            return new AiGenerateResponse(suggestions, rawText, "ollama", model);
        } catch (HttpStatusCodeException ex) {
            throw new IllegalStateException(buildApiErrorMessage(ex), ex);
        } catch (ResourceAccessException ex) {
            throw new IllegalStateException("Unable to reach Ollama at %s. Start Ollama and pull the configured model.".formatted(baseUrl), ex);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Ollama request failed. Please try again.", ex);
        }
    }

    private String extractText(String responseBody) {
        if (!StringUtils.hasText(responseBody)) {
            return "";
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("message").path("content").asText("").trim();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to parse Ollama response", ex);
        }
    }

    private String buildApiErrorMessage(HttpStatusCodeException ex) {
        String responseBody = ex.getResponseBodyAsString();
        String fallback = "Ollama returned HTTP %d.".formatted(ex.getStatusCode().value());

        if (!StringUtils.hasText(responseBody)) {
            return fallback;
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String message = root.path("error").asText("");

            if (StringUtils.hasText(message)) {
                return "Ollama returned HTTP %d: %s".formatted(ex.getStatusCode().value(), message);
            }
        } catch (Exception ignored) {
            // Fall through to a bounded raw message when Ollama returns non-JSON errors.
        }

        String compactBody = responseBody.replaceAll("\\s+", " ").trim();
        if (compactBody.length() > 180) {
            compactBody = compactBody.substring(0, 180) + "...";
        }

        return "Ollama returned HTTP %d: %s".formatted(ex.getStatusCode().value(), compactBody);
    }

    private String trimTrailingSlash(String value) {
        if (!StringUtils.hasText(value)) {
            return "http://localhost:11434";
        }

        return value.trim().replaceAll("/+$", "");
    }
}
