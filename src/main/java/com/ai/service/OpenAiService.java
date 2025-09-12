package com.ai.service;

import com.ai.dto.ChatResponse;
import com.ai.dto.MessageDto;
import com.ai.dto.SimpleMessageDto;
import com.ai.dto.SessionTestQuestionDto;

import java.util.List;
import java.util.Map;

public interface OpenAiService {

    // Common Endpoints
    ChatResponse callOpenAiApi(String modelName, String prompt, String language, String languageLevel, String topic, String tutor, List<MessageDto> pastDialogue);

    String evaluateLanguageLevel(String language, List<SimpleMessageDto> conversation);

    String translate(String inputLanguage, String outputLanguage, String message);

    // Mobile - JSON translation
    Map<String, Object> translateJsonFile(String englishJson, String targetLanguage);

    // Generate 10 session test questions using OpenAI
    List<SessionTestQuestionDto> generateSessionTestQuestions(String details);

    // Generate assessment questions with custom prompt and counts
    List<SessionTestQuestionDto> generateAssessmentQuestions(String details, int questionCount, int answerCount, boolean isAssessment, String courseLang, String userLang);

    // Generate speaking score and CEFR level from conversation
    String getSpeakingScoreAssessment(String prompt);
    
    /**
     * Validates that a course language exists and is active
     * @param languageCode the language code to validate
     * @throws IllegalArgumentException if the language is not found or not active
     */
    void validateCourseLanguage(String languageCode);

}

