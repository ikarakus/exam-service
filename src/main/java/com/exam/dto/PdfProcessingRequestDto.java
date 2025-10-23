package com.exam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfProcessingRequestDto {
    
    @NotBlank(message = "Course language is required")
    private String courseLang;
    
    @NotNull(message = "Level ID is required")
    @Min(value = 1, message = "Level ID must be at least 1")
    private Integer levelId;
    
    private Integer lessonId;
    
    @Min(value = 1, message = "Minimum question count is 1")
    @Max(value = 50, message = "Maximum question count is 50")
    private Integer questionCount = 10;
    
    @Min(value = 2, message = "Minimum answer count is 2")
    @Max(value = 6, message = "Maximum answer count is 6")
    private Integer answerCount = 5;
    
    private String userLang;
    
    private String topic;
    
    private String difficulty;
    
    private Boolean assessment = false;
    
    private String extractionMethod = "comprehensive"; // "comprehensive", "simple", "structured"
}

