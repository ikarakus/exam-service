package com.ai.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(name = "session_test_question")
public class SessionTestQuestion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_test_id")
    private SessionTest sessionTest;

    @Column(name = "question_text")
    private String questionText;

    @Type(type = "jsonb")
    @Column(name = "options", columnDefinition = "jsonb")
    private String options;

    @Column(name = "correct_label")
    private String correctLabel;

    @Column(name = "selected_label")
    private String selectedLabel;

    @Column(name = "answered_at")
    private Timestamp answeredAt;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "question_index")
    private Integer questionIndex;

    @Column(name = "explanation")
    private String explanation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SessionTest getSessionTest() { return sessionTest; }
    public void setSessionTest(SessionTest sessionTest) { this.sessionTest = sessionTest; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    public String getCorrectLabel() { return correctLabel; }
    public void setCorrectLabel(String correctLabel) { this.correctLabel = correctLabel; }
    public String getSelectedLabel() { return selectedLabel; }
    public void setSelectedLabel(String selectedLabel) { this.selectedLabel = selectedLabel; }
    public Timestamp getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(Timestamp answeredAt) { this.answeredAt = answeredAt; }
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    public Integer getQuestionIndex() { return questionIndex; }
    public void setQuestionIndex(Integer questionIndex) { this.questionIndex = questionIndex; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    // Getters and setters omitted for brevity
} 