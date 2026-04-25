package com.example.ai;

import jakarta.validation.constraints.NotBlank;

public class ChatMessage {

    /**
     * 角色：user / assistant / system
     */
    @NotBlank
    private String role;

    @NotBlank
    private String content;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
