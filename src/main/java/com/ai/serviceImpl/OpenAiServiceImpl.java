package com.ai.serviceImpl;

import com.ai.config.OpenAIConfig;
import com.ai.dto.ChatRequest;
import com.ai.dto.ChatResponse;
import com.ai.dto.MessageDto;
import com.ai.dto.SimpleMessageDto;
import com.ai.dto.SessionTestQuestionDto;
import com.ai.prompts.TranslationPromptBuilder;
import com.ai.service.OpenAiService;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;

import com.ai.repository.AppConfigRepository;
import com.ai.repository.CourseLanguageRepository;
import com.ai.entities.AppConfig;
import com.ai.entities.CourseLanguage;

@Service
public class OpenAiServiceImpl implements OpenAiService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiServiceImpl.class);
    private static final String MODEL_GPT_4O_MINI = "gpt-4o-mini";
    private static final String MODEL_GPT_4O = "gpt-4o";
    private static final double DEFAULT_COMPLEXITY_THRESHOLD = 0.5;
    private static final double DEFAULT_CONTEXT_HANDLING_THRESHOLD = 0.5;
    private static final double HIGH_COMPLEXITY_THRESHOLD = 0.7;
    private static final double HIGH_CONTEXT_HANDLING_THRESHOLD = 0.7;



    private final OpenAIConfig config;
    private final RestTemplate restTemplate;
    private final Gson gson;
    private final AppConfigRepository appConfigRepository;
    private final CourseLanguageRepository courseLanguageRepository;

    private final List<String> contextHistory = new LinkedList<>();

    @Autowired
    public OpenAiServiceImpl(OpenAIConfig config, @Qualifier("openaiRestTemplate") RestTemplate restTemplate, Gson gson, AppConfigRepository appConfigRepository, CourseLanguageRepository courseLanguageRepository) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.gson = gson;
        this.appConfigRepository = appConfigRepository;
        this.courseLanguageRepository = courseLanguageRepository;
    }


    private void loadProfiles(String profilePath) throws LangDetectException, IOException {
        File profilesDir = new File(profilePath);
        if (profilesDir.isDirectory()) {
            String[] profileFiles = profilesDir.list();
            if (profileFiles != null) {
                for (String profileFile : profileFiles) {
                    logger.info("Profile found: {}", profileFile);
                }
            } else {
                logger.warn("No profile files found in the directory.");
            }
        } else {
            logger.warn("Profiles directory does not exist: {}", profilePath);
        }
        DetectorFactory.loadProfile(profilePath);
    }

    @Override
    public ChatResponse callOpenAiApi(String modelName, String prompt, String language, String languageLevel, String topic, String tutor, List<MessageDto> pastDialogue) {
        String selectedModelName = "auto".equals(modelName) ? selectModel(prompt) : modelName;

        OpenAIConfig.ChatGpt.Model model = config.getChatgpt().getModels().stream()
                .filter(m -> m.getName().equals(selectedModelName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + selectedModelName));

        logger.info("Selected model: {}", model.getName());
        logger.info("Prompt: {}", prompt);
        logger.info("Language: {}", language);
        logger.info("Language Level: {}", languageLevel);
        logger.info("Topic: {}", topic);

        // Create request body
        ChatRequest requestBody = createRequestBody(model, prompt, language, languageLevel, topic, tutor, pastDialogue);
        logger.info("Request: {}", gson.toJson(requestBody));

        ChatResponse response = restTemplate.postForObject(config.getUrl(), requestBody, ChatResponse.class);
        if (response != null) {
            response.setLanguage(language);
            response.setLanguageLevel(languageLevel);
            logger.info("Response returning: {}", gson.toJson(response));
        } else {
            logger.warn("Received null response from OpenAI API");
        }


        return response;
    }

    private String selectModel(String prompt) {
        // Detect the language of the input text
        String detectedLanguage = detectLanguage(prompt);
        // Measure sentence length, vocabulary richness, and syntactic complexity
        double complexity = ComplexityAssessment.assessComplexity(prompt);
        // Assess how well the system can handle context by tracking the context history length
        double contextHandling = handleContext(prompt);

        // Higher thresholds for complexity and contextHandling if detected language is English or German
        double complexityThreshold = "en".equals(detectedLanguage) || "de".equals(detectedLanguage) ? HIGH_COMPLEXITY_THRESHOLD : DEFAULT_COMPLEXITY_THRESHOLD;
        double contextHandlingThreshold = "en".equals(detectedLanguage) || "de".equals(detectedLanguage) ? HIGH_CONTEXT_HANDLING_THRESHOLD : DEFAULT_CONTEXT_HANDLING_THRESHOLD;

        logger.info("Detected language: {}", detectedLanguage);
        logger.info("Complexity: {}", complexity);
        logger.info("Context Handling: {}", contextHandling);

        // Decide which model to use based on the criteria
        if (complexity < complexityThreshold && contextHandling < contextHandlingThreshold) {
            return MODEL_GPT_4O_MINI;
        } else {
            return MODEL_GPT_4O;
        }
    }

    private ChatRequest createRequestBody(OpenAIConfig.ChatGpt.Model model, String prompt, String language, String languageLevel, String topic, String tutor, List<MessageDto> pastDialogue) {
        ChatRequest chatRequest = new ChatRequest(model.getName(), prompt, language, languageLevel, topic, tutor, pastDialogue);
        chatRequest.setTemperature(model.getSettings().getTemperature());
        chatRequest.setMax_tokens(model.getSettings().getMaxTokens());
        chatRequest.setTop_p(model.getSettings().getTopP());
        chatRequest.setFrequency_penalty(model.getSettings().getFrequencyPenalty());
        chatRequest.setPresence_penalty(model.getSettings().getPresencePenalty());
        return chatRequest;
    }

    private String detectLanguage(String text) {
        try {
            Detector detector = DetectorFactory.create();
            detector.append(text);
            return detector.detect();
        } catch (LangDetectException e) {
            logger.error("Error detecting language", e);
            return "unknown";
        }
    }

    private double handleContext(String text) {
        addToContext(text);
        return contextHistory.size() / 10.0; // Normalizing by a context history length of 10
    }

    private void addToContext(String text) {
        if (contextHistory.size() >= 10) {
            contextHistory.remove(0);
        }
        contextHistory.add(text);
    }

    private String getFullLanguageName(String languageCode) {
        if (languageCode == null || languageCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Language code cannot be null or empty");
        }
        
        CourseLanguage courseLanguage = courseLanguageRepository.findByCodeAndActiveTrue(languageCode.trim())
                .orElseThrow(() -> new IllegalArgumentException("Language code '" + languageCode + "' is not found among active course languages"));
        
        return courseLanguage.getLang();
    }

    private String getExampleResponseStructure() {
        return "EXAMPLE RESPONSE STRUCTURE:\n" +
               "[\n" +
               "  {\n" +
               "    \"questionText\": \"Complete the sentence: 'I _____ to school every day.'\",\n" +
               "    \"options\": [\n" +
               "      {\"label\": \"A\", \"text\": \"go\"},\n" +
               "      {\"label\": \"B\", \"text\": \"goes\"},\n" +
               "      {\"label\": \"C\", \"text\": \"going\"},\n" +
               "      {\"label\": \"D\", \"text\": \"went\"},\n" +
               "      {\"label\": \"E\", \"text\": \"gone\"}\n" +
               "    ],\n" +
               "    \"correctLabel\": \"A\",\n" +
               "    \"explanation\": \"'I go to school every day' is correct. We use the simple present tense for habitual actions and routines. The first person singular 'I' takes the base form of the verb 'go'.\"\n" +
               "  },\n" +
               "  {\n" +
               "    \"questionText\": \"Which sentence uses the correct article?\",\n" +
               "    \"options\": [\n" +
               "      {\"label\": \"A\", \"text\": \"I have a apple.\"},\n" +
               "      {\"label\": \"B\", \"text\": \"I have an apple.\"},\n" +
               "      {\"label\": \"C\", \"text\": \"I have the apple.\"},\n" +
               "      {\"label\": \"D\", \"text\": \"I have apple.\"},\n" +
               "      {\"label\": \"E\", \"text\": \"I have some apple.\"}\n" +
               "    ],\n" +
               "    \"correctLabel\": \"B\",\n" +
               "    \"explanation\": \"'I have an apple' is correct. We use 'an' before words that begin with a vowel sound. 'Apple' starts with the vowel sound /æ/, so we use 'an' instead of 'a'.\"\n" +
               "  }\n" +
               "]";
    }

    @Override
    public String evaluateLanguageLevel(String language, List<SimpleMessageDto> conversation) {
        String prompt = "Evaluate the language proficiency level in " + language + " for the following conversation:\n";
        for (SimpleMessageDto message : conversation) {
            prompt += message.getSender() + ": " + message.getMessage() + "\n";
        }

        String systemMessage = "You are an expert linguist capable of evaluating language proficiency. Only provide the CEFR language proficiency level (A1, A2, B1, B2, C1, C2) based on the given conversation.";

        ChatRequest requestBody = new ChatRequest("gpt-4o", systemMessage, language, "any", "any", "", null);
        requestBody.getMessages().clear(); // clear initial messages
        requestBody.getMessages().add(new ChatRequest.Message("system", systemMessage));
        requestBody.getMessages().add(new ChatRequest.Message("user", prompt));
        requestBody.setTemperature(0.0);
        requestBody.setMax_tokens(3);
        requestBody.setTop_p(1.0);
        requestBody.setFrequency_penalty(0.0);
        requestBody.setPresence_penalty(0.0);

        ChatResponse response = restTemplate.postForObject(config.getUrl(), requestBody, ChatResponse.class);
        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent().trim();
        } else {
            logger.warn("Received null or empty response from OpenAI API for language level evaluation");
            return "unknown";
        }
    }

    @Override
    public String translate(String inputLanguage, String outputLanguage, String message) {
        // Build the translation prompt using TranslationPromptBuilder
        TranslationPromptBuilder promptBuilder = new TranslationPromptBuilder("gpt-4o", inputLanguage, outputLanguage, message);
        String prompt = promptBuilder.build(inputLanguage, null);

        // Create request
        ChatRequest requestBody = new ChatRequest("gpt-4o", prompt, inputLanguage, "any", "translation", null, null);
        requestBody.setTemperature(0.3);
        requestBody.setMax_tokens(1000);
        requestBody.setTop_p(1.0);
        requestBody.setFrequency_penalty(0.0);
        requestBody.setPresence_penalty(0.0);

        // Call OpenAI API
        ChatResponse response = restTemplate.postForObject(config.getUrl(), requestBody, ChatResponse.class);

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent().trim();
        } else {
            logger.warn("Received null or empty response from OpenAI API for translation");
            return "Translation failed";
        }
    }

    @Override
    public Map<String, Object> translateJsonFile(String englishJson, String targetLanguage) {
        // Build a prompt that instructs the model to translate only the values.
        String prompt = "Translate the following JSON object from English to " + targetLanguage + ". "
                + "Do not modify any keys—only translate the values. "
                + "Return the result as a valid JSON object with the same structure. "
                + "Use response_format={\"type\": \"json_object\"}.\n\n"
                + englishJson;

        // Print the prompt for debugging
        System.out.println("Translation Prompt: " + prompt);

        // Create a ChatRequest using the GPT-4o-mini model explicitly.
        ChatRequest requestBody = new ChatRequest(MODEL_GPT_4O_MINI, prompt, "en", "any", "translation", null, null);
        requestBody.setTemperature(0.3);
        requestBody.setMax_tokens(10000);
        requestBody.setTop_p(1.0);
        requestBody.setFrequency_penalty(0.0);
        requestBody.setPresence_penalty(0.0);

        // Print the request body details
        System.out.println("Request Body: " + gson.toJson(requestBody));

        // Call the OpenAI API
        ChatResponse response = restTemplate.postForObject(config.getUrl(), requestBody, ChatResponse.class);

        // Print the raw response from OpenAI
        System.out.println("Raw Response: " + gson.toJson(response));

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            String translatedJson = response.getChoices().get(0).getMessage().getContent().trim();

            // Remove any characters before the first '{'
            int jsonStartIndex = translatedJson.indexOf("{");
            if (jsonStartIndex > 0) {
                translatedJson = translatedJson.substring(jsonStartIndex);
            }

            // Remove any trailing triple backticks
            int jsonEndIndex = translatedJson.lastIndexOf("```");
            if (jsonEndIndex > 0) {
                translatedJson = translatedJson.substring(0, jsonEndIndex);
            }

            System.out.println("Cleaned Translated JSON String: " + translatedJson);

            // Parse the returned JSON string into a Map using Gson
            try {
                Map<String, Object> result = gson.fromJson(translatedJson, Map.class);
                System.out.println("Parsed Translated JSON as Map: " + result);
                return result;
            } catch (Exception e) {
                System.out.println("Failed to parse translated JSON: " + e.getMessage());
                System.out.println("Response content causing error: " + translatedJson);

                logger.error("Failed to parse the translated JSON", e);
                return null;
            }
        } else {
            System.out.println("No translation received from OpenAI API for JSON translation.");
            logger.warn("No translation received from OpenAI API for JSON translation");
            return null;
        }
    }

    @Override
    public List<SessionTestQuestionDto> generateAssessmentQuestions(String details, int questionCount, int answerCount, boolean isAssessment, String courseLang, String userLang) {
        String prompt;
        if (isAssessment) {
            prompt = buildAssessmentPrompt(questionCount, answerCount, courseLang, userLang);
        } else {
            prompt = buildRegularPrompt(questionCount, answerCount, details, courseLang, userLang);
        }
        logger.info("[OpenAI prompt] {}", prompt);

        // int maxTokens = isAssessment ? Math.max(600, Math.min(questionCount * 300, 4000)) : 2000;
        int maxTokens = 12000;
        int maxRetries = 3;
        int attempt = 0;
        Exception lastException = null;
        while (attempt < maxRetries) {
            attempt++;
            String model = "gpt-4.1-mini";
            int tokensToUse = (attempt == 1) ? maxTokens : 12000; // Use a huge number on retry
            ChatRequest requestBody = new ChatRequest(model, prompt, "en", "any", "test", null, null);
            requestBody.setTemperature(0.7);
            requestBody.setMax_tokens(tokensToUse);
            requestBody.setTop_p(1.0);
            requestBody.setFrequency_penalty(0.0);
            requestBody.setPresence_penalty(0.0);
            // Enable JSON mode
            Map<String, String> responseFormat = new HashMap<>();
            responseFormat.put("type", "json_object");
            requestBody.setResponse_format(responseFormat);
            logger.info("Max tokens: {} (attempt {})", tokensToUse, attempt);
            ChatResponse response = restTemplate.postForObject(config.getUrl(), requestBody, ChatResponse.class);
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String content = response.getChoices().get(0).getMessage().getContent().trim();
                logger.info("[OpenAI raw response] {}", content);
                // With JSON mode, the response should already be valid JSON
                // Just ensure we have the content
                if (content == null || content.trim().isEmpty()) {
                    throw new IllegalStateException("OpenAI returned empty content");
                }
                
                logger.info("[OpenAI content to parse] {}", content);
                try {
                    // With JSON mode, we expect a JSON object with a questions array
                    // The response format could be: {"questions": [...]} or {"response": [...]} or direct array
                    Type listType = new TypeToken<List<SessionTestQuestionDto>>(){}.getType();
                    List<SessionTestQuestionDto> questions;
                    
                    // Try to parse as direct array first
                    if (content.startsWith("[")) {
                        questions = gson.fromJson(content, listType);
                    } else {
                        // Try to parse as object with questions or response field
                        Map<String, Object> responseMap = gson.fromJson(content, Map.class);
                        if (responseMap.containsKey("questions")) {
                            questions = gson.fromJson(gson.toJson(responseMap.get("questions")), listType);
                        } else if (responseMap.containsKey("response")) {
                            questions = gson.fromJson(gson.toJson(responseMap.get("response")), listType);
                        } else {
                            throw new IllegalStateException("OpenAI response does not contain questions or response array");
                        }
                    }
                    
                    // Validate the parsed questions
                    if (questions != null && validateQuestions(questions)) {
                        return questions;
                    } else {
                        throw new IllegalStateException("Parsed questions failed validation");
                    }
                } catch (Exception e) {
                    logger.warn("[OpenAI parse error] {}", e.getMessage());
                    logger.error("Failed to parse generated questions JSON (attempt " + attempt + ")", e);
                    lastException = e;
                    // Retry if not last attempt
                }
            } else {
                logger.warn("No questions received from OpenAI (attempt {})", attempt);
            }
        }
        // If all attempts fail, return null or throw last exception
        if (lastException != null) {
            throw new RuntimeException("Failed to get valid questions from OpenAI after " + maxRetries + " attempts", lastException);
        }
        return null;
    }



    // Helper method to validate parsed questions
    private boolean validateQuestions(List<SessionTestQuestionDto> questions) {
        if (questions == null || questions.isEmpty()) {
            logger.warn("[Validation] Questions list is null or empty");
            return false;
        }
        
        for (int i = 0; i < questions.size(); i++) {
            SessionTestQuestionDto question = questions.get(i);
            
            // Check required fields
            if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
                logger.warn("[Validation] Question {} has empty questionText", i);
                return false;
            }
            
            if (question.getOptions() == null || question.getOptions().isEmpty()) {
                logger.warn("[Validation] Question {} has no options", i);
                return false;
            }
            
            if (question.getCorrectLabel() == null || question.getCorrectLabel().trim().isEmpty()) {
                logger.warn("[Validation] Question {} has no correctLabel", i);
                return false;
            }
            
            // Validate options
            for (int j = 0; j < question.getOptions().size(); j++) {
                SessionTestQuestionDto.Option option = question.getOptions().get(j);
                if (option.getLabel() == null || option.getText() == null || 
                    option.getLabel().trim().isEmpty() || option.getText().trim().isEmpty()) {
                    logger.warn("[Validation] Question {} option {} has invalid label or text", i, j);
                    return false;
                }
            }
            
            // Check if correctLabel exists in options
            boolean correctLabelExists = question.getOptions().stream()
                .anyMatch(opt -> question.getCorrectLabel().equals(opt.getLabel()));
            if (!correctLabelExists) {
                logger.warn("[Validation] Question {} correctLabel '{}' not found in options", i, question.getCorrectLabel());
                return false;
            }
        }
        
        logger.info("[Validation] All {} questions passed validation", questions.size());
        return true;
    }

    // Updated prompt builder for assessment
    private String buildAssessmentPrompt(int questionCount, int answerCount, String courseLang, String userLang) {
        String assessmentDetails = "General language skills, mixed difficulty, grammar, vocabulary, comprehension";
        
        // Determine the language to use for explanations
        String explanationLang = (userLang == null || userLang.equals(courseLang)) ? courseLang : userLang;
        
        // Get full language names for better clarity
        String courseLangFull = getFullLanguageName(courseLang);
        String explanationLangFull = getFullLanguageName(explanationLang);
        
        return "Create " + questionCount + " language assessment questions in " + courseLangFull + ". " +
                "Each question should have " + answerCount + " options labeled A, B, C, D, E. " +
                "Include explanations in " + explanationLangFull + ". " +
                "Focus on grammar, vocabulary, and comprehension. " +
                "Return a JSON array with this structure: " +
                getExampleResponseStructure() + "\n" +
                "Context: " + assessmentDetails;
    }

    // Updated prompt builder for regular
    private String buildRegularPrompt(int questionCount, int answerCount, String details, String courseLang, String userLang) {
        String level = extractLevelFromDetails(details);
        String levelSpecificInstructions = getLevelSpecificInstructions(level);
        
        // Determine the language to use for explanations
        String explanationLang = (userLang == null || userLang.equals(courseLang)) ? courseLang : userLang;
        
        // Get full language names for better clarity
        String courseLangFull = getFullLanguageName(courseLang);
        String explanationLangFull = getFullLanguageName(explanationLang);
        
        return "Create " + questionCount + " language practice questions in " + courseLangFull + " about the topic in the context. " +
                "Each question should have " + answerCount + " options labeled A, B, C, D, E. " +
                "Include explanations in " + explanationLangFull + ". " +
                levelSpecificInstructions +
                "Return a JSON array with this structure: " +
                getExampleResponseStructure() + "\n" +
                "Context: " + details;
    }

    // Helper method to extract level from details string
    private String extractLevelFromDetails(String details) {
        if (details == null || details.isEmpty()) {
            return "UNKNOWN";
        }
        
        // Look for "Level: A1", "Level: A2", etc.
        if (details.contains("Level: A1")) {
            return "A1";
        } else if (details.contains("Level: A2")) {
            return "A2";
        } else if (details.contains("Level: B1")) {
            return "B1";
        } else if (details.contains("Level: B2")) {
            return "B2";
        } else if (details.contains("Level: C1")) {
            return "C1";
        } else if (details.contains("Level: C2")) {
            return "C2";
        }
        
        return "UNKNOWN";
    }

    // Helper method to get level-specific instructions
    private String getLevelSpecificInstructions(String level) {
        switch (level) {
            case "A1":
                return "CRITICAL: For A1 level practice, use ONLY the most basic vocabulary (common everyday words). " +
                       "Use ONLY simple present tense and basic present continuous. " +
                       "Use ONLY basic question forms (What is...? Which is...? etc.). " +
                       "NO complex grammar structures. NO past tense, future tense, or conditional sentences. " +
                       "Focus on basic vocabulary recognition and simple comprehension. " +
                       "Vocabulary should be limited to the most common 500-1000 words only. " +
                       "Questions should be about very simple, everyday situations. " +
                       "Use simple, short sentences with basic word order. " +
                       "IMPORTANT: All questions MUST be directly related to the given topic. " +
                       "Focus questions on the topic using basic question forms appropriate for that topic. " +
                       "Do NOT ask questions unrelated to the specified topic. " +
                       "Make questions specific and clear - avoid vague questions that assume context not provided. " +
                       "Use concrete, observable facts rather than hypothetical situations. " +
                       "For A1 level, provide clearly distinct answer choices - avoid very similar or confusing options. " +
                       "Answer choices should be obviously different from each other. ";
            case "A2":
                return "CRITICAL: For A2 level practice, use simple vocabulary with some common phrases. " +
                       "Use present tense, simple past tense, and basic future (going to). " +
                       "Use basic question forms and simple statements. " +
                       "Use simple conjunctions (and, but, or). " +
                       "Focus on everyday situations and basic descriptions. " +
                       "Vocabulary should be common words (1000-2000 words) with some basic phrases. " +
                       "Questions should be about common everyday situations with slightly more detail than A1. " +
                       "Use simple sentences with basic grammar patterns. " +
                       "IMPORTANT: All questions MUST be directly related to the given topic. " +
                       "Focus questions on the topic using appropriate question forms for that topic. " +
                       "Do NOT ask questions unrelated to the specified topic. " +
                       "Make questions specific and clear - avoid vague questions that assume context not provided. " +
                       "Use concrete, observable facts rather than hypothetical situations. " +
                       "For A2 level, provide clearly distinct answer choices - avoid very similar or confusing options. " +
                       "Answer choices should be obviously different from each other. ";
            case "B1":
                return "For B1 level, use intermediate vocabulary and grammar structures. " +
                       "Include past tense, present perfect, and future forms. " +
                       "Use more complex sentence structures and conjunctions. " +
                       "Focus on practical communication and everyday situations. ";
            case "B2":
                return "For B2 level, use advanced vocabulary and complex grammar structures. " +
                       "Include all tenses, passive voice, and conditional sentences. " +
                       "Use sophisticated language and complex sentence structures. " +
                       "Focus on detailed communication and abstract concepts. ";
            case "C1":
                return "For C1 level, use sophisticated vocabulary and complex grammar structures. " +
                       "Include advanced grammar patterns, idiomatic expressions, and nuanced language. " +
                       "Use complex sentence structures and academic language. " +
                       "Focus on abstract concepts and detailed analysis. ";
            case "C2":
                return "For C2 level, use highly sophisticated vocabulary and complex grammar structures. " +
                       "Include advanced grammar patterns, idiomatic expressions, and nuanced language. " +
                       "Use complex sentence structures and academic language. " +
                       "Focus on abstract concepts, detailed analysis, and nuanced communication. ";
            default:
                return "Adjust the language complexity based on the level - use simpler structures and vocabulary for beginner levels, " +
                       "and more complex language constructs for advanced levels. ";
        }
    }

    @Override
    public List<SessionTestQuestionDto> generateSessionTestQuestions(String details) {
        int questionCount = 10;
        int answerCount = 5; // default
        try {
            AppConfig config = appConfigRepository.findByKey("question_count","ai");
            if (config != null && config.getValue() != null) {
                questionCount = Integer.parseInt(config.getValue());
            }
        } catch (Exception e) {
            logger.warn("Could not fetch question count from AppConfig, defaulting to 10", e);
        }
        try {
            AppConfig answerConfig = appConfigRepository.findByKey("answer_count","ai");
            if (answerConfig != null && answerConfig.getValue() != null) {
                answerCount = Integer.parseInt(answerConfig.getValue());
            }
        } catch (Exception e) {
            logger.warn("Could not fetch answer count from AppConfig, defaulting to 5", e);
        }
        // Pass null for courseLang and userLang for backward compatibility
        return generateAssessmentQuestions(details, questionCount, answerCount, false, null, null);
    }

    @Override
    public String getSpeakingScoreAssessment(String prompt) {
        ChatRequest requestBody = new ChatRequest("gpt-4o", prompt, "en", "any", "assessment", null, null);
        requestBody.setTemperature(0.3);
        requestBody.setMax_tokens(500);
        requestBody.setTop_p(1.0);
        requestBody.setFrequency_penalty(0.0);
        requestBody.setPresence_penalty(0.0);
        ChatResponse response = restTemplate.postForObject(config.getUrl(), requestBody, ChatResponse.class);
        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent().trim();
        } else {
            return null;
        }
    }
    
    @Override
    public void validateCourseLanguage(String languageCode) {
        // This will throw IllegalArgumentException if language is not found or not active
        getFullLanguageName(languageCode);
    }
}
