package com.exam.serviceImpl;


import com.exam.dto.*;
import com.exam.entities.*;
import com.exam.exception.UserNotFoundException;
import com.exam.repository.*;
import com.exam.service.QuestionBankService;
import com.exam.service.SessionService;
import com.exam.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.exam.service.LlamaService;
import com.exam.enums.SessionTestStatus;
import com.exam.repository.AppConfigRepository;
import java.util.Collections;

@Service
public class SessionServiceImpl implements SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class);



    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;


    @Autowired
    private SessionTestRepository sessionTestRepository;

    @Autowired
    private LlamaService llamaService;

    @Autowired
    private SessionTestQuestionRepository sessionTestQuestionRepository;

    @Autowired
    private AppConfigRepository appConfigRepository;

    @Autowired
    private QuestionBankService questionBankService;

    @Autowired
    private LevelAssessmentRepository levelAssessmentRepository;


    @Override
    @Transactional
    public SessionTestDto createExam(CreateExamRequestDto createExamRequestDto) throws UserNotFoundException {
        Optional<User> userOpt = userService.getUser(createExamRequestDto.getUserId());
        User user = userOpt.orElseThrow(UserNotFoundException::new);
        String levelStr = createExamRequestDto.getSelectedLevel();
        String topicStr = createExamRequestDto.getSelectedTopic();
        Long userId = createExamRequestDto.getUserId();
        Long levelId = createExamRequestDto.getSelectedLevelId();
        Long topicId = createExamRequestDto.getSelectedTopicId();
        Boolean restart = createExamRequestDto.getRestart();
        String courseLang = createExamRequestDto.getCourseLang();
        String userLang = createExamRequestDto.getUserLang();

        Integer levelIdInt = (levelId != null) ? levelId.intValue() : null;
        Integer topicIdInt = (topicId != null) ? topicId.intValue() : null;

        List<SessionTest> tests = Collections.emptyList();
        if (userId != null && levelIdInt != null && topicIdInt != null && courseLang != null) {
            if ("CEFR".equalsIgnoreCase(levelStr)) {
                tests = sessionTestRepository.findAssessmentByUserAndCourseLang(userId, courseLang);
            } else {
                tests = sessionTestRepository.findNonAssessmentByUserIdAndLevelIdAndLessonIdAndCourseLang(userId, levelIdInt, topicIdInt, courseLang);
            }
        }
        // Delete all matching tests if restart is true
        if (Boolean.TRUE.equals(restart) && !tests.isEmpty()) {
            for (SessionTest test : tests) {
                sessionTestQuestionRepository.deleteBySessionTest(test);
                sessionTestRepository.delete(test);
            }
        } else if (!tests.isEmpty()) {
            // If not deleting, return the first found test
            return toSessionTestDto(tests.get(0));
        }

        // Generate and trim questions
        List<SessionTestQuestionDto> questionDtos = generateQuestionsForExam(levelStr, levelId, topicId, courseLang);
        int maxQuestionCount = getMaxQuestionCount(levelStr);
        if (questionDtos != null && questionDtos.size() > maxQuestionCount) {
            questionDtos = questionDtos.subList(0, maxQuestionCount);
        }
        // Only create SessionTest if we have questions from OpenAI
        if (questionDtos == null || questionDtos.isEmpty()) {
            // Could log or throw an error here if desired
            return null;
        }
        // Create new SessionTest
        SessionTest sessionTest = new SessionTest();
        sessionTest.setUser(user);
        sessionTest.setCourseLang(courseLang);
        sessionTest.setLevelId(levelIdInt);
        sessionTest.setLessonId(topicIdInt);
        sessionTest.setStatus(SessionTestStatus.IN_PROGRESS);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        sessionTest.setCreatedDateTime(now);
        sessionTest.setUpdatedDateTime(now);
        sessionTest.setAssessment("CEFR".equalsIgnoreCase(levelStr));
        if ("CEFR".equalsIgnoreCase(levelStr)) {
            sessionTest.setSpeakingInsufficientData(true);
        }
        List<SessionTestQuestion> questions = new ArrayList<>();
        int idx = 0;
        for (SessionTestQuestionDto dto : questionDtos) {
            questions.add(createSessionTestQuestion(sessionTest, dto, idx++));
        }
        sessionTest.setScore(0);
        sessionTest.setSpeakingScore(0);
        sessionTest.setTotalScore(0);
        sessionTest.setPass(false);
        sessionTest.setQuestions(questions);
        sessionTest.setQuestionCount(questions.size());
        sessionTestRepository.save(sessionTest);
        return toSessionTestDto(sessionTest);
    }

    // Extracted helper for max question count
    private int getMaxQuestionCount(String levelStr) {
        String configKey = "CEFR".equalsIgnoreCase(levelStr) ? "assessment_question_count" : "question_count";
        int defaultCount = "CEFR".equalsIgnoreCase(levelStr) ? 20 : 10;
        try {
            AppConfig qConfig = appConfigRepository.findByKey(configKey,"ai");
            if (qConfig != null && qConfig.getValue() != null) {
                return Integer.parseInt(qConfig.getValue());
            }
        } catch (Exception ignored) {}
        return defaultCount;
    }

    // Extracted helper for question entity creation
    private SessionTestQuestion createSessionTestQuestion(SessionTest sessionTest, SessionTestQuestionDto dto, int idx) {
        SessionTestQuestion q = new SessionTestQuestion();
        q.setSessionTest(sessionTest);
        q.setQuestionText(dto.getQuestionText());
        q.setOptions(new com.google.gson.Gson().toJson(dto.getOptions()));
        q.setCorrectLabel(dto.getCorrectLabel());
        q.setSelectedLabel(null);
        q.setAnsweredAt(null);
        q.setIsCorrect(null);
        q.setQuestionIndex(idx);
        q.setExplanation(dto.getExplanation());
        return q;
    }

    // Helper to map SessionTest to SessionTestDto
    private SessionTestDto toSessionTestDto(SessionTest sessionTest) {
        SessionTestDto dto = new SessionTestDto();
        dto.setId(sessionTest.getId());
        dto.setStatus(sessionTest.getStatus() != null ? sessionTest.getStatus().name() : null);
        dto.setCompletedAt(sessionTest.getCompletedAt());
        dto.setScore(sessionTest.getScore());
        dto.setPass(sessionTest.getPass());
        dto.setQuestionCount(sessionTest.getQuestionCount());
        dto.setCreatedDateTime(sessionTest.getCreatedDateTime());
        dto.setUpdatedDateTime(sessionTest.getUpdatedDateTime());
        // Map questions
        List<SessionTestQuestionDto> questionDtos = new ArrayList<>();
        if (sessionTest.getQuestions() != null) {
            for (SessionTestQuestion q : sessionTest.getQuestions()) {
                SessionTestQuestionDto qdto = new SessionTestQuestionDto();
                qdto.setId(q.getId());
                qdto.setQuestionText(q.getQuestionText());
                try {
                    com.google.gson.reflect.TypeToken<List<SessionTestQuestionDto.Option>> typeToken = new com.google.gson.reflect.TypeToken<List<SessionTestQuestionDto.Option>>(){};
                    List<SessionTestQuestionDto.Option> options = new com.google.gson.Gson().fromJson(q.getOptions(), typeToken.getType());
                    qdto.setOptions(options);
                } catch (Exception ignored) {}
                qdto.setQuestionIndex(q.getQuestionIndex());
                qdto.setSelectedLabel(q.getSelectedLabel());
                qdto.setCorrectLabel(q.getCorrectLabel());
                qdto.setExplanation(q.getExplanation());
                questionDtos.add(qdto);
            }
        }
        dto.setQuestions(questionDtos);
        return dto;
    }

    private List<SessionTestQuestionDto> generateQuestionsForExam(String levelStr, Long levelId, Long topicId, String courseLang) {
        boolean isAssessment = "CEFR".equalsIgnoreCase(levelStr);
        int questionCount = isAssessment ? 20 : 10;
        
        // Get question count from app config
        if (isAssessment) {
            try {
                AppConfig qConfig = appConfigRepository.findByKey("assessment_question_count","ai");
                if (qConfig != null && qConfig.getValue() != null) {
                    questionCount = Integer.parseInt(qConfig.getValue());
                }
            } catch (Exception e) {}
        } else {
            try {
                AppConfig qConfig = appConfigRepository.findByKey("question_count","ai");
                if (qConfig != null && qConfig.getValue() != null) {
                    questionCount = Integer.parseInt(qConfig.getValue());
                }
            } catch (Exception e) {}
        }
        
        // Try to get questions from the question bank only
        Integer levelIdInt = (levelId != null) ? levelId.intValue() : null;
        Integer topicIdInt = (topicId != null) ? topicId.intValue() : null;
        
        if (levelIdInt != null && topicIdInt != null && courseLang != null) {
            logger.info("Checking question bank for levelId: {}, topicId: {}, courseLang: {}, isAssessment: {}", 
                    levelIdInt, topicIdInt, courseLang, isAssessment);
            
            List<QuestionBank> questionBanks = questionBankService.getQuestionsByLevelAndLessonAndLanguage(levelIdInt, topicIdInt, courseLang, isAssessment);
            
            if (questionBanks != null && !questionBanks.isEmpty()) {
                logger.info("Found {} questions in question bank", questionBanks.size());
                
                // Convert QuestionBank entities to SessionTestQuestionDto objects
                List<SessionTestQuestionDto> questionDtos = questionBankService.convertToSessionTestQuestionDtos(questionBanks);
                
                // Check if we have enough questions
                if (questionDtos.size() >= questionCount) {
                    logger.info("Using {} questions from question bank (requested: {})", questionDtos.size(), questionCount);
                    return questionDtos.subList(0, questionCount);
                } else {
                    logger.warn("Question bank has {} questions, but need {}. Not enough questions available.", 
                            questionDtos.size(), questionCount);
                    // Return what we have if it's at least 1 question, otherwise return empty
                    if (questionDtos.size() > 0) {
                        logger.info("Returning {} available questions from question bank", questionDtos.size());
                        return questionDtos;
                    } else {
                        logger.warn("No questions available in question bank for this level/topic");
                        return new ArrayList<>();
                    }
                }
            } else {
                logger.warn("No questions found in question bank for levelId: {}, topicId: {}, courseLang: {}", 
                        levelIdInt, topicIdInt, courseLang);
                return new ArrayList<>();
            }
        } else {
            logger.warn("Missing required parameters for question bank lookup: levelId={}, topicId={}, courseLang={}", 
                    levelIdInt, topicIdInt, courseLang);
            return new ArrayList<>();
        }
    }

    private void handleAssessmentSessionDestroy(MessageInfoDto messageInfoDto, List<SessionTest> tests) {
        if (tests == null || tests.isEmpty()) {
            System.out.println("[Assessment] No SessionTest found for assessment scoring.");
            return;
        }
        SessionTest sessionTest = tests.get(0);
        // 2. Call OpenAI to get speakingScore, userLevel, insufficientData
        SpeakingScoreResult speakingResult = callOpenAiForSpeakingScore(messageInfoDto.getMessages());
        if (speakingResult == null) {
            System.out.println("[Assessment] OpenAI speaking score result is null or could not be parsed.");
            return;
        }
        System.out.println("[Assessment] OpenAI speaking score result: score=" + speakingResult.speakingScore + ", level=" + speakingResult.userLevel + ", insufficientData=" + speakingResult.insufficientData);
        // If there is a speaking score, mark speakingStatus as COMPLETED
        sessionTest.setSpeakingInsufficientData(speakingResult.insufficientData);
        sessionTest.setSpeakingScore(0);
        sessionTest.setTotalScore(0);
        sessionTest.setUserLevel(null);
        sessionTest.setSpeakingStatus(SessionTestStatus.IN_PROGRESS);
        if (!speakingResult.insufficientData) {
            sessionTest.setSpeakingStatus(SessionTestStatus.COMPLETED);
            sessionTest.setSpeakingScore(speakingResult.speakingScore);
            sessionTest.setUserLevel(speakingResult.userLevel);
            // Calculate totalScore
            Integer totalScore = 0;
            if (speakingResult.speakingScore != null) {
                totalScore = (int)Math.round(sessionTest.getScore() * 0.6 + speakingResult.speakingScore * 0.4);
                sessionTest.setTotalScore(totalScore);
            }
            // Assign CEFR level based on totalScore
            if (totalScore > 0) {
                String cefrLevel = assignCefrLevel(totalScore);
                sessionTest.setUserLevel(cefrLevel);
            }
            this.saveLevelAssessmentHistory(sessionTest);
        }
        System.out.println("[Assessment] Saving to SessionTest: speakingScore=" + sessionTest.getSpeakingScore() + ", totalScore=" + sessionTest.getTotalScore() + ", userLevel=" + sessionTest.getUserLevel() + ", insufficientData=" + sessionTest.getSpeakingInsufficientData());
        sessionTestRepository.save(sessionTest);
    }

    private static class SpeakingScoreResult {
        Integer speakingScore;
        String userLevel;
        boolean insufficientData;
    }

    private SpeakingScoreResult callOpenAiForSpeakingScore(List<MessageDto> messages) {
        // Build prompt
        StringBuilder conversation = new StringBuilder();
        for (MessageDto msg : messages) {
            conversation.append(msg.getSenderNickname()).append(": ").append(msg.getMessage()).append("\n");
        }
        String prompt = "You are a language assessment expert. Given the following conversation, score the user's speaking ability on a scale of 0–100 and assign a CEFR level (A1, A2, B1, B2, C1, C2). If there is not enough data to score, say so. Only return a JSON object: {\"score\": <number>, \"level\": <CEFR>, \"insufficientData\": <true/false>}\nConversation:\n" + conversation;
        System.out.println("[Assessment] Llama prompt for speaking score:\n" + prompt);
        // Call Llama
        String response = llamaService.getSpeakingScoreAssessment(prompt);
        System.out.println("[Assessment] Llama raw response: " + response);
        if (response == null) return null;
        // Clean up response: remove any text before first { and after last }
        int startIdx = response.indexOf("{");
        int endIdx = response.lastIndexOf("}");
        if (startIdx >= 0 && endIdx > startIdx) {
            response = response.substring(startIdx, endIdx + 1);
        }
        try {
            com.google.gson.JsonObject obj = new com.google.gson.JsonParser().parse(response).getAsJsonObject();
            SpeakingScoreResult result = new SpeakingScoreResult();
            result.speakingScore = obj.has("score") && !obj.get("score").isJsonNull() ? obj.get("score").getAsInt() : null;
            result.userLevel = obj.has("level") && !obj.get("level").isJsonNull() ? obj.get("level").getAsString() : null;
            result.insufficientData = obj.has("insufficientData") && !obj.get("insufficientData").isJsonNull() && obj.get("insufficientData").getAsBoolean();
            return result;
        } catch (Exception e) {
            System.out.println("[Assessment] Error parsing OpenAI speaking score response: " + e.getMessage());
            return null;
        }
    }

    public static String assignCefrLevel(int totalScore) {
        if (totalScore <= 25) return "A1";
        if (totalScore <= 40) return "A2";
        if (totalScore <= 60) return "B1";
        if (totalScore <= 75) return "B2";
        if (totalScore <= 90) return "C1";
        return "C2";
    }

    private void saveLevelAssessmentHistory(SessionTest sessionTest) {
        LevelAssessmentHistory levelAssessmentHistory = new LevelAssessmentHistory();
        levelAssessmentHistory.setUser(sessionTest.getUser());
        levelAssessmentHistory.setCourseLang(sessionTest.getCourseLang());
        levelAssessmentHistory.setSpeakingScore(sessionTest.getSpeakingScore());
        levelAssessmentHistory.setTotalScore(sessionTest.getTotalScore());
        levelAssessmentHistory.setUserLevel(sessionTest.getUserLevel());
        levelAssessmentHistory.setScore(sessionTest.getScore());
        levelAssessmentHistory.setCreatedDateTime(new Timestamp(System.currentTimeMillis()));
        levelAssessmentRepository.save(levelAssessmentHistory);
    }

}
