/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exam.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "user_requests")
public class UserRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName="user_id",updatable=false, insertable=false)
    private UserProfile userProfile;
    @ManyToOne
    @JoinColumn(name = "tutor_id",referencedColumnName="tutor_id",updatable=false, insertable=false)
    private AvatarProfile tutorProfile;
    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private User tutor;
    @Column(name = "session_id")
    private String sessionId;
    @Column(name = "token")
    private String token;
    private Integer status;
    private Integer duration;
    private Integer package_duration;
    private Integer anytime_duration;
    private Integer request_duration;
    @Column(name = "current_rate")
    private Double currentRate;
    @Column(name = "stream_created_datetime")
    private Timestamp streamCreatedDateTime;
    @Column(name = "stream_destroyed_datetime")
    private Timestamp streamDestroyedDateTime;
    @Column(name = "reservation_id")
    private Long reservationId;
    @Column(name = "created_datetime")
    private Timestamp createdDateTime;
    @Column(name = "updated_datetime")
    private Timestamp updatedDateTime;
    @Column(name = "app")
    private boolean app;
    @Column(name = "course_lang")
    private String courseLang;
    @Column(name = "selected_level")
    private String selectedLevel;
    @Column(name = "selected_lesson")
    private String selectedLesson;


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

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public AvatarProfile getTutorProfile() {
        return tutorProfile;
    }

    public void setTutorProfile(AvatarProfile tutorProfile) {
        this.tutorProfile = tutorProfile;
    }

    public User getTutor() {
        return tutor;
    }

    public void setTutor(User tutor) {
        this.tutor = tutor;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getPackage_duration() {
        return package_duration;
    }

    public void setPackage_duration(Integer package_duration) {
        this.package_duration = package_duration;
    }

    public Integer getAnytime_duration() {
        return anytime_duration;
    }

    public void setAnytime_duration(Integer anytime_duration) {
        this.anytime_duration = anytime_duration;
    }

    public Integer getRequest_duration() {
        return request_duration;
    }

    public void setRequest_duration(Integer request_duration) {
        this.request_duration = request_duration;
    }

    public Double getCurrentRate() {
        return currentRate;
    }

    public void setCurrentRate(Double currentRate) {
        this.currentRate = currentRate;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Timestamp getStreamCreatedDateTime() {
        return streamCreatedDateTime;
    }

    public void setStreamCreatedDateTime(Timestamp streamCreatedDateTime) {
        this.streamCreatedDateTime = streamCreatedDateTime;
    }

    public Timestamp getStreamDestroyedDateTime() {
        return streamDestroyedDateTime;
    }

    public void setStreamDestroyedDateTime(Timestamp streamDestroyedDateTime) {
        this.streamDestroyedDateTime = streamDestroyedDateTime;
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

    public boolean isApp() {
        return app;
    }

    public void setApp(boolean app) {
        this.app = app;
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

    public String getSelectedLesson() {
        return selectedLesson;
    }

    public void setSelectedLesson(String selectedLesson) {
        this.selectedLesson = selectedLesson;
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
        if (!(object instanceof UserRequest)) {
            return false;
        }
        UserRequest other = (UserRequest) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "UserRequest[ id=" + id + " ]";
    }
}
