package com.ai.dto;

import java.io.Serializable;

public class CreateExamRequestDto implements Serializable {
    private Long userId;
    private String courseLang;
    private String selectedLevel;
    private String selectedTopic;
    private Long selectedLevelId;
    private Long selectedTopicId;
    private Boolean restart;
    private String userLang;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCourseLang() {
        return courseLang;
    }

    public void setCourseLang(String courseLang) {
        this.courseLang = courseLang;
    }

    public String getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(String selectedLevel) {
        this.selectedLevel = selectedLevel;
    }

    public String getSelectedTopic() {
        return selectedTopic;
    }

    public void setSelectedTopic(String selectedTopic) {
        this.selectedTopic = selectedTopic;
    }

    public Long getSelectedLevelId() {
        return selectedLevelId;
    }

    public void setSelectedLevelId(Long selectedLevelId) {
        this.selectedLevelId = selectedLevelId;
    }

    public Long getSelectedTopicId() {
        return selectedTopicId;
    }

    public void setSelectedTopicId(Long selectedTopicId) {
        this.selectedTopicId = selectedTopicId;
    }

    public Boolean getRestart() {
        return restart;
    }

    public void setRestart(Boolean restart) {
        this.restart = restart;
    }

    public String getUserLang() {
        return userLang;
    }

    public void setUserLang(String userLang) {
        this.userLang = userLang;
    }
}
