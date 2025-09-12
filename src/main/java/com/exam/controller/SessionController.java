package com.exam.controller;

import com.exam.dto.*;
import com.exam.entities.QuestionBank;
import com.exam.exception.UserNotFoundException;
import com.exam.service.ExamService;
import com.exam.service.QuestionBankService;
import com.exam.service.SessionService;
import com.exam.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    SessionService sessionService;

    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionBankService questionBankService;


    @PostMapping("/create-exam")
    public ResponseEntity<ResponseDto<SessionTestDto>> createExam(@RequestBody CreateExamRequestDto createExamRequestDto) throws UserNotFoundException {
        ResponseDto<SessionTestDto> responseDto = new ResponseDto<>();
        SessionTestDto sessionTestDto = sessionService.createExam(createExamRequestDto);
        responseDto.setResponseBody(Collections.singletonList(sessionTestDto));
        Helper.fillResponse(responseDto, ResultCodes.OK, null);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @PostMapping("/stats")
    public ResponseEntity<SessionStatsResponseDto> getSessionStats(@RequestBody StatsRequestDto request) {
        SessionStatsResponseDto response = examService.getSessionStats(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> getSessionTest(@RequestBody StatsRequestDto request) {
        Map<String, Object> response = examService.getSessionTest(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-question-bank")
    public ResponseEntity<ResponseDto<QuestionBank>> createQuestionBank(@RequestBody CreateQuestionBankRequestDto request) {
        ResponseDto<QuestionBank> responseDto = new ResponseDto<>();
        try {
            List<QuestionBank> questions = questionBankService.createQuestionBank(request);
            responseDto.setResponseBody(questions);
            Helper.fillResponse(responseDto, ResultCodes.OK, null);
        } catch (IllegalArgumentException e) {
            // Handle language validation errors specifically
            responseDto.setResultCode(ResultCodes.BAD_EXCEPTION);
            responseDto.setErrorMessage("Invalid language configuration: " + e.getMessage());
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            responseDto.setResultCode(ResultCodes.INTERNAL_SERVER_ERROR);
            responseDto.setErrorMessage("An error occurred while creating question bank: " + e.getMessage());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/create-level-question-bank")
    public ResponseEntity<ResponseDto<String>> createLevelQuestionBank(@RequestBody CreateLevelQuestionBankRequestDto request) {
        ResponseDto<String> responseDto = new ResponseDto<>();
        try {
            // Start the background process
            CompletableFuture<String> future = questionBankService.createLevelQuestionBank(request);
            
            // Return immediately with a success message
            String message;
            if (Boolean.TRUE.equals(request.getAssessment())) {
                message = "Assessment question bank creation started in background for courseLang: " + request.getCourseLang();
            } else {
                message = "Question bank creation started in background for level ID: " + request.getLevelId();
            }
            responseDto.setResponseBody(Collections.singletonList(message));
            Helper.fillResponse(responseDto, ResultCodes.OK, null);
            
        } catch (IllegalArgumentException e) {
            // Handle language validation errors specifically
            responseDto.setResultCode(ResultCodes.BAD_EXCEPTION);
            responseDto.setErrorMessage("Invalid language configuration: " + e.getMessage());
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            responseDto.setResultCode(ResultCodes.INTERNAL_SERVER_ERROR);
            responseDto.setErrorMessage("An error occurred while starting question bank creation: " + e.getMessage());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/passed-lessons")
    public ResponseEntity<ResponseDto<PassedLessonDto>> getPassedLessonsByLevel(@RequestBody StatsRequestDto request) {
        ResponseDto<PassedLessonDto> responseDto = new ResponseDto<>();
        try {
           List<PassedLessonDto> passedLessons = examService.getPassedLessonsByLevel(request);
           responseDto.setResponseBody(passedLessons);
           Helper.fillResponse(responseDto, ResultCodes.OK, null);
           return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (Exception e) {
            responseDto.setResultCode(ResultCodes.INTERNAL_SERVER_ERROR);
            responseDto.setErrorMessage("An error occurred: " + e.getMessage());
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }
    }

    @GetMapping("/assessment-speaking-duration")
    public ResponseEntity<Map<String, Object>> getAssessmentSpeakingDuration() {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer duration = examService.getAssessmentSpeakingDuration();
            response.put("duration", duration);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NumberFormatException e) {
            response.put("error", "assessment_speaking_duration value is not a valid integer");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
