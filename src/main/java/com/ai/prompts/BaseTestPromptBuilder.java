package com.ai.prompts;

import java.util.List;
import com.ai.dto.MessageDto;

public abstract class BaseTestPromptBuilder {
    protected StringBuilder systemMessage = new StringBuilder();
    protected String tutor;

    public BaseTestPromptBuilder(String model, String topic, String tutor) {
        this.tutor = tutor;
        // only change the topic if it is Ass
        if ("CEFR".equalsIgnoreCase(topic)) {
            topic = "General Language Assessment";
        }
        appendModelSpecificInstructions(model);
        appendTopicInstructions(topic);
        appendGeneralInstructions();
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
