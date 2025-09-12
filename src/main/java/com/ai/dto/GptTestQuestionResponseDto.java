package com.ai.dto;

import java.io.Serializable;
import java.util.List;

public class GptTestQuestionResponseDto implements Serializable {
    private String status = "question";
    private Question question;
    private Progress progress;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public Progress getProgress() { return progress; }
    public void setProgress(Progress progress) { this.progress = progress; }

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
} 