package com.ai.service;

import com.ai.dto.*;
import com.ai.entities.UserRequest;
import com.ai.exception.UserNotFoundException;

import java.util.List;

public interface SessionService {

     // New method for exam session creation (test_mode only)
     SessionTestDto createExam(CreateExamRequestDto createExamRequestDto) throws UserNotFoundException;
}
