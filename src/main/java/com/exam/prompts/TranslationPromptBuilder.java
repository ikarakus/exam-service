package com.exam.prompts;

public class TranslationPromptBuilder extends BasePromptBuilder {

    private String inputLanguage;
    private String outputLanguage;
    private String message;

    public TranslationPromptBuilder(String model, String inputLanguage, String outputLanguage, String message) {
        super(model, "translation", null); // "translation" as the topic, no tutor needed
        this.inputLanguage = inputLanguage;
        this.outputLanguage = outputLanguage;
        this.message = message;
    }

    @Override
    protected void appendLanguageSpecificInstructions(String language, String languageLevel) {
        // Add translation-specific instructions
        systemMessage.append("Translate the following text from ")
                     .append(inputLanguage).append(" to ").append(outputLanguage).append(":\n");
        systemMessage.append("\"").append(message).append("\"");
        systemMessage.append("\nProvide only the translation without any additional comments or explanations.");
    }
}
