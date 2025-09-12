package com.exam.dto;

import java.io.Serializable;

public class GptTestRequestDto implements Serializable {
    private Long userId;
    private String language;
    private String selectedLevel;
    private String selectedTopic;
    private Long selectedLevelId;
    private Long selectedTopicId;
    private Long questionId;
    private String selectedLabel;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getSelectedLevel() { return selectedLevel; }
    public void setSelectedLevel(String selectedLevel) { this.selectedLevel = selectedLevel; }
    public String getSelectedTopic() { return selectedTopic; }
    public void setSelectedTopic(String selectedTopic) { this.selectedTopic = selectedTopic; }
    public Long getSelectedLevelId() { return selectedLevelId; }
    public void setSelectedLevelId(Long selectedLevelId) { this.selectedLevelId = selectedLevelId; }
    public Long getSelectedTopicId() { return selectedTopicId; }
    public void setSelectedTopicId(Long selectedTopicId) { this.selectedTopicId = selectedTopicId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getSelectedLabel() { return selectedLabel; }
    public void setSelectedLabel(String selectedLabel) { this.selectedLabel = selectedLabel; }
} 
