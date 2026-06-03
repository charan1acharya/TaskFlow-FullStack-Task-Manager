package com.taskmanager.service;

import com.taskmanager.model.AiGenerateResponse;

public interface AiSuggestionProvider {

    String providerName();

    AiGenerateResponse generateSuggestions(String goal);

    AiGenerateResponse solveProblem(String problem);
}
