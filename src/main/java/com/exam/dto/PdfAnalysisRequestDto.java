package com.exam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfAnalysisRequestDto {
    
    @NotBlank(message = "PDF content is required")
    private String pdfContent;
    
    @NotBlank(message = "Course language is required")
    private String courseLang;
    
    @NotNull(message = "Level ID is required")
    private Integer levelId;
    
    private Integer lessonId;
    
    private Integer questionCount = 10;
    
    private Integer answerCount = 5;
    
    private String userLang;
    
    private String topic;
    
    private String difficulty;
    
    private Boolean assessment = false;
    
    private String analysisType = "comprehensive"; // "comprehensive", "simple", "structured"
    
    private String extractionPrompt;
}

