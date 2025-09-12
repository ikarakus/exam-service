// SpanishPromptBuilder.java
package com.ai.prompts;

public class SpanishPromptBuilder extends BasePromptBuilder {

    public SpanishPromptBuilder(String model, String topic, String tutor) {
        super(model, topic, tutor);
    }

    @Override
    protected void appendLanguageSpecificInstructions(String language, String languageLevel) {
        // Similar logic, but for Spanish
        systemMessage.append(" Reply in Spanish.");
        systemMessage.append(" Even if the user writes in Turkish, respond in Spanish.");
        systemMessage.append(" If user didn't understand previous response (in Turkish), explain again in Spanish.");
        systemMessage.append(" If user is Turkish and not indicating misunderstanding, continue in Spanish.");
        systemMessage.append(" When correcting errors, do not use 'Do you mean...?' or similar English correction phrases. Instead, provide the correct sentence directly, then give a brief explanation of the correction. Keep explanations concise and focused on the specific error. Example: 'Yo quiero queso' (correct form: 'Yo quiero queso' - you need the verb 'quiero' to express desire).");

        if (!"any".equals(languageLevel)) {
            systemMessage.append(" User’s language proficiency is ").append(languageLevel).append(". Adjust your replies accordingly in Spanish.");
        }
    }
}
