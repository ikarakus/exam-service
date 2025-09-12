package com.exam.dto;


import com.exam.entities.AvatarProfile;
import com.exam.entities.User;
import com.exam.entities.UserProfile;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserRequestDto implements Serializable {
    private Long id;
    int apiKey;
    String sessionId;
    String token;
    private User user;
    private UserProfile userProfile;
    private AvatarProfile tutorProfile;
    private User tutor;
    private Integer status;
    private Integer duration;
    private Integer request_duration;
    private boolean regular;
    private Timestamp createdDateTime;
    private Timestamp updatedDateTime;
    private boolean app;

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

    public int getApiKey() {
        return apiKey;
    }

    public void setApiKey(int apiKey) {
        this.apiKey = apiKey;
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

    public Integer getRequest_duration() {
        return request_duration;
    }

    public void setRequest_duration(Integer request_duration) {
        this.request_duration = request_duration;
    }

    public boolean isRegular() {
        return regular;
    }

    public void setRegular(boolean regular) {
        this.regular = regular;
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
}
