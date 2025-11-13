package com.exam.service;

import com.exam.dto.ChatResponse;
import com.exam.dto.MessageDto;

import java.util.List;

public interface LlamaService {
    
    /**
     * Chat with Llama 3 using the same interface as GPT chat
     * @param modelName Model name (for compatibility, but uses llama3)
     * @param prompt User's prompt
     * @param language Target language
     * @param languageLevel User's proficiency level
     * @param topic Conversation topic
     * @param tutor Tutor name
     * @param pastDialogue Previous conversation history
     * @param isChildFriendly Whether content should be child-friendly
     * @param userNickname User's nickname for personalization
     * @param ageRange User's age range
     * @param isFirstMessage Whether this is the first message
     * @param examType Exam type (YDS, TOEFL, IELTS) if applicable
     * @param levelId Level ID for question lookup
     * @param lessonId Lesson ID for question lookup
     * @param courseLang Course language for question lookup
     * @return Chat response in the same format as GPT chat
     */
    ChatResponse callLlamaApi(String modelName, String prompt, String language, String languageLevel, 
                             String topic, String tutor, List<MessageDto> pastDialogue, 
                             boolean isChildFriendly, String userNickname, String ageRange, 
                             boolean isFirstMessage, String examType, Long levelId, Long lessonId, String courseLang);
    
    /**
     * Check if Llama service is available
     * @return true if service is available, false otherwise
     */
    boolean isServiceAvailable();
    
    /**
     * Get available models from Llama service
     * @return List of available model names
     */
    java.util.List<String> getAvailableModels();
    
    /**
     * Validate course language
     * @param courseLang Language code to validate
     */
    void validateCourseLanguage(String courseLang);
    
    /**
     * Generate assessment questions
     * @param details Question details
     * @param questionCount Number of questions to generate
     * @param answerCount Number of answer options
     * @param isMultipleChoice Whether questions are multiple choice
     * @param courseLang Course language
     * @param targetLang Target language
     * @return List of generated questions
     */
    java.util.List<com.exam.dto.SessionTestQuestionDto> generateAssessmentQuestions(
        String details, int questionCount, int answerCount, boolean isMultipleChoice, 
        String courseLang, String targetLang);
    
    /**
     * Get speaking score assessment
     * @param prompt Assessment prompt
     * @return Assessment response
     */
    String getSpeakingScoreAssessment(String prompt);
}

