package com.ai.dto;

import lombok.Data;

@Data
public class TranslationRequestDto {
    private String inputLanguage;
    private String outputLanguage;
    private String message;
}
