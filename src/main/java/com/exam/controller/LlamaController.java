package com.exam.controller;

import com.exam.dto.*;
import com.exam.service.LlamaService;
import com.exam.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/llama")
public class LlamaController {
    
    @Autowired
    private LlamaService llamaService;
    
    /**
     * Chat endpoint for exam-related topics using Llama 3
     * Similar to GPT chat but specialized for YDS, TOEFL, and IELTS
     */
    @PostMapping("/chat")
    public ResponseEntity<ResponseDto> chat(@RequestBody LlamaChatRequest request) {
        ResponseDto responseDto = new ResponseDto<>();
        
        try {
            // Validate request
            if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                Helper.fillResponse(responseDto, ResultCodes.BAD_REQUEST, "Prompt cannot be empty");
                return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
            }
            
            if (request.getExamType() == null || request.getExamType().trim().isEmpty()) {
                Helper.fillResponse(responseDto, ResultCodes.BAD_REQUEST, "Exam type must be specified (YDS, TOEFL, or IELTS)");
                return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
            }
            
            // Validate exam type
            String examType = request.getExamType().toUpperCase();
            if (!examType.equals("YDS") && !examType.equals("TOEFL") && !examType.equals("IELTS")) {
                Helper.fillResponse(responseDto, ResultCodes.BAD_REQUEST, "Exam type must be YDS, TOEFL, or IELTS");
                return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
            }
            
            // Set default values if not provided
            if (request.getLanguage() == null) {
                request.setLanguage("en");
            }
            if (request.getDifficulty() == null) {
                request.setDifficulty("intermediate");
            }
            if (request.getIncludeQuestionBank() == null) {
                request.setIncludeQuestionBank(true);
            }
            
            // Call Llama service
            LlamaChatResponse chatResponse = llamaService.chatWithLlama(request);
            
            responseDto.setResponseBody(Collections.singletonList(chatResponse));
            Helper.fillResponse(responseDto, ResultCodes.OK, null);
            
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
            
        } catch (Exception e) {
            Helper.fillResponse(responseDto, ResultCodes.INTERNAL_SERVER_ERROR, 
                               "Error processing chat request: " + e.getMessage());
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Check if Llama service is available
     */
    @GetMapping("/health")
    public ResponseEntity<ResponseDto> healthCheck() {
        ResponseDto responseDto = new ResponseDto<>();
        
        try {
            boolean isAvailable = llamaService.isServiceAvailable();
            String message = isAvailable ? "Llama service is available" : "Llama service is not available";
            
            responseDto.setResponseBody(Collections.singletonList(message));
            Helper.fillResponse(responseDto, isAvailable ? ResultCodes.OK : ResultCodes.SERVICE_UNAVAILABLE, null);
            
            return new ResponseEntity<>(responseDto, isAvailable ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE);
            
        } catch (Exception e) {
            Helper.fillResponse(responseDto, ResultCodes.INTERNAL_SERVER_ERROR, 
                               "Error checking service health: " + e.getMessage());
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get available models
     */
    @GetMapping("/models")
    public ResponseEntity<ResponseDto> getAvailableModels() {
        ResponseDto responseDto = new ResponseDto<>();
        
        try {
            List<String> models = llamaService.getAvailableModels();
            responseDto.setResponseBody(models);
            Helper.fillResponse(responseDto, ResultCodes.OK, null);
            
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
            
        } catch (Exception e) {
            Helper.fillResponse(responseDto, ResultCodes.INTERNAL_SERVER_ERROR, 
                               "Error retrieving models: " + e.getMessage());
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get exam-specific help and information
     */
    @GetMapping("/help/{examType}")
    public ResponseEntity<ResponseDto> getExamHelp(@PathVariable String examType) {
        ResponseDto responseDto = new ResponseDto<>();
        
        try {
            String examTypeUpper = examType.toUpperCase();
            String helpText = getExamHelpText(examTypeUpper);
            
            responseDto.setResponseBody(Collections.singletonList(helpText));
            Helper.fillResponse(responseDto, ResultCodes.OK, null);
            
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
            
        } catch (Exception e) {
            Helper.fillResponse(responseDto, ResultCodes.INTERNAL_SERVER_ERROR, 
                               "Error retrieving help: " + e.getMessage());
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private String getExamHelpText(String examType) {
        switch (examType) {
            case "YDS":
                return "YDS (Yabancı Dil Bilgisi Seviye Tespit Sınavı) Help:\n" +
                       "- Focus on Turkish-English translation\n" +
                       "- Grammar and vocabulary questions\n" +
                       "- Reading comprehension\n" +
                       "- Cloze test questions\n" +
                       "Ask me about specific grammar rules, vocabulary, or practice questions!";
                       
            case "TOEFL":
                return "TOEFL (Test of English as a Foreign Language) Help:\n" +
                       "- Academic English skills\n" +
                       "- Reading, Listening, Speaking, Writing sections\n" +
                       "- Integrated tasks\n" +
                       "- Academic vocabulary and grammar\n" +
                       "Ask me about test strategies, practice questions, or specific skills!";
                       
            case "IELTS":
                return "IELTS (International English Language Testing System) Help:\n" +
                       "- General and Academic modules\n" +
                       "- All four skills: Reading, Writing, Listening, Speaking\n" +
                       "- Task 1 and Task 2 writing\n" +
                       "- Academic and general vocabulary\n" +
                       "Ask me about test format, practice questions, or skill development!";
                       
            default:
                return "Please specify a valid exam type: YDS, TOEFL, or IELTS";
        }
    }
}

