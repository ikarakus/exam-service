package com.ai.serviceImpl;


import com.ai.dto.*;
import com.ai.entities.*;
import com.ai.enums.SessionTestStatus;
import com.ai.repository.*;
import com.ai.service.ExamService;
import com.ai.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private SessionTestRepository sessionTestRepository;

    @Autowired
    private SessionTestQuestionRepository sessionTestQuestionRepository;

    @Autowired
    private AppConfigRepository appConfigRepository;

    @Autowired
    private LessonLevelRepository lessonLevelRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserRequestRepository userRequestRepository;

    @Override
    public TestResponseDto getTestQuestions(TestRequestDto request) {
        List<SessionTest> tests = getRelevantSessionTests(request);
        if (tests == null || tests.isEmpty()) {
            System.out.println("No session_test found for userId=" + request.getUserId() + ", levelId=" + request.getSelectedLevelId() + ", topicId=" + request.getSelectedTopicId() + ", courseLang=" + request.getCourseLang());
            return null;
        }
        SessionTest sessionTest = sessionTestRepository.findByIdWithQuestions(tests.get(0).getId());
        Map<String, Object> lastAnsweredMap = maybeUpdateAnswerAndGetLastAnswered(request, sessionTest);
        sessionTest = sessionTestRepository.findByIdWithQuestions(sessionTest.getId());
        List<SessionTestQuestion> unanswered = sessionTestQuestionRepository.findFirstUnansweredBySessionTestId(sessionTest.getId());
        if (unanswered != null && !unanswered.isEmpty()) {
            return buildQuestionResponse(sessionTest, unanswered.get(0), lastAnsweredMap);
        } else {
            return buildFinishedResponse(sessionTest, request, lastAnsweredMap);
        }
    }

    // Helper: get relevant session tests for the request
    private List<SessionTest> getRelevantSessionTests(TestRequestDto request) {
        if ("CEFR".equalsIgnoreCase(request.getSelectedLevel())) {
            return Helper.toList(sessionTestRepository.findAssessmentByUserIdAndCourseLang(request.getUserId(), request.getCourseLang()));
        } else {
            return sessionTestRepository.findNonAssessmentByUserIdAndLevelIdAndLessonIdAndCourseLang(
                    request.getUserId(), request.getSelectedLevelId(), request.getSelectedTopicId(), request.getCourseLang());
        }
    }

    // Helper: update answer if needed and return lastAnsweredMap
    private Map<String, Object> maybeUpdateAnswerAndGetLastAnswered(TestRequestDto request, SessionTest sessionTest) {
        if (request.getSelectedLabel() == null || request.getQuestionId() == null) return null;
        System.out.println("Updating answer for questionId=" + request.getQuestionId() + ", selectedLabel=" + request.getSelectedLabel());
        SessionTestQuestion question = sessionTestQuestionRepository.findById(request.getQuestionId()).orElse(null);
        if (question != null && (question.getSelectedLabel() == null || question.getSelectedLabel().isEmpty())) {
            question.setSelectedLabel(request.getSelectedLabel());
            question.setIsCorrect(question.getCorrectLabel() != null && question.getCorrectLabel().equals(request.getSelectedLabel()));
            question.setAnsweredAt(new java.sql.Timestamp(System.currentTimeMillis()));
            sessionTestQuestionRepository.save(question);
            Map<String, Object> lastAnsweredMap = new java.util.HashMap<>();
            lastAnsweredMap.put("questionId", question.getId());
            lastAnsweredMap.put("selectedLabel", request.getSelectedLabel());
            lastAnsweredMap.put("isCorrect", question.getIsCorrect());
            lastAnsweredMap.put("correctLabel", question.getCorrectLabel());
            lastAnsweredMap.put("explanation", question.getExplanation());
            return lastAnsweredMap;
        }
        return null;
    }

    // Helper: build response for next question
    private TestResponseDto buildQuestionResponse(SessionTest sessionTest, SessionTestQuestion nextQ, Map<String, Object> lastAnsweredMap) {
        TestResponseDto response = new TestResponseDto();
        response.setStatus("question");
        if (lastAnsweredMap != null) response.setLastAnswered(lastAnsweredMap);
        TestResponseDto.Question qDto = new TestResponseDto.Question();
        qDto.setQuestionId(nextQ.getId());
        qDto.setQuestionText(nextQ.getQuestionText());
        qDto.setOptions(parseOptions(nextQ.getOptions()));
        response.setQuestion(qDto);
        TestResponseDto.Progress progress = new TestResponseDto.Progress();
        long answered = sessionTest.getQuestions().stream().filter(q -> q.getSelectedLabel() != null && !q.getSelectedLabel().isEmpty()).count();
        progress.setCurrent((int)answered + 1);
        progress.setTotal(sessionTest.getQuestionCount() != null ? sessionTest.getQuestionCount() : sessionTest.getQuestions().size());
        response.setProgress(progress);
        return response;
    }

    // Helper: build response for finished test
    private TestResponseDto buildFinishedResponse(SessionTest sessionTest, TestRequestDto request, Map<String, Object> lastAnsweredMap) {
        TestResponseDto response = new TestResponseDto();
        response.setStatus("finished");
        if (lastAnsweredMap != null) response.setLastAnswered(lastAnsweredMap);
        TestResponseDto.Result result = new TestResponseDto.Result();
        int correct = (int)sessionTest.getQuestions().stream().filter(q -> Boolean.TRUE.equals(q.getIsCorrect())).count();
        int threshold = getThreshold();
        int total = getQuestionCount(request);
        int wrong = total - correct;
        int score = (int)Math.round((100.0 / total) * correct);
        result.setScore(score);
        result.setCorrect(correct);
        result.setWrong(wrong);
        result.setTotal(total);
        result.setThreshold(threshold);
        result.setPassed(score >= threshold);
        if ("CEFR".equalsIgnoreCase(request.getSelectedLevel())) {
            String cefrLevel = com.ai.serviceImpl.SessionServiceImpl.assignCefrLevel(score);
            result.setLanguageLevel(cefrLevel);
            sessionTest.setUserLevel(cefrLevel);
        }
        response.setResult(result);
        sessionTest.setStatus(SessionTestStatus.COMPLETED);
        sessionTest.setCompletedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        sessionTest.setScore(score);
        sessionTest.setPass(score >= threshold);
        sessionTestRepository.save(sessionTest);
        return response;
    }

    // Helper: parse options JSON
    private java.util.List<TestResponseDto.Option> parseOptions(String optionsJson) {
        java.util.List<TestResponseDto.Option> options = new java.util.ArrayList<>();
        try {
            com.google.gson.reflect.TypeToken<java.util.List<com.ai.dto.SessionTestQuestionDto.Option>> typeToken = new com.google.gson.reflect.TypeToken<java.util.List<com.ai.dto.SessionTestQuestionDto.Option>>(){};
            java.util.List<com.ai.dto.SessionTestQuestionDto.Option> optList = new com.google.gson.Gson().fromJson(optionsJson, typeToken.getType());
            for (com.ai.dto.SessionTestQuestionDto.Option o : optList) {
                TestResponseDto.Option opt = new TestResponseDto.Option();
                opt.setLabel(o.getLabel());
                opt.setText(o.getText());
                options.add(opt);
            }
        } catch (Exception e) {
            System.out.println("Error parsing options: " + e.getMessage());
        }
        return options;
    }

    // Helper: get threshold from app config
    private int getThreshold() {
        AppConfig thresholdConfig = appConfigRepository.findByKey("score_threshold","ai");
        int threshold = 60;
        try {
            if (thresholdConfig != null && thresholdConfig.getValue() != null) {
                threshold = Integer.parseInt(thresholdConfig.getValue());
            }
        } catch (Exception e) {
            System.out.println("Error parsing score_threshold from app_config: " + e.getMessage());
        }
        return threshold;
    }

    // Helper: get question count from app config
    private int getQuestionCount(TestRequestDto request) {
        String key = "CEFR".equalsIgnoreCase(request.getSelectedLevel()) ? "assessment_question_count" : "question_count";
        AppConfig questionCountConfig = appConfigRepository.findByKey(key,"ai");
        int total = 10;
        try {
            if (questionCountConfig != null && questionCountConfig.getValue() != null) {
                total = Integer.parseInt(questionCountConfig.getValue());
            }
        } catch (Exception e) {
            System.out.println("Error parsing question_count from app_config: " + e.getMessage());
        }
        return total;
    }

    @Override
    public List<PassedLessonDto> getPassedLessonsByLevel(StatsRequestDto request) {
        List<Object[]> results = sessionTestRepository.findPassedLessonsByLevel(request.getUserId(), request.getCourseLang(),request.getLevelId());
        List<PassedLessonDto> passedLessons = results.stream()
                .map(result -> new PassedLessonDto(
                        ((Number) result[0]).intValue(),
                        (String) result[1],
                        (String) result[2],
                        ((Number) result[3]).intValue()))
                .collect(Collectors.toList());

        return passedLessons;
    }

    @Override
    public SessionStatsResponseDto getSessionStats(StatsRequestDto request) {
        List<LevelStatsDto> stats = new ArrayList<>();
        List<SessionStatsResponseDto.AssessmentStatsDto> assessments = new java.util.ArrayList<>();
        // Get all session tests for the user
        List<SessionTest> allUserTests = sessionTestRepository.findByUserIdAndCourseLang(request.getUserId(), request.getCourseLang());
        // Get all levels for the language
        List<LessonLevel> levels = lessonLevelRepository.findByLangExcludingId5(request.getCourseLang());
        // Map for quick lookup: levelId -> List<SessionTest>
        Map<Integer, List<SessionTest>> testsByLevel = allUserTests.stream().collect(
                java.util.stream.Collectors.groupingBy(SessionTest::getLevelId));
        // Map for quick lookup: lessonId -> List<SessionTest>
        Map<Integer, List<SessionTest>> testsByLesson = allUserTests.stream().collect(
                java.util.stream.Collectors.groupingBy(SessionTest::getLessonId));
        // Get lessons for each level
        List<Lesson> allLessons = lessonRepository.findAll();
        AppConfig thresholdConfig = appConfigRepository.findByKey("score_threshold","ai");
        int scoreThreshold = 60;
        if (thresholdConfig != null && thresholdConfig.getValue() != null) {
            try {
                scoreThreshold = Integer.parseInt(thresholdConfig.getValue());
            } catch (Exception ignored) {
            }
        }
        // Get sum durations per level for the user and language
        List<Object[]> durations = userRequestRepository.findSumDurationByUserIdAndLangGroupedByLevel(request.getUserId(), request.getCourseLang());
        // Map for quick lookup by code
        Map<String, Integer> durationMap = new java.util.HashMap<>();
        for (Object[] row : durations) {
            String levelCode = (String) row[0];
            Long sumDuration = (Long) row[1];
            durationMap.put(levelCode, sumDuration == null ? 0 : sumDuration.intValue());
        }
        for (LessonLevel level : levels) {
            LevelStatsDto dto = new LevelStatsDto();
            dto.setId(level.getId());
            dto.setName(level.getName());
            dto.setCode(level.getCode());
            // Lessons for this level
            List<Lesson> lessons = allLessons.stream()
                    .filter(l -> l.getLevelId() != null && l.getLevelId().equals(level.getId()))
                    .collect(java.util.stream.Collectors.toList());
            dto.setLessonCount(lessons.size());

            // Calculate true average score considering all lessons
            List<SessionTest> testsForLevel = testsByLevel.getOrDefault(level.getId(), java.util.Collections.emptyList());
            int totalScore = 0;
            Map<Integer, Integer> lessonScores = new HashMap<>();

            // Get the highest score for each lesson (in case of multiple attempts)
            for (SessionTest test : testsForLevel) {
                if (test.getScore() != null && test.getLessonId() != null) {
                    lessonScores.merge(test.getLessonId(), test.getScore(),
                            (oldScore, newScore) -> Math.max(oldScore, newScore));
                }
            }

            // Sum up scores (using 0 for untaken lessons)
            for (Lesson lesson : lessons) {
                totalScore += lessonScores.getOrDefault(lesson.getId(), 0);
            }

            // Calculate average considering all lessons
            int avgScore = lessons.isEmpty() ? 0 : (int) Math.round((double) totalScore / lessons.size());
            dto.setAverageScore(avgScore);

            // Duration
            int duration = durationMap.getOrDefault(level.getCode(), 0);
            dto.setDuration(duration);
            // Passed count: for each lesson in this level, count if user has a test with pass=true
            int passedCount = 0;
            for (Lesson lesson : lessons) {
                List<SessionTest> testsForLesson = testsByLesson.getOrDefault(lesson.getId(), java.util.Collections.emptyList());
                if (testsForLesson.stream().anyMatch(t -> Boolean.TRUE.equals(t.getPass()))) {
                    passedCount++;
                }
            }
            dto.setPassedCount(passedCount);
            dto.setPercentCompleted(lessons.isEmpty() ? 0 : (int) Math.round(100.0 * passedCount / lessons.size()));
            dto.setScoreThreshold(scoreThreshold);
            dto.setPassed(avgScore >= scoreThreshold);
            stats.add(dto);
        }

        // Fetch assessment Test
        Optional<SessionTest> assessmentTest = sessionTestRepository.findAssessmentByUserIdAndCourseLang(request.getUserId(), request.getCourseLang());
        if (assessmentTest.isPresent()) {
            assessments.add(new SessionStatsResponseDto.AssessmentStatsDto(
                    assessmentTest.get().getUpdatedDateTime(),
                    assessmentTest.get().getScore(),
                    assessmentTest.get().getSpeakingScore(),
                    assessmentTest.get().getTotalScore(),
                    assessmentTest.get().getUserLevel(),
                    assessmentTest.get().getSpeakingInsufficientData()
            ));
        } else {
            // Always include a default assessment dictionary when no assessment record exists
            assessments.add(new SessionStatsResponseDto.AssessmentStatsDto(
                    new java.sql.Timestamp(System.currentTimeMillis()), // updatedDatetime
                    0, // score
                    0, // speakingScore
                    0, // totalScore
                    null, // userLevel
                    true // speakingInsufficientData
            ));
        }

        SessionStatsResponseDto response = new SessionStatsResponseDto(stats, assessments);
        return response;
    }

    @Override
    public Map<String, Object> getSessionTest(StatsRequestDto request) {
        // Find latest SessionTest for user, level, lesson, and courseLang
        List<SessionTest> tests = sessionTestRepository.findAllByUserIdAndLevelIdAndLessonIdAndCourseLang(request.getUserId(), request.getLevelId(), request.getLessonId(), request.getCourseLang());
        if (tests == null || tests.isEmpty()) {
            return null;
        }
        SessionTest latestTest = sessionTestRepository.findByIdWithQuestions(tests.get(0).getId());
        if (latestTest == null) {
            return null;
        }
        // Get all questions for this test
        List<SessionTestQuestionDto> previousQuestions = new ArrayList<>();
        SessionTestQuestionDto currentQuestion = null;
        for (com.ai.entities.SessionTestQuestion q : latestTest.getQuestions()) {
            boolean answered = q.getSelectedLabel() != null && !q.getSelectedLabel().isEmpty();
            SessionTestQuestionDto qdto = new SessionTestQuestionDto();
            qdto.setId(q.getId());
            qdto.setQuestionText(q.getQuestionText());
            List<SessionTestQuestionDto.Option> options = new ArrayList<>();
            try {
                com.google.gson.reflect.TypeToken<List<SessionTestQuestionDto.Option>> typeToken = new com.google.gson.reflect.TypeToken<List<SessionTestQuestionDto.Option>>(){};
                options = new com.google.gson.Gson().fromJson(q.getOptions(), typeToken.getType());
            } catch (Exception ignored) {}
            qdto.setOptions(options);
            qdto.setQuestionIndex(q.getQuestionIndex());
            qdto.setSelectedLabel(q.getSelectedLabel());
            qdto.setCorrectLabel(q.getCorrectLabel());
            qdto.setExplanation(q.getExplanation());
            if (!answered && currentQuestion == null) {
                // First unanswered question
                qdto.setSelectedLabel(null);
                currentQuestion = qdto;
            } else if (answered) {
                previousQuestions.add(qdto);
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("id", latestTest.getId());
        response.put("status", latestTest.getStatus().name());
        response.put("speakingStatus", (latestTest.getSpeakingStatus() != null && !latestTest.getSpeakingStatus().name().isEmpty()) ? latestTest.getSpeakingStatus().name() : "IN_PROGRESS");
        response.put("isAssessment", latestTest.getAssessment());
        response.put("currentQuestionIndex", currentQuestion != null ? currentQuestion.getQuestionIndex() : null);
        response.put("completedAt", latestTest.getCompletedAt());
        response.put("score", latestTest.getScore());
        response.put("pass", latestTest.getPass());
        response.put("questionCount", latestTest.getQuestionCount());
        response.put("createdDateTime", latestTest.getCreatedDateTime());
        response.put("updatedDateTime", latestTest.getUpdatedDateTime());
        response.put("previousQuestions", previousQuestions);
        response.put("currentQuestion", currentQuestion);

        // Add 'result' and 'lastQuestion' fields
        if (latestTest.getStatus() == com.ai.enums.SessionTestStatus.COMPLETED) {
            // Build result object (same as gpt/test)
            int correct = (int) previousQuestions.stream().filter(q -> Boolean.TRUE.equals(q.getSelectedLabel() != null && q.getSelectedLabel().equals(q.getCorrectLabel()))).count();
            int total = latestTest.getQuestionCount() != null ? latestTest.getQuestionCount() : previousQuestions.size();
            int wrong = total - correct;
            int threshold = 60; // default
            com.ai.entities.AppConfig thresholdConfig = appConfigRepository.findByKey("score_threshold","ai");
            if (thresholdConfig != null && thresholdConfig.getValue() != null) {
                try {
                    threshold = Integer.parseInt(thresholdConfig.getValue());
                } catch (Exception ignored) {}
            }
            int score = latestTest.getScore() != null ? latestTest.getScore() : 0;
            boolean passed = latestTest.getPass() != null ? latestTest.getPass() : false;
            Map<String, Object> result = new HashMap<>();
            result.put("score", score);
            result.put("correct", correct);
            result.put("wrong", wrong);
            result.put("total", total);
            result.put("threshold", threshold);
            result.put("passed", passed);
            response.put("result", result);

            // Find lastQuestion (highest index in previousQuestions)
            SessionTestQuestionDto lastQuestion = previousQuestions.stream()
                    .max(java.util.Comparator.comparingInt(SessionTestQuestionDto::getQuestionIndex))
                    .orElse(null);
            response.put("lastQuestion", lastQuestion);
        } else {
            response.put("result", null);
            response.put("lastQuestion", null);
        }

        return response;
    }

    public Integer getAssessmentSpeakingDuration() {
        Integer duration = 0;
        AppConfig config = appConfigRepository.findByKey("assessment_speaking_duration","ai");
        try {
            if (config != null && config.getValue() != null) {
                duration = Integer.parseInt(config.getValue());
            }
        } catch (Exception e) {
            System.out.println("Error parsing openai_key from app_config: " + e.getMessage());
        }
        return duration;

    }

}
