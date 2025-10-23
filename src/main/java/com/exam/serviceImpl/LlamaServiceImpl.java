package com.exam.serviceImpl;

import com.exam.config.LlamaConfig;
import com.exam.dto.*;
import com.exam.entities.QuestionBank;
import com.exam.repository.QuestionBankRepository;
import com.exam.service.LlamaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LlamaServiceImpl implements LlamaService {
    
    private static final Logger logger = LoggerFactory.getLogger(LlamaServiceImpl.class);
    
    private final LlamaConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final QuestionBankRepository questionBankRepository;
    
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
    
    @Override
    public LlamaChatResponse chatWithLlama(LlamaChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("Processing Llama chat request for exam type: {}, topic: {}", 
                       request.getExamType(), request.getTopic());
            
            // Build system prompt based on exam type and requirements
            String systemPrompt = buildSystemPrompt(request);
            
            // Build user prompt with context from question bank if requested
            String userPrompt = buildUserPrompt(request);
            
            // Combine system prompt with user prompt
            String fullPrompt = systemPrompt + "\n\nUser: " + userPrompt + "\n\nAssistant:";
            
            // Create Llama API request
            LlamaApiRequest apiRequest = new LlamaApiRequest(
                config.getModel(),
                fullPrompt
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
            
            // Get related questions from question bank
            List<LlamaChatResponse.QuestionReference> relatedQuestions = 
                getRelatedQuestions(request);
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Build response
            LlamaChatResponse chatResponse = new LlamaChatResponse();
            chatResponse.setResponse(llamaResponse);
            chatResponse.setExamType(request.getExamType());
            chatResponse.setLanguage(request.getLanguage());
            chatResponse.setDifficulty(request.getDifficulty());
            chatResponse.setTopic(request.getTopic());
            chatResponse.setRelatedQuestions(relatedQuestions);
            chatResponse.setModelUsed(config.getModel());
            chatResponse.setResponseTime(responseTime);
            
            logger.info("Llama chat response generated successfully in {}ms", responseTime);
            return chatResponse;
            
        } catch (Exception e) {
            logger.error("Error processing Llama chat request", e);
            throw new RuntimeException("Failed to process Llama chat request: " + e.getMessage(), e);
        }
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
    
    private String buildSystemPrompt(LlamaChatRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an expert language tutor specializing in ");
        prompt.append(request.getExamType()).append(" exam preparation. ");
        
        // Add exam-specific context
        switch (request.getExamType().toUpperCase()) {
            case "YDS":
                prompt.append("YDS (Yabancı Dil Bilgisi Seviye Tespit Sınavı) is a Turkish language proficiency test. ");
                prompt.append("Focus on Turkish-English translation, grammar, vocabulary, and reading comprehension. ");
                break;
            case "TOEFL":
                prompt.append("TOEFL (Test of English as a Foreign Language) is an English proficiency test. ");
                prompt.append("Focus on academic English, reading, listening, speaking, and writing skills. ");
                break;
            case "IELTS":
                prompt.append("IELTS (International English Language Testing System) is an English proficiency test. ");
                prompt.append("Focus on general and academic English, all four skills: reading, writing, listening, and speaking. ");
                break;
            default:
                prompt.append("Focus on general language learning and exam preparation. ");
        }
        
        prompt.append("Your knowledge is limited to these exam topics and related language learning materials. ");
        prompt.append("Provide helpful, accurate, and exam-focused responses. ");
        prompt.append("If asked about topics outside of these exams, politely redirect to exam-related content. ");
        
        if (request.getDifficulty() != null) {
            prompt.append("Adjust your language level to ").append(request.getDifficulty()).append(" level. ");
        }
        
        return prompt.toString();
    }
    
    private String buildUserPrompt(LlamaChatRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        // Add past dialogue context if available
        if (request.getPastDialogue() != null && !request.getPastDialogue().isEmpty()) {
            prompt.append("Previous conversation context:\n");
            for (MessageDto message : request.getPastDialogue()) {
                prompt.append(message.getSenderNickname()).append(": ")
                      .append(message.getMessage()).append("\n");
            }
            prompt.append("\n");
        }
        
        // Add current prompt
        prompt.append("Current question: ").append(request.getPrompt());
        
        // Add topic context if specified
        if (request.getTopic() != null && !request.getTopic().equals("any")) {
            prompt.append("\n\nPlease focus on the topic: ").append(request.getTopic());
        }
        
        return prompt.toString();
    }
    
    
    private List<LlamaChatResponse.QuestionReference> getRelatedQuestions(LlamaChatRequest request) {
        if (!request.getIncludeQuestionBank()) {
            return Collections.emptyList();
        }
        
        try {
            // Get questions from question bank based on exam type and topic
            List<QuestionBank> questions = questionBankRepository.findAll();
            
            return questions.stream()
                .filter(q -> isRelevantQuestion(q, request))
                .limit(5) // Limit to 5 most relevant questions
                .map(this::convertToQuestionReference)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.warn("Could not retrieve related questions: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    private boolean isRelevantQuestion(QuestionBank question, LlamaChatRequest request) {
        // Simple relevance check based on exam type and topic
        String questionText = question.getQuestionText().toLowerCase();
        String prompt = request.getPrompt().toLowerCase();
        
        // Check if question text contains keywords from the prompt
        String[] promptWords = prompt.split("\\s+");
        int matchCount = 0;
        
        for (String word : promptWords) {
            if (word.length() > 3 && questionText.contains(word)) {
                matchCount++;
            }
        }
        
        // Consider relevant if at least 2 words match
        return matchCount >= 2;
    }
    
    private LlamaChatResponse.QuestionReference convertToQuestionReference(QuestionBank question) {
        LlamaChatResponse.QuestionReference reference = new LlamaChatResponse.QuestionReference();
        reference.setQuestionId(question.getId());
        reference.setQuestionText(question.getQuestionText());
        reference.setExamType(question.getCourseLang()); // Using courseLang as exam type
        reference.setTopic("general"); // Could be enhanced with more specific topic mapping
        reference.setDifficulty("intermediate"); // Could be enhanced with level mapping
        reference.setRelevance("high"); // Could be enhanced with actual relevance scoring
        
        return reference;
    }
}
