package com.taskmanager.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class TaskSuggestionParser {

    private final ObjectMapper objectMapper;

    public TaskSuggestionParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<String> parse(String rawText) {
        List<String> jsonSuggestions = parseJsonSuggestions(rawText);
        if (!jsonSuggestions.isEmpty()) {
            return jsonSuggestions;
        }

        List<String> suggestions = new ArrayList<>();

        if (!StringUtils.hasText(rawText)) {
            return suggestions;
        }

        for (String line : rawText.split("\\R")) {
            String cleaned = line
                    .replaceFirst("^\\s*[-*]\\s*", "")
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

    private List<String> parseJsonSuggestions(String rawText) {
        List<String> suggestions = new ArrayList<>();

        if (!StringUtils.hasText(rawText)) {
            return suggestions;
        }

        try {
            JsonNode root = objectMapper.readTree(rawText);
            JsonNode suggestionsNode = root.isArray() ? root : root.path("suggestions");

            if (!suggestionsNode.isArray()) {
                return suggestions;
            }

            for (JsonNode suggestionNode : suggestionsNode) {
                String suggestion = suggestionNode.asText("").trim();

                if (StringUtils.hasText(suggestion)) {
                    suggestions.add(suggestion);
                }

                if (suggestions.size() == 5) {
                    break;
                }
            }
        } catch (Exception ex) {
            return suggestions;
        }

        return suggestions;
    }
}
