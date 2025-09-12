package com.exam.dto;

import java.io.Serializable;

public class CreateLevelQuestionBankRequestDto implements Serializable {
    private String courseLang;
    private Integer levelId;
    private Boolean assessment = false;

    public String getCourseLang() {
        return courseLang;
    }

    public void setCourseLang(String courseLang) {
        this.courseLang = courseLang;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public Boolean getAssessment() {
        return assessment;
    }

    public void setAssessment(Boolean assessment) {
        this.assessment = assessment;
    }
} 
