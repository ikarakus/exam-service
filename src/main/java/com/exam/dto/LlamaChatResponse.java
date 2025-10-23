package com.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LlamaChatResponse {
    
    private String response;
    private String examType;
    private String language;
    private String difficulty;
    private String topic;
    private List<QuestionReference> relatedQuestions; // Questions from question bank that are relevant
    private String modelUsed;
    private Long responseTime; // Response time in milliseconds
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionReference {
        private Long questionId;
        private String questionText;
        private String examType;
        private String topic;
        private String difficulty;
        private String relevance; // How relevant this question is to the user's query
    }
}

