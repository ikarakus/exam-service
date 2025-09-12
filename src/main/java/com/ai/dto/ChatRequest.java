package com.ai.dto;

import com.ai.prompts.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatRequest {

    private String model;
    private List<Message> messages;
    private double temperature;
    private int max_tokens;
    private double top_p;
    private double frequency_penalty;
    private double presence_penalty;
    private Object response_format;

    public ChatRequest(String model, String prompt, String language, String languageLevel, String topic, String tutor, List<MessageDto> pastDialogue) {
        this.model = model;
        this.messages = new ArrayList<>();

        // Decide which builder to use based on languageLevel
        if ("CEFR".equalsIgnoreCase(languageLevel)) {
            BaseTestPromptBuilder promptBuilder = getTestPromptBuilder(model, topic, tutor, language);
            if (promptBuilder instanceof com.ai.prompts.AssessmentPromptBuilder) {
                // Only for chat/Ass mode, set free conversation mode
                ((com.ai.prompts.AssessmentPromptBuilder) promptBuilder).setFreeConversationMode(true);
            }
            if (pastDialogue != null && !pastDialogue.isEmpty()) {
                promptBuilder.appendPastDialogue(pastDialogue);
            }
            promptBuilder.appendUserPrompt(prompt);
            String systemMessage = promptBuilder.build(language, languageLevel);
            this.messages.add(new Message("user", prompt));
            this.messages.add(new Message("system", systemMessage));
        } else {
            BasePromptBuilder promptBuilder = getPromptBuilder(model, topic, tutor, language);
            if (pastDialogue != null && !pastDialogue.isEmpty()) {
                promptBuilder.appendPastDialogue(pastDialogue);
            }
            promptBuilder.appendUserPrompt(prompt);
            String systemMessage = promptBuilder.build(language, languageLevel);
            this.messages.add(new Message("user", prompt));
            this.messages.add(new Message("system", systemMessage));
        }
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    private BasePromptBuilder getPromptBuilder(String model, String topic, String tutor, String language) {
        if ("english".equalsIgnoreCase(language) || "en".equalsIgnoreCase(language)) {
            return new EnglishPromptBuilder(model, topic, tutor);
        } else if ("german".equalsIgnoreCase(language) || "de".equalsIgnoreCase(language)) {
            return new GermanPromptBuilder(model, topic, tutor);
        } else if ("spanish".equalsIgnoreCase(language) || "es".equalsIgnoreCase(language)) {
            return new SpanishPromptBuilder(model, topic, tutor);
        } else if ("french".equalsIgnoreCase(language) || "fr".equalsIgnoreCase(language)) {
            return new FrenchPromptBuilder(model, topic, tutor);
        } else if ("turkish".equalsIgnoreCase(language) || "tr".equalsIgnoreCase(language)) {
            return new TurkishPromptBuilder(model, topic, tutor);
        }
        else {
            // Default fallback
            return new EnglishPromptBuilder(model, topic, tutor);
        }
    }

    private BaseTestPromptBuilder getTestPromptBuilder(String model, String topic, String tutor, String language) {
        return new com.ai.prompts.AssessmentPromptBuilder(model, topic, tutor);
    }

    // getters and setters
    // Getters and setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMax_tokens() {
        return max_tokens;
    }

    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }

    public double getTop_p() {
        return top_p;
    }

    public void setTop_p(double top_p) {
        this.top_p = top_p;
    }

    public double getFrequency_penalty() {
        return frequency_penalty;
    }

    public void setFrequency_penalty(double frequency_penalty) {
        this.frequency_penalty = frequency_penalty;
    }

    public double getPresence_penalty() {
        return presence_penalty;
    }

    public void setPresence_penalty(double presence_penalty) {
        this.presence_penalty = presence_penalty;
    }

    public Object getResponse_format() {
        return response_format;
    }

    public void setResponse_format(Object response_format) {
        this.response_format = response_format;
    }
}
