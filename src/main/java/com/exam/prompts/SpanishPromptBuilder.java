// SpanishPromptBuilder.java
package com.exam.prompts;

public class SpanishPromptBuilder extends BasePromptBuilder {

    public SpanishPromptBuilder(String model, String topic, String tutor) {
        super(model, topic, tutor);
    }
    
    public SpanishPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange) {
        super(model, topic, tutor, isChildFriendly, userNickname, ageRange, false);
    }
    
    public SpanishPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange, boolean isFirstMessage) {
        super(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
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
            // Check if it's an exam name
            if ("YDS".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" You are now in YDS exam mode. Focus on YDS exam content in Spanish context. ");
            } else if ("TOEFL".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" You are now in TOEFL exam mode. Focus on TOEFL exam content in Spanish context. ");
            } else if ("IELTS".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" You are now in IELTS exam mode. Focus on IELTS exam content in Spanish context. ");
            } else {
                systemMessage.append(" User's language proficiency is ").append(languageLevel).append(". Adjust your replies accordingly in Spanish.");
            }
        }
        
        // Add first message specific instructions for Spanish
        if (isFirstMessage) {
            systemMessage.append(" Since this is the first message, automatically generate an appropriate greeting and start the conversation. ");
            
            if ("any".equals(languageLevel)) {
                systemMessage.append(" Start with 'Let's start evaluating your level' and then proceed with a random question to assess their Spanish level. ");
            } else if ("CEFR".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" Greet the user warmly and start a conversation to assess their CEFR level. ");
            } else if ("YDS".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" Greet the user and start with YDS exam preparation in Spanish context. ");
            } else if ("TOEFL".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" Greet the user and start with TOEFL exam preparation in Spanish context. ");
            } else if ("IELTS".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" Greet the user and start with IELTS exam preparation in Spanish context. ");
            } else {
                // Specific topic case
                if (userNickname != null && !userNickname.trim().isEmpty()) {
                    systemMessage.append(" Greet the user with their nickname '").append(userNickname).append("' first, then focus on the topic '").append(topic).append("' and find subtopics to discuss. ");
                } else {
                    systemMessage.append(" Greet the user first, then focus on the topic '").append(topic).append("' and find subtopics to discuss. ");
                }
            }
        }
    }
}
