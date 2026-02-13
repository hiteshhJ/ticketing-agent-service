package com.sainsburys.agent.controller;

import com.sainsburys.agent.model.ChatRequest;
import com.sainsburys.agent.model.ChatResponse;
import com.sainsburys.agent.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final AgentService agentService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "ticketing-agent-service"
        ));
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
            @Valid @RequestBody ChatRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        log.info("💬 User ({}): {}", userEmail, request.getMessage());

        try {
            ChatResponse response = agentService.runAgent(
                    request.getMessage(),
                    request.getConversationHistory()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Chat error", e);
            return ResponseEntity.internalServerError()
                    .body(ChatResponse.builder()
                            .response("Failed to process chat request: " + e.getMessage())
                            .build());
        }
    }
}
