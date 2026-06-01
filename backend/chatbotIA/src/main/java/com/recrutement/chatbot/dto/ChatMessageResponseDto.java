package com.recrutement.chatbot.dto;

public class ChatMessageResponseDto {
    private String response;

    public ChatMessageResponseDto() {}

    public ChatMessageResponseDto(String response) {
        this.response = response;
    }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
}
