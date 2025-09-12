package com.ai.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class SessionTestDto implements Serializable {
    private Long id;
    private String status;
    private Timestamp completedAt;
    private Integer score;
    private Boolean pass;
    private Integer questionCount;
    private List<SessionTestQuestionDto> questions;
    private Timestamp createdDateTime;
    private Timestamp updatedDateTime;
    // String representation of the speaking status, matches the status field
    private String speakingStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getPass() {
        return pass;
    }

    public void setPass(Boolean pass) {
        this.pass = pass;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public List<SessionTestQuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SessionTestQuestionDto> questions) {
        this.questions = questions;
    }

    public Timestamp getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Timestamp createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Timestamp getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(Timestamp updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public String getSpeakingStatus() {
        return speakingStatus;
    }

    public void setSpeakingStatus(String speakingStatus) {
        this.speakingStatus = speakingStatus;
    }
} 
