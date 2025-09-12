package com.exam.serviceImpl;

import java.util.HashSet;
import java.util.Set;

public class ComplexityAssessment {

    public static double assessComplexity(String text) {
        double sentenceLengthScore = assessSentenceLength(text);
        double vocabularyRichnessScore = assessVocabularyRichness(text);
        double syntacticComplexityScore = assessSyntacticComplexity(text);
        
        // Combine the scores in a weighted manner
        return (sentenceLengthScore + vocabularyRichnessScore + syntacticComplexityScore) / 3.0;
    }

    private static double assessSentenceLength(String text) {
        String[] sentences = text.split("[.!?]");
        double averageLength = (double) text.split("\\s+").length / sentences.length;
        return averageLength / 20.0; // Normalizing by a typical average sentence length
    }

    private static double assessVocabularyRichness(String text) {
        String[] tokens = text.split("\\s+");
        Set<String> uniqueTokens = new HashSet<>();
        for (String token : tokens) {
            uniqueTokens.add(token.toLowerCase());
        }
        return (double) uniqueTokens.size() / tokens.length;
    }

    private static double assessSyntacticComplexity(String text) {
        // Basic syntactic complexity assessment based on punctuation and conjunctions
        int punctuations = text.split("[,;]").length - 1;
        int conjunctions = text.split("\\b(and|or|but|so|because)\\b").length - 1;
        return (double) (punctuations + conjunctions) / (text.split("\\s+").length);
    }
}
