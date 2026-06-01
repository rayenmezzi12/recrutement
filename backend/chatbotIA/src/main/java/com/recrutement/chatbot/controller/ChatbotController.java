package com.recrutement.chatbot.controller;

import com.recrutement.chatbot.dto.ChatMessageRequestDto;
import com.recrutement.chatbot.dto.ChatbotResponseDto;
import com.recrutement.chatbot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/ask")
    public ResponseEntity<ChatbotResponseDto> ask(@RequestBody ChatMessageRequestDto request) {
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body(new ChatbotResponseDto("Message vide."));
        }
        return ResponseEntity.ok(chatbotService.processMessage(request));
    }
}
