package com.ai.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import com.ai.enums.SessionTestStatus;
import com.ai.entities.SessionTestQuestion;

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(name = "session_test")
public class SessionTest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level_id")
    private Integer levelId;

    @Column(name = "lesson_id")
    private Integer lessonId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SessionTestStatus status;

    @Column(name = "completed_at")
    private Timestamp completedAt;

    @Column(name = "score")
    private Integer score;

    @Column(name = "pass")
    private Boolean pass;

    @Column(name = "question_count")
    private Integer questionCount;

    @OneToMany(mappedBy = "sessionTest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionTestQuestion> questions;

    @Column(name = "created_datetime")
    private Timestamp createdDateTime;

    @Column(name = "updated_datetime")
    private Timestamp updatedDateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "course_lang")
    private String courseLang;

    @Column(name = "speaking_score")
    private Integer speakingScore;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "user_level")
    private String userLevel;

    @Column(name = "speaking_insufficient_data")
    private Boolean speakingInsufficientData;

    @Column(name = "assessment")
    private Boolean assessment = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "speaking_status")
    private SessionTestStatus speakingStatus;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }
    public Integer getLessonId() { return lessonId; }
    public void setLessonId(Integer lessonId) { this.lessonId = lessonId; }
    public SessionTestStatus getStatus() { return status; }
    public void setStatus(SessionTestStatus status) { this.status = status; }
    public Timestamp getCompletedAt() { return completedAt; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Boolean getPass() { return pass; }
    public void setPass(Boolean pass) { this.pass = pass; }
    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
    public List<SessionTestQuestion> getQuestions() { return questions; }
    public void setQuestions(List<SessionTestQuestion> questions) { this.questions = questions; }
    public Timestamp getCreatedDateTime() { return createdDateTime; }
    public void setCreatedDateTime(Timestamp createdDateTime) { this.createdDateTime = createdDateTime; }
    public Timestamp getUpdatedDateTime() { return updatedDateTime; }
    public void setUpdatedDateTime(Timestamp updatedDateTime) { this.updatedDateTime = updatedDateTime; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCourseLang() {
        return courseLang;
    }

    public void setCourseLang(String courseLang) {
        this.courseLang = courseLang;
    }

    public Integer getSpeakingScore() { return speakingScore; }
    public void setSpeakingScore(Integer speakingScore) { this.speakingScore = speakingScore; }
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
    public String getUserLevel() { return userLevel; }
    public void setUserLevel(String userLevel) { this.userLevel = userLevel; }
    public Boolean getSpeakingInsufficientData() { return speakingInsufficientData; }
    public void setSpeakingInsufficientData(Boolean speakingInsufficientData) { this.speakingInsufficientData = speakingInsufficientData; }

    public Boolean getAssessment() {
        return assessment;
    }

    public void setAssessment(Boolean assessment) {
        this.assessment = assessment;
    }

    public SessionTestStatus getSpeakingStatus() { return speakingStatus; }
    public void setSpeakingStatus(SessionTestStatus speakingStatus) { this.speakingStatus = speakingStatus; }
}
