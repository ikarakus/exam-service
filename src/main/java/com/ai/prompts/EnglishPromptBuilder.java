package com.ai.prompts;

public class EnglishPromptBuilder extends BasePromptBuilder {

    public EnglishPromptBuilder(String model, String topic, String tutor) {
        super(model, topic, tutor);
    }

    @Override
    protected void appendLanguageSpecificInstructions(String language, String languageLevel) {
        if (!"any".equals(language) && !language.isEmpty()) {
            systemMessage.append(" Reply in English.");
            systemMessage.append(" Even if the user writes in Turkish, respond in English.");
            systemMessage.append(" If user didn't understand previous response (in Turkish), explain again in English.");
            systemMessage.append(" If user is Turkish and not indicating misunderstanding, continue in English.");
        } else {
            systemMessage.append(" Default to replying in English.");
        }

        // English-specific correction instructions
        systemMessage.append(" If the user's sentence has a clear grammatical error (e.g., verb tense issues), use 'Do you mean...?' to provide the correction explicitly, and avoid adding suggestions in the same response. ");
        systemMessage.append(" If the user's sentence appears incomplete or missing key words, suggest the missing words by rephrasing with: 'Did you mean to say: I am travelling by plane?' or 'Perhaps you meant: I am travelling by plane.' ");
        systemMessage.append(" For unclear sentences, use friendly phrases like 'Do you mean...?' to clarify intent, but do not include additional suggestions in the same response. ");

        if (!"any".equals(languageLevel)) {
            systemMessage.append(" User's language proficiency is ").append(languageLevel).append(". Adjust your replies accordingly.");
        }
    }
}
