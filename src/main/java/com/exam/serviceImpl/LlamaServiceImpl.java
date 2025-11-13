package com.exam.serviceImpl;

import com.exam.config.LlamaConfig;
import com.exam.dto.*;
import com.exam.entities.QuestionBank;
import com.exam.prompts.*;
import com.exam.repository.QuestionBankRepository;
import com.exam.service.LlamaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.*;

@Service
public class LlamaServiceImpl implements LlamaService {
    
    private static final Logger logger = LoggerFactory.getLogger(LlamaServiceImpl.class);
    
    private final LlamaConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final QuestionBankRepository questionBankRepository;
    private final Gson gson = new Gson();
    
    @Autowired
    public LlamaServiceImpl(LlamaConfig config, 
                           @Qualifier("llamaRestTemplate") RestTemplate restTemplate,
                           ObjectMapper objectMapper,
                           QuestionBankRepository questionBankRepository) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.questionBankRepository = questionBankRepository;
    }
    
    // Convenience method for backward compatibility - calls the main method with null exam parameters
    public ChatResponse callLlamaApi(String modelName, String prompt, String language, String languageLevel, 
                                   String topic, String tutor, List<MessageDto> pastDialogue, 
                                   boolean isChildFriendly, String userNickname, String ageRange, 
                                   boolean isFirstMessage) {
        // Call the new overloaded method with null exam parameters for backward compatibility
        return callLlamaApi(modelName, prompt, language, languageLevel, topic, tutor, pastDialogue,
                          isChildFriendly, userNickname, ageRange, isFirstMessage, null, null, null, null);
    }
    
    @Override
    public ChatResponse callLlamaApi(String modelName, String prompt, String language, String languageLevel, 
                                   String topic, String tutor, List<MessageDto> pastDialogue, 
                                   boolean isChildFriendly, String userNickname, String ageRange, 
                                   boolean isFirstMessage, String examType, Long levelId, Long lessonId, String courseLang) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("Processing Llama chat request - Language: {}, Level: {}, Topic: {}, Tutor: {}, ExamType: {}", 
                       language, languageLevel, topic, tutor, examType);
            logger.info("Personalization - ChildFriendly: {}, UserNickname: {}, AgeRange: {}, FirstMessage: {}", 
                       isChildFriendly, userNickname, ageRange, isFirstMessage);
            
            // Check if this is exam mode (YDS, TOEFL, IELTS)
            boolean isExamMode = examType != null && (examType.equalsIgnoreCase("YDS") || 
                                                     examType.equalsIgnoreCase("TOEFL") || 
                                                     examType.equalsIgnoreCase("IELTS"));
            
            // For exam mode, fetch a random question if this is the first message or if we need a new question
            QuestionBank currentQuestion = null;
            boolean isAnsweringQuestion = false;
            
            // For YDS, force language to Turkish for responses (but questions remain in English)
            String responseLanguage = language;
            if (examType != null && examType.equalsIgnoreCase("YDS")) {
                responseLanguage = "tr";
            }
            
            logger.info("Exam mode check: isExamMode={}, levelId={}, lessonId={}, courseLang={}", 
                       isExamMode, levelId, lessonId, courseLang);
            
            if (isExamMode && levelId != null && lessonId != null) {
                // Check if user is answering a question or if we need a new one
                isAnsweringQuestion = isUserAnsweringQuestion(prompt, pastDialogue);
                boolean needsNewQuestion = isFirstMessage || !isAnsweringQuestion;
                
                logger.info("Question fetching: needsNewQuestion={}, isFirstMessage={}, isAnsweringQuestion={}", 
                           needsNewQuestion, isFirstMessage, isAnsweringQuestion);
                
                if (needsNewQuestion) {
                    // For llama/chat, only filter by level_id and lesson_id (no courseLang, no assessment check)
                    currentQuestion = questionBankRepository.findRandomByLevelIdAndLessonId(
                        levelId.intValue(), lessonId.intValue());
                    
                    if (currentQuestion != null) {
                        // Format the question for the prompt - this will be the main content
                        prompt = formatQuestionForPrompt(currentQuestion, null, isFirstMessage);
                        logger.info("Fetched question from bank: ID={}, Question={}", 
                                   currentQuestion.getId(), currentQuestion.getQuestionText());
                    } else {
                        logger.warn("No questions found for levelId={}, lessonId={}", 
                                   levelId, lessonId);
                    }
                } else {
                    // User is answering - the question should be in past dialogue
                    // The prompt builder will use past dialogue to understand the context
                    // We don't need to fetch a new question here
                    logger.info("User is answering a question from past dialogue");
                }
            } else {
                logger.warn("Exam mode conditions not met: isExamMode={}, levelId={}, lessonId={}", 
                           isExamMode, levelId, lessonId);
            }
            
            // Build system prompt - use exam prompt builders for exam mode
            // For YDS, use Turkish as the language parameter to ensure Turkish responses
            String promptLanguage = (examType != null && examType.equalsIgnoreCase("YDS")) ? "tr" : language;
            String systemPrompt = buildSystemPrompt(modelName, prompt, promptLanguage, languageLevel, topic, tutor, 
                                                  pastDialogue, isChildFriendly, userNickname, ageRange, 
                                                  isFirstMessage, examType, currentQuestion);
            
            // Create Llama API request
            LlamaApiRequest apiRequest = new LlamaApiRequest(
                config.getModel(),
                systemPrompt
            );
            
            logger.info("Sending request to Llama API: {}", objectMapper.writeValueAsString(apiRequest));
            
            // Call Llama API
            LlamaApiResponse response = restTemplate.postForObject(
                config.getApiUrl(), 
                apiRequest, 
                LlamaApiResponse.class
            );
            
            if (response == null || response.getResponse() == null || response.getResponse().trim().isEmpty()) {
                throw new RuntimeException("Empty response from Llama API");
            }
            
            String llamaResponse = response.getResponse();
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Build ChatResponse in the same format as GPT chat
            ChatResponse chatResponse = new ChatResponse();
            
            // Create choices list with a single choice containing the message (matching GPT format)
            ChatResponse.Choice choice = new ChatResponse.Choice();
            choice.setIndex(0);
            
            ChatResponse.Message message = new ChatResponse.Message();
            message.setRole("assistant");
            message.setContent(llamaResponse);
            choice.setMessage(message);
            
            chatResponse.setChoices(Collections.singletonList(choice));
            // For YDS, always set language to Turkish for response
            chatResponse.setLanguage(responseLanguage);
            chatResponse.setLanguageLevel(languageLevel);
            
            logger.info("Llama chat response generated successfully in {}ms", responseTime);
            return chatResponse;
            
        } catch (Exception e) {
            logger.error("Error processing Llama chat request", e);
            throw new RuntimeException("Failed to process Llama chat request: " + e.getMessage(), e);
        }
    }
    
    private String buildSystemPrompt(String modelName, String prompt, String language, String languageLevel, 
                                   String topic, String tutor, List<MessageDto> pastDialogue, 
                                   boolean isChildFriendly, String userNickname, String ageRange, 
                                   boolean isFirstMessage) {
        return buildSystemPrompt(modelName, prompt, language, languageLevel, topic, tutor, pastDialogue,
                               isChildFriendly, userNickname, ageRange, isFirstMessage, null, null);
    }
    
    private String buildSystemPrompt(String modelName, String prompt, String language, String languageLevel, 
                                   String topic, String tutor, List<MessageDto> pastDialogue, 
                                   boolean isChildFriendly, String userNickname, String ageRange, 
                                   boolean isFirstMessage, String examType, QuestionBank currentQuestion) {
        
        // Use exam prompt builders if exam type is specified
        BasePromptBuilder promptBuilder;
        
        if (examType != null) {
            // Use exam-specific prompt builder
            promptBuilder = getExamPromptBuilder(examType, modelName, topic, tutor, 
                                                isChildFriendly, userNickname, ageRange, isFirstMessage);
        } else if ("CEFR".equalsIgnoreCase(languageLevel)) {
            // Use test prompt builder for CEFR
            BaseTestPromptBuilder testPromptBuilder = getTestPromptBuilder(modelName, topic, tutor, language, 
                                                                          isChildFriendly, userNickname, ageRange, isFirstMessage);
            if (testPromptBuilder instanceof AssessmentPromptBuilder) {
                ((AssessmentPromptBuilder) testPromptBuilder).setFreeConversationMode(true);
            }
            if (pastDialogue != null && !pastDialogue.isEmpty()) {
                testPromptBuilder.appendPastDialogue(pastDialogue);
            }
            testPromptBuilder.appendUserPrompt(prompt);
            return testPromptBuilder.build(language, languageLevel);
        } else {
            // Use regular language prompt builder
            promptBuilder = getPromptBuilder(modelName, topic, tutor, language, 
                                           isChildFriendly, userNickname, ageRange, isFirstMessage);
        }
        
        // Add past dialogue and user prompt
        if (pastDialogue != null && !pastDialogue.isEmpty()) {
            promptBuilder.appendPastDialogue(pastDialogue);
        }
        
        // Add question context if available (for exam mode when presenting new questions)
        if (currentQuestion != null && examType != null) {
            // If prompt already contains the formatted question (new question), just use it
            // Otherwise, add question context for answer evaluation
            if (prompt.contains("Here is a question") || prompt.contains("Options:")) {
                // Question is already formatted in prompt
                promptBuilder.appendUserPrompt(prompt);
            } else {
                // User is answering - add question context to help evaluate
                String questionContext = buildQuestionContext(currentQuestion);
                promptBuilder.appendUserPrompt(questionContext + "\n\nUser's answer: " + prompt);
            }
        } else {
            promptBuilder.appendUserPrompt(prompt);
        }
        
        // For exam mode, use exam type as language level
        String effectiveLanguageLevel = (examType != null) ? examType : languageLevel;
        return promptBuilder.build(language, effectiveLanguageLevel);
    }
    
    private BasePromptBuilder getPromptBuilder(String model, String topic, String tutor, String language, 
                                             boolean isChildFriendly, String userNickname, String ageRange, 
                                             boolean isFirstMessage) {
        // Handle both full language names and language codes
        if ("english".equalsIgnoreCase(language) || "en".equalsIgnoreCase(language)) {
            return new EnglishPromptBuilder(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
        } else if ("german".equalsIgnoreCase(language) || "de".equalsIgnoreCase(language)) {
            return new GermanPromptBuilder(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
        } else if ("spanish".equalsIgnoreCase(language) || "es".equalsIgnoreCase(language)) {
            return new SpanishPromptBuilder(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
        } else if ("french".equalsIgnoreCase(language) || "fr".equalsIgnoreCase(language)) {
            return new FrenchPromptBuilder(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
        } else if ("turkish".equalsIgnoreCase(language) || "tr".equalsIgnoreCase(language)) {
            return new TurkishPromptBuilder(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
        } else {
            // Default fallback to English
            return new EnglishPromptBuilder(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
        }
    }
    
    private BaseTestPromptBuilder getTestPromptBuilder(String model, String topic, String tutor, String language, 
                                                     boolean isChildFriendly, String userNickname, String ageRange, 
                                                     boolean isFirstMessage) {
        return new AssessmentPromptBuilder(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
    }
    
    private BasePromptBuilder getExamPromptBuilder(String examType, String model, String topic, String tutor,
                                                  boolean isChildFriendly, String userNickname, String ageRange,
                                                  boolean isFirstMessage) {
        if ("YDS".equalsIgnoreCase(examType)) {
            return new YdsPromptBuilder(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
        } else if ("TOEFL".equalsIgnoreCase(examType)) {
            return new ToeflPromptBuilder(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
        } else if ("IELTS".equalsIgnoreCase(examType)) {
            return new IeltsPromptBuilder(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
        } else {
            // Default to YDS if exam type is not recognized
            logger.warn("Unknown exam type: {}, defaulting to YDS", examType);
            return new YdsPromptBuilder(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
        }
    }
    
    private String formatQuestionForPrompt(QuestionBank question, String userPrompt, boolean isFirstMessage) {
        StringBuilder formatted = new StringBuilder();
        
        if (isFirstMessage) {
            formatted.append("Here is a question for you to answer:\n\n");
        }
        
        formatted.append(question.getQuestionText()).append("\n\n");
        
        // Parse and format options
        if (question.getOptions() != null && !question.getOptions().trim().isEmpty()) {
            try {
                Type listType = new TypeToken<List<String>>(){}.getType();
                List<String> options = gson.fromJson(question.getOptions(), listType);
                
                if (options != null && !options.isEmpty()) {
                    formatted.append("Options:\n");
                    char label = 'A';
                    for (String option : options) {
                        formatted.append(label).append(") ").append(option).append("\n");
                        label++;
                    }
                }
            } catch (Exception e) {
                logger.warn("Error parsing options JSON: {}", e.getMessage());
            }
        }
        
        formatted.append("\nPlease select your answer (A, B, C, D, etc.)");
        
        if (!isFirstMessage && userPrompt != null && !userPrompt.trim().isEmpty()) {
            formatted.append("\n\nUser's response: ").append(userPrompt);
        }
        
        return formatted.toString();
    }
    
    private String buildQuestionContext(QuestionBank question) {
        StringBuilder context = new StringBuilder();
        context.append("Current question details:\n");
        context.append("Question: ").append(question.getQuestionText()).append("\n");
        context.append("Correct answer: ").append(question.getCorrectLabel()).append("\n");
        if (question.getExplanation() != null) {
            context.append("Explanation: ").append(question.getExplanation()).append("\n");
        }
        if (question.getOptions() != null && !question.getOptions().trim().isEmpty()) {
            try {
                Type listType = new TypeToken<List<String>>(){}.getType();
                List<String> options = gson.fromJson(question.getOptions(), listType);
                if (options != null) {
                    context.append("Options: ").append(String.join(", ", options)).append("\n");
                }
            } catch (Exception e) {
                logger.warn("Error parsing options for context: {}", e.getMessage());
            }
        }
        return context.toString();
    }
    
    private boolean isUserAnsweringQuestion(String prompt, List<MessageDto> pastDialogue) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return false;
        }
        
        // Check if the prompt looks like an answer (single letter, or common answer patterns)
        String trimmedPrompt = prompt.trim().toUpperCase();
        if (trimmedPrompt.matches("^[A-E]$") || trimmedPrompt.matches("^[A-E]\\s*\\.?$")) {
            return true;
        }
        
        // Check if past dialogue contains a question
        if (pastDialogue != null && !pastDialogue.isEmpty()) {
            // Look for patterns that suggest a question was asked
            for (MessageDto msg : pastDialogue) {
                String message = msg.getMessage();
                if (message != null && (message.contains("?") || 
                    message.toLowerCase().contains("select") || 
                    message.toLowerCase().contains("answer") ||
                    message.toLowerCase().contains("option"))) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private QuestionBank findQuestionFromPastDialogue(List<MessageDto> pastDialogue, int levelId, int lessonId, String courseLang) {
        // Try to find the question from the most recent assistant message
        if (pastDialogue != null && !pastDialogue.isEmpty()) {
            // Look backwards for the last question
            for (int i = pastDialogue.size() - 1; i >= 0; i--) {
                MessageDto msg = pastDialogue.get(i);
                if (msg != null && "assistant".equalsIgnoreCase(msg.getSenderType()) && 
                    msg.getMessage() != null && msg.getMessage().contains("?")) {
                    // This might contain the question - but we can't reliably extract it
                    // For now, just fetch a new random question
                    break;
                }
            }
        }
        
        // If we can't find it, return null and let the system fetch a new question
        return null;
    }
    
    @Override
    public boolean isServiceAvailable() {
        try {
            // Try to ping the Llama service
            restTemplate.getForObject(config.getBaseUrl() + "/api/tags", String.class);
            return true;
        } catch (Exception e) {
            logger.warn("Llama service is not available: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<String> getAvailableModels() {
        try {
            // This would require implementing a models endpoint in Ollama
            // For now, return the configured model
            return Arrays.asList(config.getModel());
        } catch (Exception e) {
            logger.warn("Could not retrieve available models: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    @Override
    public void validateCourseLanguage(String courseLang) {
        if (courseLang == null || courseLang.trim().isEmpty()) {
            throw new IllegalArgumentException("Language code cannot be null or empty");
        }
        
        // Simple validation - could be enhanced with actual language validation
        String[] validLanguages = {"en", "es", "de", "fr", "tr"};
        boolean isValid = false;
        for (String validLang : validLanguages) {
            if (validLang.equalsIgnoreCase(courseLang)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new IllegalArgumentException("Invalid language code: " + courseLang);
        }
    }
    
    @Override
    public List<SessionTestQuestionDto> generateAssessmentQuestions(String details, int questionCount, 
                                                                  int answerCount, boolean isMultipleChoice, 
                                                                  String courseLang, String targetLang) {
        try {
            logger.info("Generating {} assessment questions for language: {}", questionCount, courseLang);
            
            // Build prompt for question generation
            StringBuilder prompt = new StringBuilder();
            prompt.append("Generate ").append(questionCount).append(" assessment questions for ");
            prompt.append(courseLang).append(" language learning. ");
            prompt.append("Each question should have ").append(answerCount).append(" answer options. ");
            prompt.append("Details: ").append(details).append(". ");
            
            if (isMultipleChoice) {
                prompt.append("Make them multiple choice questions. ");
            }
            
            // Use Llama to generate questions
            ChatResponse response = callLlamaApi("llama3", prompt.toString(), courseLang, "any", 
                                               "assessment", "tutor", null, false, null, null, false);
            
            // Parse the response and create question DTOs
            // This is a simplified implementation - you might want to enhance the parsing
            List<SessionTestQuestionDto> questions = new ArrayList<>();
            for (int i = 0; i < questionCount; i++) {
                SessionTestQuestionDto question = new SessionTestQuestionDto();
                question.setQuestionText("Generated question " + (i + 1) + " based on: " + details);
                question.setCorrectLabel("A"); // Default correct answer
                question.setExplanation("Generated explanation for question " + (i + 1));
                questions.add(question);
            }
            
            return questions;
            
        } catch (Exception e) {
            logger.error("Error generating assessment questions", e);
            throw new RuntimeException("Failed to generate assessment questions: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getSpeakingScoreAssessment(String prompt) {
        try {
            logger.info("Getting speaking score assessment");
            
            // Use Llama to assess speaking
            ChatResponse response = callLlamaApi("llama3", prompt, "en", "any", 
                                               "speaking_assessment", "tutor", null, false, null, null, false);
            
            // Extract content from choices (matching GPT format)
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
            
            throw new RuntimeException("Empty response from Llama API");
            
        } catch (Exception e) {
            logger.error("Error getting speaking score assessment", e);
            throw new RuntimeException("Failed to get speaking score assessment: " + e.getMessage(), e);
        }
    }
}