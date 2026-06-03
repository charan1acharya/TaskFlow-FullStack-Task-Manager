package com.taskmanager.model;

import java.util.List;

public class AiGenerateResponse {

    private List<String> suggestions;
    private String answer;
    private String rawText;
    private String provider;
    private String model;
    private String mode;

    public AiGenerateResponse() {}

    public AiGenerateResponse(List<String> suggestions, String rawText) {
        this(suggestions, null, rawText, "grok", null, "suggestions");
    }

    public AiGenerateResponse(List<String> suggestions, String rawText, String provider, String model) {
        this(suggestions, null, rawText, provider, model, "suggestions");
    }

    public AiGenerateResponse(List<String> suggestions, String answer, String rawText, String provider, String model, String mode) {
        this.suggestions = suggestions;
        this.answer = answer;
        this.rawText = rawText;
        this.provider = provider;
        this.model = model;
        this.mode = mode;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
