package com.ai.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TestResponseDto implements Serializable {
    private String status; // "question" or "finished"
    private Question question;
    private Progress progress;
    private Result result;
    private Map<String, Object> lastAnswered;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public Progress getProgress() { return progress; }
    public void setProgress(Progress progress) { this.progress = progress; }
    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }
    public Map<String, Object> getLastAnswered() { return lastAnswered; }
    public void setLastAnswered(Map<String, Object> lastAnswered) { this.lastAnswered = lastAnswered; }

    public static class Question implements Serializable {
        private Long questionId;
        private String questionText;
        private List<Option> options;
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public List<Option> getOptions() { return options; }
        public void setOptions(List<Option> options) { this.options = options; }
    }
    public static class Option implements Serializable {
        private String label;
        private String text;
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
    public static class Progress implements Serializable {
        private int current;
        private int total;
        public int getCurrent() { return current; }
        public void setCurrent(int current) { this.current = current; }
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
    }
    public static class Result implements Serializable {
        private int score;
        private int correct;
        private int wrong;
        private int total;
        private int threshold;
        private boolean passed;
        private String languageLevel;
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        public int getCorrect() { return correct; }
        public void setCorrect(int correct) { this.correct = correct; }
        public int getWrong() { return wrong; }
        public void setWrong(int wrong) { this.wrong = wrong; }
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public int getThreshold() { return threshold; }
        public void setThreshold(int threshold) { this.threshold = threshold; }
        public boolean isPassed() { return passed; }
        public void setPassed(boolean passed) { this.passed = passed; }
        public String getLanguageLevel() { return languageLevel; }
        public void setLanguageLevel(String languageLevel) { this.languageLevel = languageLevel; }
    }
} 