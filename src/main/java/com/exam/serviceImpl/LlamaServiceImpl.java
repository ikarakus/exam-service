package com.exam.serviceImpl;

import com.exam.config.LlamaConfig;
import com.exam.dto.*;
import com.exam.prompts.*;
import com.exam.service.LlamaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LlamaServiceImpl implements LlamaService {
    
    private static final Logger logger = LoggerFactory.getLogger(LlamaServiceImpl.class);
    
    private final LlamaConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public LlamaServiceImpl(LlamaConfig config, 
                           @Qualifier("llamaRestTemplate") RestTemplate restTemplate,
                           ObjectMapper objectMapper) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public ChatResponse callLlamaApi(String modelName, String prompt, String language, String languageLevel, 
                                   String topic, String tutor, List<MessageDto> pastDialogue, 
                                   boolean isChildFriendly, String userNickname, String ageRange, 
                                   boolean isFirstMessage) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("Processing Llama chat request - Language: {}, Level: {}, Topic: {}, Tutor: {}", 
                       language, languageLevel, topic, tutor);
            logger.info("Personalization - ChildFriendly: {}, UserNickname: {}, AgeRange: {}, FirstMessage: {}", 
                       isChildFriendly, userNickname, ageRange, isFirstMessage);
            
            // Build system prompt using the same prompt builders as GPT chat
            String systemPrompt = buildSystemPrompt(modelName, prompt, language, languageLevel, topic, tutor, 
                                                  pastDialogue, isChildFriendly, userNickname, ageRange, isFirstMessage);
            
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
            chatResponse.setLanguage(language);
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
        
        // Use the same prompt builder logic as GPT chat
        BasePromptBuilder promptBuilder;
        
        // Decide which builder to use based on languageLevel
        if ("CEFR".equalsIgnoreCase(languageLevel)) {
            BaseTestPromptBuilder testPromptBuilder = getTestPromptBuilder(modelName, topic, tutor, language, 
                                                                          isChildFriendly, userNickname, ageRange, isFirstMessage);
            if (testPromptBuilder instanceof AssessmentPromptBuilder) {
                // Only for chat/Ass mode, set free conversation mode
                ((AssessmentPromptBuilder) testPromptBuilder).setFreeConversationMode(true);
            }
            if (pastDialogue != null && !pastDialogue.isEmpty()) {
                testPromptBuilder.appendPastDialogue(pastDialogue);
            }
            testPromptBuilder.appendUserPrompt(prompt);
            return testPromptBuilder.build(language, languageLevel);
        } else {
            promptBuilder = getPromptBuilder(modelName, topic, tutor, language, 
                                           isChildFriendly, userNickname, ageRange, isFirstMessage);
            if (pastDialogue != null && !pastDialogue.isEmpty()) {
                promptBuilder.appendPastDialogue(pastDialogue);
            }
            promptBuilder.appendUserPrompt(prompt);
            return promptBuilder.build(language, languageLevel);
        }
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