package com.exam.prompts;

public class EnglishPromptBuilder extends BasePromptBuilder {

    public EnglishPromptBuilder(String model, String topic, String tutor) {
        super(model, topic, tutor);
    }
    
    public EnglishPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange) {
        super(model, topic, tutor, isChildFriendly, userNickname, ageRange, false);
    }
    
    public EnglishPromptBuilder(String model, String topic, String tutor, boolean isChildFriendly, String userNickname, String ageRange, boolean isFirstMessage) {
        super(model, topic, tutor, isChildFriendly, userNickname, ageRange, isFirstMessage);
    }

    @Override
    protected void appendLanguageSpecificInstructions(String language, String languageLevel) {
        // Always respond in English for EnglishPromptBuilder
        systemMessage.append(" Reply in English.");
        systemMessage.append(" Even if the user writes in Turkish, respond in English.");
        systemMessage.append(" If user didn't understand previous response (in Turkish), explain again in English.");
        systemMessage.append(" If user is Turkish and not indicating misunderstanding, continue in English.");

        // English-specific correction instructions
        systemMessage.append(" If the user's sentence has a clear grammatical error (e.g., verb tense issues), use 'Do you mean...?' to provide the correction explicitly, and avoid adding suggestions in the same response. ");
        systemMessage.append(" If the user's sentence appears incomplete or missing key words, suggest the missing words by rephrasing with: 'Did you mean to say: I am travelling by plane?' or 'Perhaps you meant: I am travelling by plane.' ");
        systemMessage.append(" For unclear sentences, use friendly phrases like 'Do you mean...?' to clarify intent, but do not include additional suggestions in the same response. ");

        // Handle CEFR levels, exam names, and other proficiency levels
        if (!"any".equals(languageLevel) && !languageLevel.isEmpty()) {
            // Check if it's an exam name
            if ("YDS".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" You are now in YDS (Yabancı Dil Bilgisi Seviye Tespit Sınavı) exam mode. ");
                systemMessage.append("Focus specifically on YDS exam content: Turkish-English translation, grammar, vocabulary, reading comprehension, and cloze test questions. ");
                systemMessage.append("Limit your responses to YDS-relevant topics and question types. ");
                systemMessage.append("Ask questions that are typical for YDS exam preparation. ");
            } else if ("TOEFL".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" You are now in TOEFL (Test of English as a Foreign Language) exam mode. ");
                systemMessage.append("Focus specifically on TOEFL exam content: academic English, reading, listening, speaking, writing skills, and integrated tasks. ");
                systemMessage.append("Limit your responses to TOEFL-relevant topics and question types. ");
                systemMessage.append("Ask questions that are typical for TOEFL exam preparation. ");
            } else if ("IELTS".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" You are now in IELTS (International English Language Testing System) exam mode. ");
                systemMessage.append("Focus specifically on IELTS exam content: general and academic English, all four skills (reading, writing, listening, speaking), Task 1 and Task 2 writing. ");
                systemMessage.append("Limit your responses to IELTS-relevant topics and question types. ");
                systemMessage.append("Ask questions that are typical for IELTS exam preparation. ");
            } else {
                // Handle CEFR levels
                systemMessage.append(" User's language proficiency is ").append(languageLevel).append(". Adjust your replies accordingly.");
                
                // Add specific instructions for CEFR levels
                if ("A1".equals(languageLevel)) {
                    systemMessage.append(" Use very simple vocabulary and short sentences. Focus on basic communication.");
                } else if ("A2".equals(languageLevel)) {
                    systemMessage.append(" Use simple vocabulary and clear sentences. Introduce some common expressions.");
                } else if ("B1".equals(languageLevel)) {
                    systemMessage.append(" Use intermediate vocabulary and more complex sentence structures.");
                } else if ("B2".equals(languageLevel)) {
                    systemMessage.append(" Use advanced vocabulary and complex sentence structures.");
                } else if ("C1".equals(languageLevel) || "C2".equals(languageLevel)) {
                    systemMessage.append(" Use sophisticated vocabulary and complex grammatical structures.");
                }
            }
        }
        
        // Add first message specific instructions for English
        if (isFirstMessage) {
            systemMessage.append(" CRITICAL: This is the first message from the user. You MUST automatically generate a personalized greeting and start the conversation immediately. Do not wait for the user to provide instructions. ");
            
            if ("any".equals(languageLevel)) {
                systemMessage.append(" Start with 'Let's start evaluating your level' and then proceed with a random question to assess their English level. ");
            } else if ("CEFR".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" Greet the user warmly and start a conversation to assess their CEFR level. ");
            } else if ("YDS".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" Greet the user and start with YDS exam preparation. Ask a YDS-style question about Turkish-English translation or grammar. ");
            } else if ("TOEFL".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" Greet the user and start with TOEFL exam preparation. Ask a TOEFL-style question about academic English or reading comprehension. ");
            } else if ("IELTS".equalsIgnoreCase(languageLevel)) {
                systemMessage.append(" Greet the user and start with IELTS exam preparation. Ask an IELTS-style question about general or academic English. ");
            } else {
                // Specific topic case for CEFR levels
                if (userNickname != null && !userNickname.trim().isEmpty()) {
                    systemMessage.append(" You MUST start your response with exactly: 'Hello, ").append(userNickname).append("! I'm ").append(tutor).append(". Let's talk about ").append(topic).append(".' Then ask a specific question about the topic to get the conversation started. ");
                } else {
                    systemMessage.append(" You MUST start your response with exactly: 'Hello! I'm ").append(tutor).append(". Let's talk about ").append(topic).append(".' Then ask a specific question about the topic to get the conversation started. ");
                }
            }
        }
    }
}
