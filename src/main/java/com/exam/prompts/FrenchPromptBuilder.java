package com.exam.prompts;

public class FrenchPromptBuilder extends BasePromptBuilder {

    public FrenchPromptBuilder(String model, String topic, String tutor) {
        super(model, topic, tutor);
    }
    
    public FrenchPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange) {
        super(model, topic, tutor, isChildFriendly, userNickname, ageRange, false);
    }
    
    public FrenchPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange, boolean isFirstMessage) {
        super(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
    }

    @Override
    protected void appendLanguageSpecificInstructions(String language, String languageLevel) {
        // Similar logic, but for French
        systemMessage.append(" Reply in French.");
        systemMessage.append(" Even if the user writes in Turkish, respond in French.");
        systemMessage.append(" If user didn't understand previous response (in Turkish), explain again in French.");
        systemMessage.append(" If user is Turkish and not indicating misunderstanding, continue in French.");
        systemMessage.append(" When correcting errors, do not use 'Do you mean...?' or similar English correction phrases. Instead, provide the correct sentence directly, then give a brief explanation of the correction. Keep explanations concise and focused on the specific error. Example: 'Je veux du fromage' (correct form: 'Je veux du fromage' - you need the verb 'veux' to express desire).");

        if (!"any".equals(languageLevel)) {
            systemMessage.append(" User's language proficiency is ").append(languageLevel).append(". Adjust your replies accordingly in French.");
        }
    }
}
