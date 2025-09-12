package com.ai.dto;

public class SttRequestDto {
    private byte[] audioContent;
    private String language;

    // Getters and Setters
    public byte[] getAudioContent() {
        return audioContent;
    }

    public void setAudioContent(byte[] audioContent) {
        this.audioContent = audioContent;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
