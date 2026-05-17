package com.taskmanager.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.taskmanager.model.AiGenerateResponse;

@Service
public class AiSuggestionService {

    private final Map<String, AiSuggestionProvider> providers;
    private final String configuredProvider;

    public AiSuggestionService(
            List<AiSuggestionProvider> providers,
            @Value("${ai.provider:grok}") String configuredProvider) {
        this.providers = providers.stream()
                .collect(Collectors.toMap(
                        provider -> provider.providerName().toLowerCase(Locale.ROOT),
                        Function.identity()
                ));
        this.configuredProvider = normalizeProvider(configuredProvider);
    }

    public AiGenerateResponse generateSuggestions(String goal) {
        AiSuggestionProvider provider = providers.get(configuredProvider);

        if (provider == null) {
            throw new IllegalStateException(
                    "AI provider '%s' is not supported. Use one of: %s"
                            .formatted(configuredProvider, String.join(", ", providers.keySet()))
            );
        }

        return provider.generateSuggestions(goal);
    }

    public String getConfiguredProvider() {
        return configuredProvider;
    }

    private String normalizeProvider(String provider) {
        if (!StringUtils.hasText(provider)) {
            return "grok";
        }

        return provider.trim().toLowerCase(Locale.ROOT);
    }
}
