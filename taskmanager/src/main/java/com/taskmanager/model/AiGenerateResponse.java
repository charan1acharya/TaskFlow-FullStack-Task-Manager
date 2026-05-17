package com.taskmanager.model;

import java.util.List;

public class AiGenerateResponse {

    private List<String> suggestions;
    private String rawText;
    private String provider;
    private String model;

    public AiGenerateResponse() {}

    public AiGenerateResponse(List<String> suggestions, String rawText) {
        this(suggestions, rawText, "grok", null);
    }

    public AiGenerateResponse(List<String> suggestions, String rawText, String provider, String model) {
        this.suggestions = suggestions;
        this.rawText = rawText;
        this.provider = provider;
        this.model = model;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
