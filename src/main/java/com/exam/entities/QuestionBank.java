package com.exam.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(name = "question_bank")
public class QuestionBank implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text")
    private String questionText;

    @Type(type = "jsonb")
    @Column(name = "options", columnDefinition = "jsonb")
    private String options;

    @Column(name = "correct_label")
    private String correctLabel;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "level_id")
    private Integer levelId;

    @Column(name = "lesson_id")
    private Integer lessonId;

    @Column(name = "course_lang")
    private String courseLang;

    @Column(name = "assessment")
    private Boolean assessment = false;

    @Column(name = "created_datetime")
    private Timestamp createdDatetime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    public String getCorrectLabel() { return correctLabel; }
    public void setCorrectLabel(String correctLabel) { this.correctLabel = correctLabel; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }
    public Integer getLessonId() { return lessonId; }
    public void setLessonId(Integer lessonId) { this.lessonId = lessonId; }
    public String getCourseLang() { return courseLang; }
    public void setCourseLang(String courseLang) { this.courseLang = courseLang; }
    public Boolean getAssessment() { return assessment; }
    public void setAssessment(Boolean assessment) { this.assessment = assessment; }
    public Timestamp getCreatedDatetime() { return createdDatetime; }
    public void setCreatedDatetime(Timestamp createdDatetime) { this.createdDatetime = createdDatetime; }
} 
