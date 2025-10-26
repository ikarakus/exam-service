package com.exam.controller;

import com.exam.dto.*;
import com.exam.service.PdfProcessingService;
import com.exam.util.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pdf")
@Api(tags = "PDF Processing", description = "PDF processing and question extraction endpoints")
public class PdfProcessingController {

    private static final Logger logger = LoggerFactory.getLogger(PdfProcessingController.class);

    @Autowired
    private PdfProcessingService pdfProcessingService;

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Process PDF file and extract questions", 
                  notes = "Upload a PDF file and extract multiple choice questions to add to the question bank")
    public ResponseEntity<ResponseDto> processPdfFile(
            @ApiParam(value = "PDF file to process", required = true)
            @RequestParam("file") MultipartFile file,
            
            @ApiParam(value = "Processing configuration", required = true)
            @Valid @ModelAttribute PdfProcessingRequestDto request) {
        
        logger.info("Processing PDF file: {} with size: {} bytes", file.getOriginalFilename(), file.getSize());
        
        ResponseDto responseDto = new ResponseDto<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                responseDto.setResponseBody(Collections.singletonList("PDF file is empty"));
                Helper.fillResponse(responseDto, ResultCodes.ERROR, "PDF file is empty");
                return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
            }
            
            // Process the PDF
            PdfProcessingResponseDto processingResponse = pdfProcessingService.processPdfFile(file, request);
            
            if (processingResponse.isSuccess()) {
                responseDto.setResponseBody(Collections.singletonList(processingResponse));
                Helper.fillResponse(responseDto, ResultCodes.OK, "PDF processed successfully");
                logger.info("Successfully processed PDF: {} questions extracted, {} saved to database", 
                           processingResponse.getTotalQuestionsExtracted(), 
                           processingResponse.getQuestionsAddedToDatabase());
            } else {
                responseDto.setResponseBody(Collections.singletonList(processingResponse));
                Helper.fillResponse(responseDto, ResultCodes.ERROR, processingResponse.getMessage());
                logger.error("Failed to process PDF: {}", processingResponse.getMessage());
            }
            
        } catch (Exception e) {
            logger.error("Error processing PDF file", e);
            responseDto.setResponseBody(Collections.singletonList("Error processing PDF: " + e.getMessage()));
            Helper.fillResponse(responseDto, ResultCodes.ERROR, "Internal server error");
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/analyze")
    @ApiOperation(value = "Analyze PDF content text", 
                  notes = "Analyze extracted PDF text content and extract questions")
    public ResponseEntity<ResponseDto> analyzePdfContent(
            @ApiParam(value = "PDF analysis request", required = true)
            @Valid @RequestBody PdfAnalysisRequestDto request) {
        
        logger.info("Analyzing PDF content for language: {}, level: {}", 
                   request.getCourseLang(), request.getLevelId());
        
        ResponseDto responseDto = new ResponseDto<>();
        
        try {
            // Analyze the content
            PdfProcessingResponseDto analysisResponse = pdfProcessingService.analyzePdfContent(request);
            
            if (analysisResponse.isSuccess()) {
                responseDto.setResponseBody(Collections.singletonList(analysisResponse));
                Helper.fillResponse(responseDto, ResultCodes.OK, "PDF content analyzed successfully");
                logger.info("Successfully analyzed PDF content: {} questions extracted, {} saved to database", 
                           analysisResponse.getTotalQuestionsExtracted(), 
                           analysisResponse.getQuestionsAddedToDatabase());
            } else {
                responseDto.setResponseBody(Collections.singletonList(analysisResponse));
                Helper.fillResponse(responseDto, ResultCodes.ERROR, analysisResponse.getMessage());
                logger.error("Failed to analyze PDF content: {}", analysisResponse.getMessage());
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing PDF content", e);
            responseDto.setResponseBody(Collections.singletonList("Error analyzing PDF content: " + e.getMessage()));
            Helper.fillResponse(responseDto, ResultCodes.ERROR, "Internal server error");
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping(value = "/extract-text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Extract text from PDF file", 
                  notes = "Extract plain text content from a PDF file without processing questions")
    public ResponseEntity<ResponseDto> extractTextFromPdf(
            @ApiParam(value = "PDF file to extract text from", required = true)
            @RequestParam("file") MultipartFile file) {
        
        logger.info("Extracting text from PDF file: {}", file.getOriginalFilename());
        
        ResponseDto responseDto = new ResponseDto<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                responseDto.setResponseBody(Collections.singletonList("PDF file is empty"));
                Helper.fillResponse(responseDto, ResultCodes.ERROR, "PDF file is empty");
                return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
            }
            
            // Extract text
            String extractedText = pdfProcessingService.extractTextFromPdf(file);
            
            if (extractedText != null && !extractedText.trim().isEmpty()) {
                responseDto.setResponseBody(Collections.singletonList(extractedText));
                Helper.fillResponse(responseDto, ResultCodes.OK, "Text extracted successfully");
                logger.info("Successfully extracted text from PDF: {} characters", extractedText.length());
            } else {
                responseDto.setResponseBody(Collections.singletonList("No text content found in PDF"));
                Helper.fillResponse(responseDto, ResultCodes.ERROR, "No text content found");
            }
            
        } catch (Exception e) {
            logger.error("Error extracting text from PDF", e);
            responseDto.setResponseBody(Collections.singletonList("Error extracting text: " + e.getMessage()));
            Helper.fillResponse(responseDto, ResultCodes.ERROR, "Internal server error");
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping(value = "/process-simple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Process PDF file with simple text parsing", 
                  notes = "Upload a PDF file and extract questions using text parsing for specific format with DOĞRU CEVAP")
    public ResponseEntity<ResponseDto> processPdfFileSimple(
            @ApiParam(value = "PDF file to process", required = true)
            @RequestParam("file") MultipartFile file,
            
            @ApiParam(value = "Course language", required = true)
            @RequestParam("courseLang") String courseLang,
            
            @ApiParam(value = "Level ID", required = true)
            @RequestParam("levelId") Integer levelId,
            
            @ApiParam(value = "Lesson ID", required = true)
            @RequestParam("lessonId") Integer lessonId) {
        
        logger.info("Processing PDF file with simple parsing: {} with size: {} bytes", file.getOriginalFilename(), file.getSize());
        
        ResponseDto responseDto = new ResponseDto<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                responseDto.setResponseBody(Collections.singletonList("PDF file is empty"));
                Helper.fillResponse(responseDto, ResultCodes.ERROR, "PDF file is empty");
                return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
            }
            
            // Create simple request
            SimplePdfProcessingRequestDto request = new SimplePdfProcessingRequestDto();
            request.setCourseLang(courseLang);
            request.setLevelId(levelId);
            request.setLessonId(lessonId);
            
            // Process the PDF
            PdfProcessingResponseDto processingResponse = pdfProcessingService.processPdfFileSimple(file, request);
            
            if (processingResponse.isSuccess()) {
                responseDto.setResponseBody(Collections.singletonList(processingResponse));
                Helper.fillResponse(responseDto, ResultCodes.OK, "PDF processed successfully with simple parsing");
                logger.info("Successfully processed PDF with simple parsing: {} questions extracted, {} saved to database", 
                           processingResponse.getTotalQuestionsExtracted(), 
                           processingResponse.getQuestionsAddedToDatabase());
            } else {
                responseDto.setResponseBody(Collections.singletonList(processingResponse));
                Helper.fillResponse(responseDto, ResultCodes.ERROR, processingResponse.getMessage());
                logger.error("Failed to process PDF with simple parsing: {}", processingResponse.getMessage());
            }
            
        } catch (Exception e) {
            logger.error("Error processing PDF file with simple parsing", e);
            responseDto.setResponseBody(Collections.singletonList("Error processing PDF: " + e.getMessage()));
            Helper.fillResponse(responseDto, ResultCodes.ERROR, "Internal server error");
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/health")
    @ApiOperation(value = "Health check for PDF processing service")
    public ResponseEntity<ResponseDto> healthCheck() {
        ResponseDto responseDto = new ResponseDto<>();
        responseDto.setResponseBody(Collections.singletonList("PDF processing service is healthy"));
        Helper.fillResponse(responseDto, ResultCodes.OK, "Service is running");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
    @PostMapping("/add-manual")
    @ApiOperation(value = "Add manual question to question bank",
                  notes = "Manually add a question with options to the question bank")
    public ResponseEntity<ResponseDto> addManualQuestion(
            @ApiParam(value = "Manual question data", required = true)
            @Valid @RequestBody ManualQuestionRequestDto request) {

        logger.info("Adding manual question: {} with {} options", 
                   request.getQuestionText().substring(0, Math.min(50, request.getQuestionText().length())), 
                   request.getOptions().size());

        ResponseDto responseDto = new ResponseDto<>();

        try {
            PdfProcessingResponseDto processingResponse = pdfProcessingService.addManualQuestion(request);

            if (processingResponse.isSuccess()) {
                responseDto.setResponseBody(Collections.singletonList(processingResponse));
                Helper.fillResponse(responseDto, ResultCodes.OK, "Manual question added successfully");
                logger.info("Successfully added manual question: {} questions saved to database",
                           processingResponse.getQuestionsAddedToDatabase());
            } else {
                responseDto.setResponseBody(Collections.singletonList(processingResponse));
                Helper.fillResponse(responseDto, ResultCodes.ERROR, processingResponse.getMessage());
                logger.error("Failed to add manual question: {}", processingResponse.getMessage());
            }

        } catch (Exception e) {
            logger.error("Error adding manual question", e);
            responseDto.setResponseBody(Collections.singletonList("Error adding question: " + e.getMessage()));
            Helper.fillResponse(responseDto, ResultCodes.ERROR, "Internal server error");
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
    @GetMapping("/summary")
    @ApiOperation(value = "Get processing summary", notes = "Get a summary of recent PDF processing results")
    public ResponseEntity<ResponseDto> getProcessingSummary() {
        logger.info("Getting PDF processing summary");
        
        ResponseDto responseDto = new ResponseDto<>();
        
        try {
            // This would typically query the database for recent processing results
            // For now, return a simple summary
            Map<String, Object> summary = new HashMap<>();
            summary.put("message", "PDF processing summary endpoint - implement database queries as needed");
            summary.put("timestamp", new java.util.Date());
            
            responseDto.setResponseBody(Collections.singletonList(summary));
            Helper.fillResponse(responseDto, ResultCodes.OK, "Processing summary retrieved successfully");
            
        } catch (Exception e) {
            logger.error("Error getting processing summary", e);
            responseDto.setResponseBody(Collections.singletonList("Error getting summary: " + e.getMessage()));
            Helper.fillResponse(responseDto, ResultCodes.ERROR, "Internal server error");
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

