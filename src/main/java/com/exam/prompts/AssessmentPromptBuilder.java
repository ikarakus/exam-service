package com.exam.prompts;

public class AssessmentPromptBuilder extends BaseTestPromptBuilder {
    private boolean freeConversationMode = false;
    public void setFreeConversationMode(boolean freeConversationMode) {
        this.freeConversationMode = freeConversationMode;
    }
    public AssessmentPromptBuilder(String model, String topic, String tutor) {
        super(model, topic, tutor);
    }
    
    public AssessmentPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange) {
        super(model, topic, tutor, isChildFriendly, userNickname, ageRange, false);
    }
    
    public AssessmentPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange, boolean isFirstMessage) {
        super(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
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
            // Check if it's an exam name
            if ("YDS".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" You are now in YDS (Yabancı Dil Bilgisi Seviye Tespit Sınavı) assessment mode. ");
                systemMessage.append("Focus specifically on YDS exam assessment: Turkish-English translation, grammar, vocabulary, reading comprehension, and cloze test questions. ");
                systemMessage.append("Ask YDS-style assessment questions and evaluate responses based on YDS criteria. ");
            } else if ("TOEFL".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" You are now in TOEFL (Test of English as a Foreign Language) assessment mode. ");
                systemMessage.append("Focus specifically on TOEFL exam assessment: academic English, reading, listening, speaking, writing skills, and integrated tasks. ");
                systemMessage.append("Ask TOEFL-style assessment questions and evaluate responses based on TOEFL criteria. ");
            } else if ("IELTS".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" You are now in IELTS (International English Language Testing System) assessment mode. ");
                systemMessage.append("Focus specifically on IELTS exam assessment: general and academic English, all four skills (reading, writing, listening, speaking). ");
                systemMessage.append("Ask IELTS-style assessment questions and evaluate responses based on IELTS criteria. ");
            } else {
                systemMessage.append(" User's assessment level is ").append(languageLevel).append(". Adjust your assessment accordingly. ");
                
                // Add specific instructions for CEFR levels
                if ("A1".equals(languageLevel)) {
                    systemMessage.append(" Focus on basic vocabulary, simple present tense, and everyday expressions. ");
                } else if ("A2".equals(languageLevel)) {
                    systemMessage.append(" Focus on basic grammar, past and future tenses, and common situations. ");
                } else if ("B1".equals(languageLevel)) {
                    systemMessage.append(" Focus on intermediate grammar, complex sentences, and abstract topics. ");
                } else if ("B2".equals(languageLevel)) {
                    systemMessage.append(" Focus on advanced grammar, nuanced expressions, and complex ideas. ");
                } else if ("C1".equals(languageLevel) || "C2".equals(languageLevel)) {
                    systemMessage.append(" Focus on sophisticated language use, idiomatic expressions, and complex discourse. ");
                }
            }
        }
        systemMessage.append(" Provide assessment questions, evaluate answers, and give feedback. ");
        systemMessage.append(" Focus on substantial language issues and assessment criteria. ");
        systemMessage.append(" Only ask one assessment question at a time. Wait for the user's answer before asking another question. Do not ask multiple questions in a single response. ");
    }
} 
