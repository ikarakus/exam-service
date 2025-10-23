package com.exam.service;

import com.exam.dto.LlamaChatRequest;
import com.exam.dto.LlamaChatResponse;

public interface LlamaService {
    
    /**
     * Chat with Llama 3 for exam-related topics (YDS, TOEFL, IELTS)
     * @param request The chat request containing prompt, exam type, and other parameters
     * @return Llama chat response with exam-specific information
     */
    LlamaChatResponse chatWithLlama(LlamaChatRequest request);
    
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
}

