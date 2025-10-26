package com.exam.service;

import com.exam.dto.PdfProcessingRequestDto;
import com.exam.dto.PdfProcessingResponseDto;
import com.exam.dto.PdfAnalysisRequestDto;
import com.exam.dto.SimplePdfProcessingRequestDto;
import com.exam.dto.ManualQuestionRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PdfProcessingService {
    
    /**
     * Process a PDF file and extract questions to add to the question bank
     * @param file The PDF file to process
     * @param request The processing configuration
     * @return Processing response with extracted questions
     */
    PdfProcessingResponseDto processPdfFile(MultipartFile file, PdfProcessingRequestDto request);
    
    /**
     * Analyze PDF content text and extract questions (deprecated - use process-simple or add-manual)
     * @param request The analysis request with PDF content
     * @return Processing response with extracted questions
     */
    PdfProcessingResponseDto analyzePdfContent(PdfAnalysisRequestDto request);
    
    /**
     * Extract text from PDF file
     * @param file The PDF file
     * @return Extracted text content
     */
    String extractTextFromPdf(MultipartFile file);
    
    /**
     * Validate extracted questions
     * @param questions List of extracted questions
     * @return List of validated questions
     */
    List<PdfProcessingResponseDto.ExtractedQuestionDto> validateQuestions(List<PdfProcessingResponseDto.ExtractedQuestionDto> questions);
    
    /**
     * Save questions to the question bank
     * @param questions List of validated questions
     * @param request Original processing request
     * @return Number of questions successfully saved
     */
    int saveQuestionsToDatabase(List<PdfProcessingResponseDto.ExtractedQuestionDto> questions, PdfProcessingRequestDto request);
    
    /**
     * Process PDF file and extract questions using simple text parsing
     * @param file The PDF file to process
     * @param request The processing configuration
     * @return Processing response with extracted questions
     */
    PdfProcessingResponseDto processPdfFileSimple(MultipartFile file, SimplePdfProcessingRequestDto request);
    
    PdfProcessingResponseDto addManualQuestion(ManualQuestionRequestDto request);
}

