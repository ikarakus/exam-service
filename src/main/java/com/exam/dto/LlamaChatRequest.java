package com.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class LlamaChatRequest {
    
    private String prompt;
    private String examType; // YDS, TOEFL, IELTS
    private String language = "en"; // Default to English
    private String difficulty = "intermediate"; // beginner, intermediate, advanced
    private String topic; // grammar, vocabulary, reading, listening, writing, speaking
    private List<MessageDto> pastDialogue;
    private Long userId;
    private Boolean includeQuestionBank = true; // Whether to include questions from question bank
    
    // Constructor for basic requests
    public LlamaChatRequest() {}
    
    // Constructor with exam type
    public LlamaChatRequest(String prompt, String examType) {
        this.prompt = prompt;
        this.examType = examType;
    }
    
    // Full constructor
    public LlamaChatRequest(String prompt, String examType, String language, 
                           String difficulty, String topic, List<MessageDto> pastDialogue, 
                           Long userId, Boolean includeQuestionBank) {
        this.prompt = prompt;
        this.examType = examType;
        this.language = language;
        this.difficulty = difficulty;
        this.topic = topic;
        this.pastDialogue = pastDialogue;
        this.userId = userId;
        this.includeQuestionBank = includeQuestionBank;
    }
}

