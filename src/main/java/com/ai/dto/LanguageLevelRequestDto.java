package com.ai.dto;

import java.util.List;

public class LanguageLevelRequestDto {
    private String language;
    private List<SimpleMessageDto> conversation;

    // Getters and setters
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<SimpleMessageDto> getConversation() {
        return conversation;
    }

    public void setConversation(List<SimpleMessageDto> conversation) {
        this.conversation = conversation;
    }
}
