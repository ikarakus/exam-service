package com.ai.dto;

import java.io.Serializable;

public class LanguageLevelResponseDto implements Serializable {
    private String languageLevel;

    // Getters and setters
    public String getLanguageLevel() {
        return languageLevel;
    }

    public void setLanguageLevel(String languageLevel) {
        this.languageLevel = languageLevel;
    }
}
