package com.exam.serviceImpl;

import com.exam.dto.*;
import com.exam.entities.QuestionBank;
import com.exam.repository.QuestionBankRepository;
import com.exam.service.LlamaService;
import com.exam.service.PdfProcessingService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.lang.reflect.Type;

@Service
public class PdfProcessingServiceImpl implements PdfProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(PdfProcessingServiceImpl.class);
    
    @Autowired
    private LlamaService llamaService;
    
    @Autowired
    private QuestionBankRepository questionBankRepository;
    
    @Autowired
    private Gson gson;
    
    private final Tika tika = new Tika();

    @Override
    public PdfProcessingResponseDto processPdfFile(MultipartFile file, PdfProcessingRequestDto request) {
        PdfProcessingResponseDto response = new PdfProcessingResponseDto();
        
        try {
            // This method is deprecated - use /api/pdf/process-simple or /api/pdf/add-manual instead
            response.setSuccess(false);
            response.setMessage("This endpoint is deprecated. Use /api/pdf/process-simple for text parsing or /api/pdf/add-manual for manual entry");
            return response;
            
        } catch (Exception e) {
            logger.error("Error processing PDF file", e);
            response.setSuccess(false);
            response.setMessage("Error processing PDF: " + e.getMessage());
            response.setErrorDetails(e.toString());
        }
        
        return response;
    }

    @Override
    public PdfProcessingResponseDto analyzePdfContent(PdfAnalysisRequestDto request) {
        PdfProcessingResponseDto response = new PdfProcessingResponseDto();
        
        try {
            // This method is deprecated - use /api/pdf/process-simple or /api/pdf/add-manual instead
            response.setSuccess(false);
            response.setMessage("This endpoint is deprecated. Use /api/pdf/process-simple for text parsing or /api/pdf/add-manual for manual entry");
            return response;
            
        } catch (Exception e) {
            logger.error("Error analyzing PDF content", e);
            response.setSuccess(false);
            response.setMessage("Error analyzing PDF content: " + e.getMessage());
            response.setErrorDetails(e.toString());
        }
        
        return response;
    }

    @Override
    public String extractTextFromPdf(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // Try PDFBox first for better PDF handling
            try (PDDocument document = PDDocument.load(inputStream)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            } catch (Exception e) {
                logger.warn("PDFBox failed, trying Tika: {}", e.getMessage());
                // Fallback to Tika
                try (InputStream fallbackStream = file.getInputStream()) {
                    return tika.parseToString(fallbackStream);
                }
            }
        } catch (IOException | TikaException e) {
            logger.error("Error extracting text from PDF", e);
            return null;
        }
    }

    @Override
    public List<PdfProcessingResponseDto.ExtractedQuestionDto> validateQuestions(List<PdfProcessingResponseDto.ExtractedQuestionDto> questions) {
        if (questions == null || questions.isEmpty()) {
            return new ArrayList<>();
        }
        
        return questions.stream()
                .map(this::validateSingleQuestion)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public int saveQuestionsToDatabase(List<PdfProcessingResponseDto.ExtractedQuestionDto> questions, PdfProcessingRequestDto request) {
        if (questions == null || questions.isEmpty()) {
            return 0;
        }
        
        int savedCount = 0;
        for (PdfProcessingResponseDto.ExtractedQuestionDto questionDto : questions) {
            try {
                QuestionBank questionBank = new QuestionBank();
                questionBank.setQuestionText(questionDto.getQuestionText());
                questionBank.setCorrectLabel(questionDto.getCorrectLabel());
                questionBank.setExplanation(questionDto.getExplanation());
                questionBank.setLevelId(request.getLevelId());
                questionBank.setLessonId(request.getLessonId());
                questionBank.setCourseLang(request.getCourseLang());
                questionBank.setAssessment(request.getAssessment());
                questionBank.setCreatedDatetime(Timestamp.valueOf(LocalDateTime.now()));
                
                // Convert options to JSON
                String optionsJson = gson.toJson(questionDto.getOptions());
                questionBank.setOptions(optionsJson);
                
                questionBankRepository.save(questionBank);
                savedCount++;
                
            } catch (Exception e) {
                logger.error("Error saving question to database", e);
            }
        }
        
        return savedCount;
    }
    
    private boolean isPdfFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }
    
    private String buildQuestionExtractionPrompt(PdfAnalysisRequestDto request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an expert educational content analyzer. Extract and create multiple choice questions from the following PDF content.\n\n");
        
        prompt.append("REQUIREMENTS:\n");
        prompt.append("- Extract ").append(request.getQuestionCount()).append(" questions\n");
        prompt.append("- Each question must have ").append(request.getAnswerCount()).append(" options (A, B, C, D, E)\n");
        prompt.append("- Questions should be in ").append(request.getCourseLang()).append(" language\n");
        prompt.append("- Include detailed explanations for each answer\n");
        prompt.append("- Focus on the topic: ").append(request.getTopic() != null ? request.getTopic() : "general content").append("\n");
        prompt.append("- Difficulty level: ").append(request.getDifficulty() != null ? request.getDifficulty() : "appropriate for level").append("\n");
        
        if (request.getAssessment()) {
            prompt.append("- These are assessment questions - make them comprehensive and challenging\n");
        }
        
        prompt.append("\nPDF CONTENT:\n");
        prompt.append(request.getPdfContent());
        
        prompt.append("\n\nReturn ONLY a valid JSON array with this exact structure:\n");
        prompt.append("[\n");
        prompt.append("  {\n");
        prompt.append("    \"questionText\": \"Your question here\",\n");
        prompt.append("    \"options\": [\n");
        prompt.append("      {\"label\": \"A\", \"text\": \"Option A\"},\n");
        prompt.append("      {\"label\": \"B\", \"text\": \"Option B\"},\n");
        prompt.append("      {\"label\": \"C\", \"text\": \"Option C\"},\n");
        prompt.append("      {\"label\": \"D\", \"text\": \"Option D\"},\n");
        prompt.append("      {\"label\": \"E\", \"text\": \"Option E\"}\n");
        prompt.append("    ],\n");
        prompt.append("    \"correctLabel\": \"A\",\n");
        prompt.append("    \"explanation\": \"Detailed explanation of why this answer is correct\",\n");
        prompt.append("    \"difficulty\": \"easy|medium|hard\",\n");
        prompt.append("    \"topic\": \"Topic category\",\n");
        prompt.append("    \"isValid\": true\n");
        prompt.append("  }\n");
        prompt.append("]\n");
        
        return prompt.toString();
    }
    
    // AI method removed - use /api/pdf/process-simple or /api/pdf/add-manual instead
    
    private List<PdfProcessingResponseDto.ExtractedQuestionDto> parseExtractedQuestions(String jsonResponse) {
        try {
            // Clean the JSON response
            String cleanedJson = cleanJsonResponse(jsonResponse);
            
            Type listType = new TypeToken<List<PdfProcessingResponseDto.ExtractedQuestionDto>>(){}.getType();
            return gson.fromJson(cleanedJson, listType);
            
        } catch (Exception e) {
            logger.error("Error parsing extracted questions JSON", e);
            return new ArrayList<>();
        }
    }
    
    private String cleanJsonResponse(String jsonResponse) {
        // Remove any markdown code blocks
        if (jsonResponse.contains("```json")) {
            jsonResponse = jsonResponse.substring(jsonResponse.indexOf("```json") + 7);
        }
        if (jsonResponse.contains("```")) {
            jsonResponse = jsonResponse.substring(0, jsonResponse.lastIndexOf("```"));
        }
        
        // Remove any leading/trailing whitespace
        return jsonResponse.trim();
    }
    
    private PdfProcessingResponseDto.ExtractedQuestionDto validateSingleQuestion(PdfProcessingResponseDto.ExtractedQuestionDto question) {
        if (question == null) {
            return null;
        }
        
        // Check required fields
        if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            question.setIsValid(false);
            question.setValidationError("Question text is empty");
            return question;
        }
        
        if (question.getOptions() == null || question.getOptions().isEmpty()) {
            question.setIsValid(false);
            question.setValidationError("No options provided");
            return question;
        }
        
        if (question.getCorrectLabel() == null || question.getCorrectLabel().trim().isEmpty()) {
            question.setIsValid(false);
            question.setValidationError("No correct answer specified");
            return question;
        }
        
        // Validate options
        for (PdfProcessingResponseDto.ExtractedQuestionDto.OptionDto option : question.getOptions()) {
            if (option.getLabel() == null || option.getText() == null || 
                option.getLabel().trim().isEmpty() || option.getText().trim().isEmpty()) {
                question.setIsValid(false);
                question.setValidationError("Invalid option: " + option.getLabel());
                return question;
            }
        }
        
        // Check if correct label exists in options
        boolean correctLabelExists = question.getOptions().stream()
            .anyMatch(opt -> question.getCorrectLabel().equals(opt.getLabel()));
        
        if (!correctLabelExists) {
            question.setIsValid(false);
            question.setValidationError("Correct label '" + question.getCorrectLabel() + "' not found in options");
            return question;
        }
        
        question.setIsValid(true);
        return question;
    }
    
    private double calculateConfidenceScore(List<PdfProcessingResponseDto.ExtractedQuestionDto> questions) {
        if (questions.isEmpty()) {
            return 0.0;
        }
        
        long validQuestions = questions.stream()
            .mapToLong(q -> q.getIsValid() ? 1 : 0)
            .sum();
        
        return (double) validQuestions / questions.size();
    }
    
    private PdfProcessingRequestDto convertToProcessingRequest(PdfAnalysisRequestDto analysisRequest) {
        PdfProcessingRequestDto request = new PdfProcessingRequestDto();
        request.setCourseLang(analysisRequest.getCourseLang());
        request.setLevelId(analysisRequest.getLevelId());
        request.setLessonId(analysisRequest.getLessonId());
        request.setQuestionCount(analysisRequest.getQuestionCount());
        request.setAnswerCount(analysisRequest.getAnswerCount());
        request.setUserLang(analysisRequest.getUserLang());
        request.setTopic(analysisRequest.getTopic());
        request.setDifficulty(analysisRequest.getDifficulty());
        request.setAssessment(analysisRequest.getAssessment());
        request.setExtractionMethod(analysisRequest.getAnalysisType());
        return request;
    }

    @Override
    public PdfProcessingResponseDto processPdfFileSimple(MultipartFile file, SimplePdfProcessingRequestDto request) {
        long startTime = System.currentTimeMillis();
        PdfProcessingResponseDto response = new PdfProcessingResponseDto();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("PDF file is empty");
                return response;
            }
            
            if (!isPdfFile(file)) {
                response.setSuccess(false);
                response.setMessage("File is not a valid PDF");
                return response;
            }
            
            // Extract text from PDF
            String pdfContent = extractTextFromPdf(file);
            if (pdfContent == null || pdfContent.trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("No text content found in PDF");
                return response;
            }
            
            logger.info("Extracted PDF content length: {} characters", pdfContent.length());
            
            // Parse questions from the extracted text
            List<PdfProcessingResponseDto.ExtractedQuestionDto> questions = parseQuestionsFromText(pdfContent);
            
            // Validate questions
            List<PdfProcessingResponseDto.ExtractedQuestionDto> validatedQuestions = validateQuestions(questions);
            
            // Convert to processing request for database saving
            PdfProcessingRequestDto processingRequest = new PdfProcessingRequestDto();
            processingRequest.setCourseLang(request.getCourseLang());
            processingRequest.setLevelId(request.getLevelId());
            processingRequest.setLessonId(request.getLessonId());
            processingRequest.setAssessment(false); // Default to practice questions
            
            // Save to database
            int savedCount = saveQuestionsToDatabase(validatedQuestions, processingRequest);
            
            // Update response with processing stats
            long processingTime = System.currentTimeMillis() - startTime;
            Map<String, Object> stats = new HashMap<>();
            stats.put("processingTimeMs", processingTime);
            stats.put("pdfSizeBytes", file.getSize());
            stats.put("textLength", pdfContent.length());
            stats.put("extractionMethod", "simple_text_parsing");
            stats.put("confidenceScore", calculateConfidenceScore(validatedQuestions));
            
            // Create processing summary
            List<String> validationErrors = new ArrayList<>();
            int validQuestions = 0;
            int invalidQuestions = 0;
            
            for (PdfProcessingResponseDto.ExtractedQuestionDto question : validatedQuestions) {
                if (question.getIsValid()) {
                    validQuestions++;
                } else {
                    invalidQuestions++;
                    if (question.getValidationError() != null) {
                        validationErrors.add(question.getValidationError());
                    }
                }
            }
            
            response.setSuccess(true);
            response.setMessage("Successfully processed PDF and extracted questions using text parsing");
            response.setTotalQuestionsExtracted(questions.size());
            response.setQuestionsAddedToDatabase(savedCount);
            response.setExtractedQuestions(null); // Don't return all questions to reduce response size
            response.setProcessingStats(stats);
            
        } catch (Exception e) {
            logger.error("Error processing PDF file with simple parsing", e);
            response.setSuccess(false);
            response.setMessage("Error processing PDF: " + e.getMessage());
            response.setErrorDetails(e.toString());
        }
        
        return response;
    }
    
    @Override
    public PdfProcessingResponseDto addManualQuestion(ManualQuestionRequestDto request) {
        long startTime = System.currentTimeMillis();
        PdfProcessingResponseDto response = new PdfProcessingResponseDto();
        
        try {
            // Validate request
            if (request.getQuestionText() == null || request.getQuestionText().trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Question text is required");
                return response;
            }
            
            if (request.getOptions() == null || request.getOptions().size() < 2) {
                response.setSuccess(false);
                response.setMessage("At least 2 options are required");
                return response;
            }
            
            if (request.getCorrectLabel() == null || request.getCorrectLabel().trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Correct answer label is required");
                return response;
            }
            
            if (request.getCourseLang() == null || request.getCourseLang().trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Course language is required");
                return response;
            }
            
            if (request.getLevelId() == null) {
                response.setSuccess(false);
                response.setMessage("Level ID is required");
                return response;
            }
            
            if (request.getLessonId() == null) {
                response.setSuccess(false);
                response.setMessage("Lesson ID is required");
                return response;
            }
            
            // Create question DTO
            PdfProcessingResponseDto.ExtractedQuestionDto questionDto = new PdfProcessingResponseDto.ExtractedQuestionDto();
            questionDto.setQuestionText(request.getQuestionText().trim());
            questionDto.setCorrectLabel(request.getCorrectLabel().trim());
            questionDto.setExplanation(request.getExplanation() != null ? request.getExplanation().trim() : "Correct answer: " + request.getCorrectLabel());
            questionDto.setDifficulty("medium");
            questionDto.setTopic("manual");
            questionDto.setIsValid(true);
            
            // Convert options
            List<PdfProcessingResponseDto.ExtractedQuestionDto.OptionDto> options = new ArrayList<>();
            for (ManualQuestionRequestDto.OptionDto option : request.getOptions()) {
                options.add(new PdfProcessingResponseDto.ExtractedQuestionDto.OptionDto(option.getLabel(), option.getText()));
            }
            questionDto.setOptions(options);
            
            // Create processing request for database saving
            PdfProcessingRequestDto processingRequest = new PdfProcessingRequestDto();
            processingRequest.setCourseLang(request.getCourseLang());
            processingRequest.setLevelId(request.getLevelId());
            processingRequest.setLessonId(request.getLessonId());
            processingRequest.setAssessment(request.getAssessment() != null ? request.getAssessment() : false);
            
            // Save to database
            List<PdfProcessingResponseDto.ExtractedQuestionDto> questions = Collections.singletonList(questionDto);
            int savedCount = saveQuestionsToDatabase(questions, processingRequest);
            
            // Create processing stats
            long processingTime = System.currentTimeMillis() - startTime;
            Map<String, Object> stats = new HashMap<>();
            stats.put("processingTimeMs", processingTime);
            stats.put("extractionMethod", "manual_input");
            stats.put("confidenceScore", 1.0); // Manual questions are always 100% confident
            
            response.setSuccess(true);
            response.setMessage("Successfully added manual question to database");
            response.setTotalQuestionsExtracted(1);
            response.setQuestionsAddedToDatabase(savedCount);
            response.setExtractedQuestions(null); // Don't return the question to reduce response size
            response.setProcessingStats(stats);
            
        } catch (Exception e) {
            logger.error("Error adding manual question", e);
            response.setSuccess(false);
            response.setMessage("Error adding question: " + e.getMessage());
            response.setErrorDetails(e.toString());
        }
        
        return response;
    }
    
    private List<PdfProcessingResponseDto.ExtractedQuestionDto> parseQuestionsFromText(String text) {
        List<PdfProcessingResponseDto.ExtractedQuestionDto> questions = new ArrayList<>();
        
        // Split text into lines
        String[] lines = text.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            // Look for question start pattern (number followed by dot)
            if (isQuestionStart(line)) {
                // Try to parse this question
                PdfProcessingResponseDto.ExtractedQuestionDto question = parseQuestionFromLines(lines, i);
                if (question != null) {
                    questions.add(question);
                    logger.info("Successfully parsed question: {}", question.getQuestionText().substring(0, Math.min(50, question.getQuestionText().length())));
                }
            }
        }
        
        logger.info("Parsed {} questions from text", questions.size());
        return questions;
    }
    
    private boolean isQuestionStart(String line) {
        // Check if line starts with a number followed by a dot (e.g., "5.", "2." or "5. Question text")
        return line.matches("^\\d+\\.\\s*.*$");
    }
    
    private PdfProcessingResponseDto.ExtractedQuestionDto parseQuestionFromLines(String[] lines, int startIndex) {
        try {
            StringBuilder questionText = new StringBuilder();
            List<PdfProcessingResponseDto.ExtractedQuestionDto.OptionDto> options = new ArrayList<>();
            String correctAnswer = null;
            
            // Extract question number and initial text
            String firstLine = lines[startIndex].trim();
            String questionPart = firstLine.replaceFirst("^\\d+\\.\\s*", "").trim();
            if (!questionPart.isEmpty()) {
                questionText.append(questionPart);
            }
            
            // Look for question text and options in subsequent lines
            boolean foundOptions = false;
            boolean foundCorrectAnswer = false;
            StringBuilder currentOptionText = new StringBuilder();
            String currentOptionLabel = null;
            
            for (int i = startIndex + 1; i < lines.length && !foundCorrectAnswer; i++) {
                String line = lines[i].trim();
                
                if (line.isEmpty()) continue;
                
                // Check for correct answer
                if (line.contains("DOĞRU CEVAP")) {
                    Matcher matcher = Pattern.compile("DOĞRU CEVAP:\\s*([A-E])").matcher(line);
                    if (matcher.find()) {
                        correctAnswer = matcher.group(1);
                        foundCorrectAnswer = true;
                    }
                    break;
                }
                
                // Check for option patterns
                if (line.matches("^[A-E]\\)\\s*.*$")) {
                    // Save previous option if exists
                    if (currentOptionLabel != null) {
                        options.add(new PdfProcessingResponseDto.ExtractedQuestionDto.OptionDto(currentOptionLabel, currentOptionText.toString().trim()));
                    }
                    
                    foundOptions = true;
                    currentOptionLabel = line.substring(0, 1);
                    currentOptionText = new StringBuilder();
                    String optionText = line.substring(2).trim();
                    if (!optionText.isEmpty()) {
                        currentOptionText.append(optionText);
                    }
                } else if (line.matches("^[A-E]\\)\\s*$")) {
                    // Save previous option if exists
                    if (currentOptionLabel != null) {
                        options.add(new PdfProcessingResponseDto.ExtractedQuestionDto.OptionDto(currentOptionLabel, currentOptionText.toString().trim()));
                    }
                    
                    foundOptions = true;
                    currentOptionLabel = line.substring(0, 1);
                    currentOptionText = new StringBuilder();
                } else if (foundOptions && currentOptionLabel != null && 
                          !line.contains("DOĞRU CEVAP") && !line.contains("Go on to the next page") && 
                          !line.contains("2025-YDS") && !line.contains("For these questions") &&
                          !line.contains("Answer these questions") && !line.matches("^\\d+\\.\\s*$")) {
                    // This is continuation of current option text
                    if (currentOptionText.length() > 0) {
                        currentOptionText.append(" ");
                    }
                    currentOptionText.append(line);
                } else if (!foundOptions && !line.contains("Go on to the next page") && 
                          !line.contains("2025-YDS") && !line.contains("For these questions") &&
                          !line.contains("Answer these questions") && !line.matches("^\\d+\\.\\s*$")) {
                    // This is part of the question text
                    if (questionText.length() > 0) {
                        questionText.append(" ");
                    }
                    questionText.append(line);
                }
            }
            
            // Save the last option if exists
            if (currentOptionLabel != null) {
                options.add(new PdfProcessingResponseDto.ExtractedQuestionDto.OptionDto(currentOptionLabel, currentOptionText.toString().trim()));
            }
            
            // Validate that we have all required components
            if (questionText.length() == 0 || options.size() < 2 || correctAnswer == null) {
                logger.warn("Invalid question format: questionText={}, options={}, correctAnswer={}", 
                           questionText.toString(), options.size(), correctAnswer);
                return null;
            }
            
            // Create the question DTO
            PdfProcessingResponseDto.ExtractedQuestionDto question = new PdfProcessingResponseDto.ExtractedQuestionDto();
            question.setQuestionText(questionText.toString().trim());
            question.setOptions(options);
            question.setCorrectLabel(correctAnswer);
            question.setExplanation("Correct answer: " + correctAnswer);
            question.setDifficulty("medium");
            question.setTopic("general");
            question.setIsValid(true);
            
            return question;
            
        } catch (Exception e) {
            logger.error("Error parsing question from lines starting at index {}", startIndex, e);
            return null;
        }
    }
    
    private PdfProcessingResponseDto.ExtractedQuestionDto parseSingleQuestion(String questionText) {
        try {
            String[] lines = questionText.split("\n");
            
            // Find the question text (lines after the number until options start)
            StringBuilder questionTextBuilder = new StringBuilder();
            List<PdfProcessingResponseDto.ExtractedQuestionDto.OptionDto> options = new ArrayList<>();
            String correctAnswer = null;
            
            boolean foundQuestionText = false;
            boolean inOptions = false;
            
            for (String line : lines) {
                line = line.trim();
                
                if (line.isEmpty()) continue;
                
                // Handle question number line (may contain question text)
                if (line.matches("^\\d+\\.\\s*.*$")) {
                    foundQuestionText = true;
                    // Extract question text from the same line if present
                    String questionPart = line.replaceFirst("^\\d+\\.\\s*", "").trim();
                    if (!questionPart.isEmpty()) {
                        if (questionTextBuilder.length() > 0) {
                            questionTextBuilder.append(" ");
                        }
                        questionTextBuilder.append(questionPart);
                    }
                    continue;
                }
                
                // Check for correct answer
                if (line.contains("DOĞRU CEVAP")) {
                    Matcher matcher = Pattern.compile("DOĞRU CEVAP:\\s*([A-E])").matcher(line);
                    if (matcher.find()) {
                        correctAnswer = matcher.group(1);
                    }
                    break;
                }
                
                // Check if this is an option line (A), B), C), D), E))
                if (line.matches("^[A-E]\\)\\s+.*")) {
                    inOptions = true;
                    String optionLabel = line.substring(0, 1);
                    String optionText = line.substring(2).trim();
                    options.add(new PdfProcessingResponseDto.ExtractedQuestionDto.OptionDto(optionLabel, optionText));
                } else if (foundQuestionText && !inOptions) {
                    // This is part of the question text
                    if (questionTextBuilder.length() > 0) {
                        questionTextBuilder.append(" ");
                    }
                    questionTextBuilder.append(line);
                }
            }
            
            // Validate that we have all required components
            if (questionTextBuilder.length() == 0 || options.size() < 2 || correctAnswer == null) {
                logger.warn("Invalid question format: questionText={}, options={}, correctAnswer={}", 
                           questionTextBuilder.toString(), options.size(), correctAnswer);
                return null;
            }
            
            // Create the question DTO
            PdfProcessingResponseDto.ExtractedQuestionDto question = new PdfProcessingResponseDto.ExtractedQuestionDto();
            question.setQuestionText(questionTextBuilder.toString().trim());
            question.setOptions(options);
            question.setCorrectLabel(correctAnswer);
            question.setExplanation("Correct answer: " + correctAnswer);
            question.setDifficulty("medium");
            question.setTopic("general");
            question.setIsValid(true);
            
            return question;
            
        } catch (Exception e) {
            logger.error("Error parsing single question", e);
            return null;
        }
    }
}

