package com.ai.service;

import com.ai.dto.CreateLevelQuestionBankRequestDto;
import com.ai.dto.CreateQuestionBankRequestDto;
import com.ai.dto.SessionTestQuestionDto;
import com.ai.entities.QuestionBank;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface QuestionBankService {
    
    /**
     * Create question bank for a specific level and course language
     * @param request The request containing courseLang and levelId
     * @return List of created questions
     */
    List<QuestionBank> createQuestionBank(CreateQuestionBankRequestDto request);
    
    /**
     * Get questions from question bank by level, lesson, and course language
     * @param levelId The level ID
     * @param lessonId The lesson ID
     * @param courseLang The course language
     * @param isAssessment Whether this is an assessment test
     * @return List of questions
     */
    List<QuestionBank> getQuestionsByLevelAndLessonAndLanguage(Integer levelId, Integer lessonId, String courseLang, boolean isAssessment);
    
    /**
     * Convert QuestionBank entities to SessionTestQuestionDto objects
     * @param questionBanks List of QuestionBank entities
     * @return List of SessionTestQuestionDto objects
     */
    List<SessionTestQuestionDto> convertToSessionTestQuestionDtos(List<QuestionBank> questionBanks);
    
    /**
     * Create question bank for all lessons in a level (runs in background)
     * @param request The request containing courseLang and levelId
     * @return CompletableFuture that completes when all questions are generated
     */
    CompletableFuture<String> createLevelQuestionBank(CreateLevelQuestionBankRequestDto request);
} 
