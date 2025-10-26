package com.exam.serviceImpl;

import com.exam.dto.CreateLevelQuestionBankRequestDto;
import com.exam.dto.CreateQuestionBankRequestDto;
import com.exam.dto.SessionTestQuestionDto;
import com.exam.entities.AppConfig;
import com.exam.entities.Lesson;
import com.exam.entities.LessonLevel;
import com.exam.entities.QuestionBank;
import com.exam.repository.AppConfigRepository;
import com.exam.repository.LessonLevelRepository;
import com.exam.repository.LessonRepository;
import com.exam.repository.QuestionBankRepository;
import com.exam.service.LlamaService;
import com.exam.service.QuestionBankService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class QuestionBankServiceImpl implements QuestionBankService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionBankServiceImpl.class);

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private LessonLevelRepository lessonLevelRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LlamaService llamaService;

    @Autowired
    private AppConfigRepository appConfigRepository;

    private final Gson gson = new Gson();

    /**
     * Validates that a course language exists and is active
     * @param courseLang the language code to validate
     * @throws IllegalArgumentException if the language is not found or not active
     */
    private void validateCourseLanguage(String courseLang) {
        if (courseLang == null || courseLang.trim().isEmpty()) {
            throw new IllegalArgumentException("Language code cannot be null or empty");
        }
        
        // Use the LlamaService validation method
        llamaService.validateCourseLanguage(courseLang);
    }

    @Override
    @Transactional
    public List<QuestionBank> createQuestionBank(CreateQuestionBankRequestDto request) {
        logger.info("Creating question bank for level: {}, topic: {}, levelId: {}, topicId: {}, courseLang: {}", 
                request.getLevel(), request.getTopic(), request.getLevelId(), request.getTopicId(), request.getCourseLang());

        // Generate questions for the specific level and topic
        List<QuestionBank> questions = generateQuestionsForLevelAndTopic(request);
        
        if (questions != null && !questions.isEmpty()) {
            // Save questions immediately
            List<QuestionBank> savedQuestions = questionBankRepository.saveAll(questions);
            logger.info("Successfully created {} questions in question bank", savedQuestions.size());
            return savedQuestions;
        } else {
            logger.warn("No questions generated for level: {} and topic: {}", request.getLevel(), request.getTopic());
            return new ArrayList<>();
        }
    }

    private List<QuestionBank> generateQuestionsForLevelAndTopic(CreateQuestionBankRequestDto request) {
        logger.info("Generating questions for level: {} and topic: {}", request.getLevel(), request.getTopic());
        
        // Get level name from database
        String levelName = request.getLevel();
        if (levelName == null && request.getLevelId() != null) {
            try {
                LessonLevel level = lessonLevelRepository.findById(request.getLevelId()).orElse(null);
                if (level != null) {
                    levelName = level.getName();
                }
            } catch (Exception e) {
                logger.warn("Could not fetch level name for levelId: {}, using provided level", request.getLevelId(), e);
            }
        }
        
        // Get lesson name from database
        String topicName = request.getTopic();
        if (topicName == null && request.getTopicId() != null) {
            try {
                Lesson lesson = lessonRepository.findById(request.getTopicId()).orElse(null);
                if (lesson != null) {
                    topicName = lesson.getName();
                }
            } catch (Exception e) {
                logger.warn("Could not fetch lesson name for topicId: {}, using provided topic", request.getTopicId(), e);
            }
        }
        
        // Use the same question generation logic as create-exam
        String details = "Level: " + levelName + ", Topic: " + topicName + ", Language: " + request.getCourseLang();
        
        // Get question count and answer count from app config (same as create-exam)
        int questionCount = 10; // default
        int answerCount = 5; // default
        
        try {
            // You might want to get these from AppConfigRepository like in the original code
            // For now, using defaults
        } catch (Exception e) {
            logger.warn("Could not fetch question count from AppConfig, defaulting to 10", e);
        }

        logger.info("Calling Llama service for level: {} and topic: {}", request.getLevel(), request.getTopic());
        
        // Generate questions using the same logic as create-exam
        List<SessionTestQuestionDto> questionDtos = llamaService.generateAssessmentQuestions(
                details, questionCount, answerCount, false, request.getCourseLang(), request.getCourseLang());

        if (questionDtos == null || questionDtos.isEmpty()) {
            logger.warn("No questions generated for level: {} and topic: {}", request.getLevel(), request.getTopic());
            return new ArrayList<>();
        }

        logger.info("Generated {} questions for level: {} and topic: {}", questionDtos.size(), request.getLevel(), request.getTopic());

        // Convert SessionTestQuestionDto to QuestionBank entities
        List<QuestionBank> questionBanks = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        for (SessionTestQuestionDto dto : questionDtos) {
            QuestionBank questionBank = new QuestionBank();
            questionBank.setQuestionText(dto.getQuestionText());
            questionBank.setOptions(gson.toJson(dto.getOptions()));
            questionBank.setCorrectLabel(dto.getCorrectLabel());
            questionBank.setExplanation(dto.getExplanation());
            questionBank.setLevelId(request.getLevelId());
            questionBank.setLessonId(request.getTopicId());
            questionBank.setCourseLang(request.getCourseLang());
            questionBank.setAssessment("CEFR".equalsIgnoreCase(request.getLevel()));
            questionBank.setCreatedDatetime(now);
            questionBanks.add(questionBank);
        }

        logger.info("Converted {} questions to QuestionBank entities", questionBanks.size());
        return questionBanks;
    }

    private List<QuestionBank> generateQuestionsForLesson(Lesson lesson, String courseLang) {
        logger.info("Starting question generation for lesson: {}", lesson.getName());
        
        // Get level name from database
        String levelName = "Unknown Level";
        try {
            LessonLevel level = lessonLevelRepository.findById(lesson.getLevelId()).orElse(null);
            if (level != null) {
                levelName = level.getName();
            }
        } catch (Exception e) {
            logger.warn("Could not fetch level name for levelId: {}, using default", lesson.getLevelId(), e);
        }
        
        // Use the same question generation logic as create-exam
        String details = "Level: " + levelName + ", Topic: " + lesson.getName() + ", Language: " + courseLang;
        
        // Get question count and answer count from app config (same as create-exam)
        int questionCount = 10; // default
        int answerCount = 5; // default
        
        try {
            // You might want to get these from AppConfigRepository like in the original code
            // For now, using defaults
        } catch (Exception e) {
            logger.warn("Could not fetch question count from AppConfig, defaulting to 10", e);
        }

        logger.info("Calling Llama service for lesson: {}", lesson.getName());
        
        // Generate questions using the same logic as create-exam
        List<SessionTestQuestionDto> questionDtos = llamaService.generateAssessmentQuestions(
                details, questionCount, answerCount, false, courseLang, courseLang);

        if (questionDtos == null || questionDtos.isEmpty()) {
            logger.warn("No questions generated for lesson: {}", lesson.getName());
            return new ArrayList<>();
        }

        logger.info("Generated {} questions for lesson: {}", questionDtos.size(), lesson.getName());

        // Convert SessionTestQuestionDto to QuestionBank entities
        List<QuestionBank> questionBanks = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        for (SessionTestQuestionDto dto : questionDtos) {
            QuestionBank questionBank = new QuestionBank();
            questionBank.setQuestionText(dto.getQuestionText());
            questionBank.setOptions(gson.toJson(dto.getOptions()));
            questionBank.setCorrectLabel(dto.getCorrectLabel());
            questionBank.setExplanation(dto.getExplanation());
            questionBank.setLevelId(lesson.getLevelId());
            questionBank.setLessonId(lesson.getId());
            questionBank.setCourseLang(courseLang);
            questionBank.setAssessment(false);
            questionBank.setCreatedDatetime(now);
            
            questionBanks.add(questionBank);
        }

        logger.info("Converted {} questions to QuestionBank entities for lesson: {}", questionBanks.size(), lesson.getName());
        return questionBanks;
    }
    
    
    @Override
    public List<QuestionBank> getQuestionsByLevelAndLessonAndLanguage(Integer levelId, Integer lessonId, String courseLang, boolean isAssessment) {
        logger.info("Getting questions from question bank for levelId: {}, lessonId: {}, courseLang: {}, isAssessment: {}", 
                levelId, lessonId, courseLang, isAssessment);
        
        if (isAssessment) {
            // For assessment tests, get questions by course language and assessment flag
            return questionBankRepository.findByCourseLangAndAssessment(courseLang);
        } else {
            // For regular tests, get questions by level, lesson, and course language
            return questionBankRepository.findByLevelIdAndLessonIdAndCourseLang(levelId, lessonId, courseLang);
        }
    }
    
    @Override
    public List<SessionTestQuestionDto> convertToSessionTestQuestionDtos(List<QuestionBank> questionBanks) {
        if (questionBanks == null || questionBanks.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<SessionTestQuestionDto> questionDtos = new ArrayList<>();
        
        for (QuestionBank questionBank : questionBanks) {
            SessionTestQuestionDto dto = new SessionTestQuestionDto();
            dto.setQuestionText(questionBank.getQuestionText());
            dto.setCorrectLabel(questionBank.getCorrectLabel());
            dto.setExplanation(questionBank.getExplanation());
            dto.setId(questionBank.getId());
            
            // Parse options from JSON
            try {
                List<SessionTestQuestionDto.Option> options = gson.fromJson(questionBank.getOptions(), 
                    new com.google.gson.reflect.TypeToken<List<SessionTestQuestionDto.Option>>(){}.getType());
                dto.setOptions(options);
            } catch (Exception e) {
                logger.error("Error parsing options JSON for question bank ID: {}", questionBank.getId(), e);
                // Set empty options if parsing fails
                dto.setOptions(new ArrayList<>());
            }
            
            questionDtos.add(dto);
        }
        
        logger.info("Converted {} QuestionBank entities to SessionTestQuestionDto objects", questionDtos.size());
        return questionDtos;
    }
    
    @Override
    public CompletableFuture<String> createLevelQuestionBank(CreateLevelQuestionBankRequestDto request) {
        // Validate language upfront before starting background process
        if (request.getCourseLang() == null || request.getCourseLang().trim().isEmpty()) {
            throw new IllegalArgumentException("Course language cannot be null or empty");
        }
        
        // Validate that the language exists and is active by calling getFullLanguageName
        // This will throw IllegalArgumentException if language is not found or not active
        try {
            // We need to access the LlamaService to validate the language
            // Since getFullLanguageName is private, we'll create a simple validation here
            validateCourseLanguage(request.getCourseLang());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid language configuration: " + e.getMessage());
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (Boolean.TRUE.equals(request.getAssessment())) {
                    // Handle assessment mode (CEFR)
                    logger.info("Starting background assessment question bank creation for courseLang: {}", 
                            request.getCourseLang());
                    
                    return createAssessmentQuestionBank(request.getCourseLang());
                } else {
                    // Handle regular level mode
                    if (request.getLevelId() == null) {
                        throw new RuntimeException("levelId is required when assessment is false");
                    }
                    
                    logger.info("Starting background question bank creation for levelId: {}, courseLang: {}", 
                            request.getLevelId(), request.getCourseLang());
                    
                    return createLevelQuestionBankForLessons(request.getLevelId(), request.getCourseLang());
                }
                
            } catch (Exception e) {
                logger.error("Error in background question bank creation for levelId: {}, courseLang: {}, assessment: {}", 
                        request.getLevelId(), request.getCourseLang(), request.getAssessment(), e);
                throw new RuntimeException("Failed to create question bank: " + e.getMessage(), e);
            }
        });
    }
    
    private String createAssessmentQuestionBank(String courseLang) {
        try {
            logger.info("Creating assessment questions for courseLang: {}", courseLang);
            
            // Generate assessment questions using the same logic as create-exam
            String details = "Language: " + courseLang;
            
            // Get question count from app config
            int questionCount = 20; // default for assessment
            try {
                AppConfig qConfig = appConfigRepository.findByKey("assessment_question_count","ai");
                if (qConfig != null && qConfig.getValue() != null) {
                    questionCount = Integer.parseInt(qConfig.getValue());
                }
            } catch (Exception e) {
                logger.warn("Could not fetch assessment_question_count from AppConfig, defaulting to 20", e);
            }
            
            logger.info("Generating {} assessment questions for courseLang: {}", questionCount, courseLang);
            
            // Generate questions using Llama
            List<SessionTestQuestionDto> questionDtos = llamaService.generateAssessmentQuestions(
                    details, questionCount, 5, true, courseLang, courseLang);
            
            if (questionDtos == null || questionDtos.isEmpty()) {
                logger.warn("No assessment questions generated for courseLang: {}", courseLang);
                return "No assessment questions generated for courseLang: " + courseLang;
            }
            
            logger.info("Generated {} assessment questions for courseLang: {}", questionDtos.size(), courseLang);
            
            // Convert to QuestionBank entities
            List<QuestionBank> questionBanks = new ArrayList<>();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            for (SessionTestQuestionDto dto : questionDtos) {
                QuestionBank questionBank = new QuestionBank();
                questionBank.setQuestionText(dto.getQuestionText());
                questionBank.setOptions(gson.toJson(dto.getOptions()));
                questionBank.setCorrectLabel(dto.getCorrectLabel());
                questionBank.setExplanation(dto.getExplanation());
                questionBank.setLevelId(null); // No level for assessment
                questionBank.setLessonId(null); // No lesson for assessment
                questionBank.setCourseLang(courseLang);
                questionBank.setAssessment(true);
                questionBank.setCreatedDatetime(now);
                questionBanks.add(questionBank);
            }
            
            // Save questions to database
            List<QuestionBank> savedQuestions = questionBankRepository.saveAll(questionBanks);
            
            String result = String.format("Successfully created %d assessment questions for courseLang: %s", 
                    savedQuestions.size(), courseLang);
            
            logger.info(result);
            return result;
            
        } catch (Exception e) {
            logger.error("Error creating assessment question bank for courseLang: {}", courseLang, e);
            throw new RuntimeException("Failed to create assessment question bank: " + e.getMessage(), e);
        }
    }
    
    private String createLevelQuestionBankForLessons(Integer levelId, String courseLang) {
        // Get level name from lesson_level table
        LessonLevel level = lessonLevelRepository.findById(levelId)
                .orElseThrow(() -> new RuntimeException("Level not found with ID: " + levelId));
        
        logger.info("Found level: {} (ID: {})", level.getName(), level.getId());
        
        // Get all lessons for the given level
        List<Lesson> lessons = lessonRepository.findAll().stream()
                .filter(lesson -> lesson.getLevelId() != null && lesson.getLevelId().equals(levelId))
                .collect(Collectors.toList());
        
        logger.info("Found {} lessons for level: {}", lessons.size(), level.getName());
        
        if (lessons.isEmpty()) {
            logger.warn("No lessons found for level: {}", level.getName());
            return "No lessons found for level: " + level.getName();
        }
        
        int totalQuestionsCreated = 0;
        int successfulLessons = 0;
        
        // Generate questions for each lesson
        for (Lesson lesson : lessons) {
            try {
                logger.info("Generating questions for lesson: {} (ID: {})", lesson.getName(), lesson.getId());
                
                List<QuestionBank> questions = generateQuestionsForLesson(lesson, courseLang);
                
                if (questions != null && !questions.isEmpty()) {
                    // Save questions to database
                    List<QuestionBank> savedQuestions = questionBankRepository.saveAll(questions);
                    totalQuestionsCreated += savedQuestions.size();
                    successfulLessons++;
                    
                    logger.info("Successfully created {} questions for lesson: {}", 
                            savedQuestions.size(), lesson.getName());
                } else {
                    logger.warn("No questions generated for lesson: {}", lesson.getName());
                }
                
                // Add a small delay between lessons to avoid overwhelming the system
                Thread.sleep(1000);
                
            } catch (Exception e) {
                logger.error("Error generating questions for lesson: {} (ID: {})", 
                        lesson.getName(), lesson.getId(), e);
            }
        }
        
        String result = String.format("Successfully created %d questions for %d out of %d lessons in level: %s", 
                totalQuestionsCreated, successfulLessons, lessons.size(), level.getName());
        
        logger.info(result);
        return result;
    }
} 
