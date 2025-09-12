/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ai.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "user_profile")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "nick_name")
    private String nickName;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "user_lang")
    private String userLang;
    @Column(name = "message_sound")
    private boolean messageSound;
    @Column(name = "record_session")
    private boolean recordSession;
    @Column(name = "record_session_datetime")
    private Timestamp recordSessionDateTime;
    @Column(name = "location_id")
    private Long locationId;
    @Column(name = "time_zone")
    private String timeZone;
    @Column(name = "assessment")
    private boolean assessment;
    @Column(name = "assessment_interval")
    private Integer assessmentInterval;
    @Column(name = "subtitle")
    private boolean subtitle;
    @Column(name = "created_datetime")
    private Timestamp createdDateTime;
    @Column(name = "updated_datetime")
    private Timestamp updatedDateTime;
    @Column(name = "age_group")
    private Integer ageGroup;
    @Column(name = "course_lang")
    private String courseLang;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserLang() {
        return userLang;
    }

    public void setUserLang(String userLang) {
        this.userLang = userLang;
    }

    public boolean isMessageSound() {
        return messageSound;
    }

    public void setMessageSound(boolean messageSound) {
        this.messageSound = messageSound;
    }

    public boolean isRecordSession() {
        return recordSession;
    }

    public void setRecordSession(boolean recordSession) {
        this.recordSession = recordSession;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isAssessment() {
        return assessment;
    }

    public void setAssessment(boolean assessment) {
        this.assessment = assessment;
    }

    public Integer getAssessmentInterval() {
        return assessmentInterval;
    }

    public void setAssessmentInterval(Integer assessmentInterval) {
        this.assessmentInterval = assessmentInterval;
    }

    public boolean isSubtitle() {
        return subtitle;
    }

    public void setSubtitle(boolean subtitle) {
        this.subtitle = subtitle;
    }

    public Timestamp getRecordSessionDateTime() {
        return recordSessionDateTime;
    }

    public void setRecordSessionDateTime(Timestamp recordSessionDateTime) {
        this.recordSessionDateTime = recordSessionDateTime;
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

    public Integer getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(Integer ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getCourseLang() {
        return courseLang;
    }

    public void setCourseLang(String courseLang) {
        this.courseLang = courseLang;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserProfile)) {
            return false;
        }
        UserProfile other = (UserProfile) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "UserProfile[ id=" + id + " ]";
    }
}
