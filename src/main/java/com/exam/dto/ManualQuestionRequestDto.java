package com.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualQuestionRequestDto {
    private String questionText;
    private List<OptionDto> options;
    private String correctLabel;
    private String explanation;
    private String courseLang;
    private Integer levelId;
    private Integer lessonId;
    private Boolean assessment = false;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionDto {
        private String label;
        private String text;
    }
}
