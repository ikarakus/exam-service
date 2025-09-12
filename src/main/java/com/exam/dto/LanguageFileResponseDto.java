package com.exam.dto;

public class LanguageFileResponseDto {
    // The language code of the file returned
    private String language;
    // The JSON content of the language file
    private String jsonContent;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }
}
