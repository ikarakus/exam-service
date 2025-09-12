package com.ai.dto;

public class LanguageFileRequestDto {
    // The language code requested by the mobile app (e.g. "fr", "es", etc.)
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
