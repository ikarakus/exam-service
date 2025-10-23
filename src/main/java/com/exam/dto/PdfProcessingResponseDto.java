package com.exam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfProcessingResponseDto {
    
    private boolean success;
    private String message;
    private int totalQuestionsExtracted;
    private int questionsAddedToDatabase;
    private List<ExtractedQuestionDto> extractedQuestions;
    private Map<String, Object> processingStats;
    private String errorDetails;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtractedQuestionDto {
        private String questionText;
        private List<OptionDto> options;
        private String correctLabel;
        private String explanation;
        private String difficulty;
        private String topic;
        private Boolean isValid;
        private String validationError;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OptionDto {
            private String label;
            private String text;
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessingStats {
        private long processingTimeMs;
        private int pdfPagesProcessed;
        private int textLength;
        private String extractionMethod;
        private int openaiApiCalls;
        private double confidenceScore;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessingSummaryDto {
        private int totalQuestionsExtracted;
        private int validQuestions;
        private int invalidQuestions;
        private int questionsSavedToDatabase;
        private int questionsSkipped;
        private List<String> validationErrors;
        private Map<String, Object> processingStats;
    }
}

