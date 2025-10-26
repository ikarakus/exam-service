package com.exam.prompts;

import java.util.List;
import com.exam.dto.MessageDto;

public abstract class BaseTestPromptBuilder {
    protected StringBuilder systemMessage = new StringBuilder();
    protected String tutor;
    protected String topic;
    protected boolean isChildFriendly;
    protected String userNickname;
    protected String ageRange;
    protected boolean isFirstMessage;

    public BaseTestPromptBuilder(String model, String topic, String tutor) {
        this(model, topic, tutor, false, null, null, false);
    }
    
    public BaseTestPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange) {
        this(model, topic, tutor, isChildFriendly, userNickname, ageRange, false);
    }
    
    public BaseTestPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange, boolean isFirstMessage) {
        this.tutor = tutor;
        this.topic = topic;
        this.isChildFriendly = isChildFriendly;
        this.userNickname = userNickname;
        this.ageRange = ageRange;
        this.isFirstMessage = isFirstMessage;
        // only change the topic if it is Ass
        if ("CEFR".equalsIgnoreCase(topic)) {
            topic = "General Language Assessment";
        }
        appendModelSpecificInstructions(model);
        appendTopicInstructions(topic);
        appendGeneralInstructions();
        appendPersonalizationInstructions();
        if (isFirstMessage) {
            appendFirstMessageInstructions();
        }
    }

    private void appendModelSpecificInstructions(String model) {
        String name = (tutor != null && !tutor.trim().isEmpty() && !"any".equalsIgnoreCase(tutor)) ? tutor : "your assessment AI assistant";
        if (model.equals("gpt-4o")) {
            systemMessage.append("You are ").append(name).append(", an assessment AI assistant focused on language evaluation. ");
        } else if (model.equals("gpt-4o-mini")) {
            systemMessage.append("You are ").append(name).append(", a concise assessment AI assistant. ");
        } else {
            systemMessage.append("You are ").append(name).append(", a helpful assessment AI assistant. ");
        }
    }

    private void appendTopicInstructions(String topic) {
        if (topic != null && !"any".equals(topic) && !topic.isEmpty()) {
            systemMessage.append("Assessment topic: ").append(topic).append(". ");
        } else {
            systemMessage.append("General language assessment. ");
        }
    }

    private void appendGeneralInstructions() {
        systemMessage.append("Your goal is to assess the user's language skills, provide questions, and evaluate answers. ");
        systemMessage.append("Focus on grammar, vocabulary, comprehension, and language proficiency. ");
        systemMessage.append("Keep instructions clear and concise. ");
    }
    
    private void appendPersonalizationInstructions() {
        // Add child-friendly instructions if applicable
        if (isChildFriendly) {
            systemMessage.append("You are assessing a child. Use simple, clear language and be extra encouraging and patient. ");
            systemMessage.append("Make the assessment feel like a fun conversation rather than a formal test. ");
        }
        
        // Add user nickname personalization
        if (userNickname != null && !userNickname.trim().isEmpty()) {
            systemMessage.append("The user's name is ").append(userNickname).append(". ");
            systemMessage.append("You can use their name naturally in the assessment to make it more personal and friendly. ");
        }
        
        // Add age-appropriate instructions
        if (ageRange != null && !ageRange.trim().isEmpty()) {
            systemMessage.append("The user is in the age range: ").append(ageRange).append(". ");
            systemMessage.append("Adjust your assessment questions and language complexity to be appropriate for this age group. ");
        }
    }
    
    private void appendFirstMessageInstructions() {
        systemMessage.append("This is the first message from the user. Since this is a first message, you should automatically generate an appropriate greeting and start the assessment conversation. Do not wait for the user to provide instructions - take the initiative to start the assessment based on the context. ");
    }

    public void appendPastDialogue(List<MessageDto> pastDialogue) {
        if (pastDialogue != null && !pastDialogue.isEmpty()) {
            systemMessage.append("Past assessment dialogue: ");
            for (MessageDto pastItem : pastDialogue) {
                String senderType = pastItem.getSenderType().equals("u") ? "user" : "system";
                systemMessage.append("\n").append(senderType).append(": ").append(pastItem.getMessage());
            }
        }
        systemMessage.append("Continue the assessment based on previous interactions. ");
    }

    public void appendUserPrompt(String prompt) {
        systemMessage.append("Assessment prompt: ").append(prompt).append(" ");
    }

    public String build(String language, String languageLevel) {
        appendAssessmentSpecificInstructions(language, languageLevel);
        return systemMessage.toString();
    }

    protected abstract void appendAssessmentSpecificInstructions(String language, String languageLevel);
} 
