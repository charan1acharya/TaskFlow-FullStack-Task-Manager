package com.taskmanager.model;

import java.util.List;

public class AiGenerateResponse {

    private List<String> suggestions;
    private String rawText;

    public AiGenerateResponse() {}

    public AiGenerateResponse(List<String> suggestions, String rawText) {
        this.suggestions = suggestions;
        this.rawText = rawText;
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
}
