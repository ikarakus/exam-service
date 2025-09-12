package com.exam.dto;

import java.io.Serializable;

public class GptTestFinishedResponseDto implements Serializable {
    private String status = "finished";
    private Result result;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }

    public static class Result implements Serializable {
        private int score;
        private int correct;
        private int wrong;
        private int total;
        private int threshold;
        private boolean passed;
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
    }
} 
