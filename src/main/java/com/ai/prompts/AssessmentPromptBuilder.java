package com.ai.prompts;

public class AssessmentPromptBuilder extends BaseTestPromptBuilder {
    private boolean freeConversationMode = false;
    public void setFreeConversationMode(boolean freeConversationMode) {
        this.freeConversationMode = freeConversationMode;
    }
    public AssessmentPromptBuilder(String model, String topic, String tutor) {
        super(model, topic, tutor);
    }

    @Override
    protected void appendAssessmentSpecificInstructions(String language, String languageLevel) {
        if (freeConversationMode) {
            systemMessage.append(
                " Keep your responses short and conversational, like a friendly chat. " +
                "Avoid strict lesson or test language. Make the conversation feel natural and supportive, not like a quiz. " +
                "Respond in a friendly, encouraging way. " +
                "During the conversation, ask friendly and engaging questions that help you understand the user's language proficiency, " +
                "but do not make it feel like an assessment. Focus on topics that encourage the user to express themselves, " +
                "and use their responses to gently gauge their level. " +
                "Do not focus on correction or explicit assessment unless the user asks."
            );
            return;
        }
        if (language != null && !"any".equalsIgnoreCase(language) && !language.isEmpty()) {
            systemMessage.append(" Reply in ").append(language).append(". ");
            systemMessage.append(" Even if the user writes in Turkish, respond in ").append(language).append(". ");
            systemMessage.append(" If user didn't understand previous response (in Turkish), explain again in ").append(language).append(". ");
            systemMessage.append(" If user is Turkish and not indicating misunderstanding, continue in ").append(language).append(". ");
        } else {
            systemMessage.append(" Default to replying in English. ");
        }
        if (languageLevel != null && !"any".equalsIgnoreCase(languageLevel) && !languageLevel.isEmpty()) {
            systemMessage.append(" User's assessment level is ").append(languageLevel).append(". Adjust your assessment accordingly. ");
        }
        systemMessage.append(" Provide assessment questions, evaluate answers, and give feedback. ");
        systemMessage.append(" Focus on substantial language issues and assessment criteria. ");
        systemMessage.append(" Only ask one assessment question at a time. Wait for the user's answer before asking another question. Do not ask multiple questions in a single response. ");
    }
} 