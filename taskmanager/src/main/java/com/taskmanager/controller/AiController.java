package com.taskmanager.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.model.AiGenerateRequest;
import com.taskmanager.model.AiGenerateResponse;
import com.taskmanager.service.AiSuggestionService;

@RestController
@RequestMapping("/ai")
@CrossOrigin
public class AiController {

    private final AiSuggestionService aiSuggestionService;

    public AiController(AiSuggestionService aiSuggestionService) {
        this.aiSuggestionService = aiSuggestionService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody AiGenerateRequest request) {
        if (request == null || !StringUtils.hasText(request.getGoal())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Goal is required"));
        }

        try {
            AiGenerateResponse response = aiSuggestionService.generate(request.getGoal(), request.getMode());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (IllegalStateException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("API key is not configured")) {
                return ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of("message", "Grok API key is not configured. Set GROK_API_KEY and restart the backend."));
            }

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "AI assistant is unavailable. Please try again later."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", "AI assistant is busy right now. Please try again."));
        }
    }
}
