package com.exam.prompts;

public class GermanPromptBuilder extends BasePromptBuilder {

    public GermanPromptBuilder(String model, String topic, String tutor) {
        super(model, topic, tutor);
    }
    
    public GermanPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange) {
        super(model, topic, tutor, isChildFriendly, userNickname, ageRange, false);
    }
    
    public GermanPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange, boolean isFirstMessage) {
        super(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
    }

    @Override
    protected void appendLanguageSpecificInstructions(String language, String languageLevel) {
        // Similar logic, but for German
        systemMessage.append(" Reply in German.");
        systemMessage.append(" Even if the user writes in Turkish, respond in German.");
        systemMessage.append(" If user didn't understand previous response (in Turkish), explain again in German.");
        systemMessage.append(" If user is Turkish and not indicating misunderstanding, continue in German.");
        systemMessage.append(" When correcting errors, do not use 'Do you mean...?' or similar English correction phrases. Instead, provide the correct sentence directly, then give a brief explanation of the correction. Keep explanations concise and focused on the specific error. Example: 'Ich möchte Käse' (correct form: 'Ich möchte Käse' - you need the verb 'möchte' to express desire).");

        if (!"any".equals(languageLevel)) {
            systemMessage.append(" User's language proficiency is ").append(languageLevel).append(". Adjust your replies accordingly in German.");
        }
    }
}
