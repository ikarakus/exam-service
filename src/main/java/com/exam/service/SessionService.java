package com.exam.service;

import com.exam.dto.*;
import com.exam.exception.UserNotFoundException;

public interface SessionService {

     // New method for exam session creation (test_mode only)
     SessionTestDto createExam(CreateExamRequestDto createExamRequestDto) throws UserNotFoundException;
}
