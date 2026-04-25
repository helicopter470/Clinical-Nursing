package com.example.ai;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class AiChatRequest {

    @NotBlank(message = "message不能为空")
    private String message;

    private List<com.example.ai.ChatMessage> history;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<com.example.ai.ChatMessage> getHistory() {
        return history;
    }

    public void setHistory(List<com.example.ai.ChatMessage> history) {
        this.history = history;
    }
}
