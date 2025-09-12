package com.ai.service;


import com.ai.dto.*;

import java.util.List;
import java.util.Map;


public interface ExamService {

    TestResponseDto getTestQuestions(TestRequestDto request);

    List<PassedLessonDto> getPassedLessonsByLevel(StatsRequestDto request);

    SessionStatsResponseDto getSessionStats(StatsRequestDto request);

    Map<String, Object> getSessionTest(StatsRequestDto request);

    Integer getAssessmentSpeakingDuration();

}
