package com.exam.prompts;

public class TurkishPromptBuilder extends BasePromptBuilder {

    public TurkishPromptBuilder(String model, String topic, String tutor) {
        super(model, topic, tutor);
    }

    @Override
    protected void appendLanguageSpecificInstructions(String language, String languageLevel) {
        // Similar logic, but for Turkish
        systemMessage.append(" Reply in Turkish.");
        systemMessage.append(" If user didn't understand previous response (in Turkish), explain again in Turkish.");
        systemMessage.append(" If user is Turkish and not indicating misunderstanding, continue in Turkish.");
        systemMessage.append(" When correcting errors, do not use 'Do you mean...?' or similar English correction phrases. Instead, provide the correct sentence directly, then give a brief explanation of the correction. Keep explanations concise and focused on the specific error. Example: 'Peynir istiyorum' (correct form: 'Peynir istiyorum' - you need the verb 'istiyorum' to express desire).");

        if (!"any".equals(languageLevel)) {
            systemMessage.append(" User's language proficiency is ").append(languageLevel).append(". Adjust your replies accordingly in Turkish.");
        }
    }
}
