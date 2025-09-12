package com.ai.dto;

import java.io.Serializable;

public class TestRequestDto implements Serializable {
    private Long userId;
    private String courseLang;
    private String selectedLevel;
    private String selectedTopic;
    private Integer selectedLevelId;
    private Integer selectedTopicId;
    private Long questionId;
    private String selectedLabel;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCourseLang() { return courseLang; }
    public void setCourseLang(String courseLang) { this.courseLang = courseLang; }
    public String getSelectedLevel() { return selectedLevel; }
    public void setSelectedLevel(String selectedLevel) { this.selectedLevel = selectedLevel; }
    public String getSelectedTopic() { return selectedTopic; }
    public void setSelectedTopic(String selectedTopic) { this.selectedTopic = selectedTopic; }

    public Integer getSelectedLevelId() {
        return selectedLevelId;
    }

    public void setSelectedLevelId(Integer selectedLevelId) {
        this.selectedLevelId = selectedLevelId;
    }

    public Integer getSelectedTopicId() {
        return selectedTopicId;
    }

    public void setSelectedTopicId(Integer selectedTopicId) {
        this.selectedTopicId = selectedTopicId;
    }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getSelectedLabel() { return selectedLabel; }
    public void setSelectedLabel(String selectedLabel) { this.selectedLabel = selectedLabel; }
} 
