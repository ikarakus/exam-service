package com.exam.dto;


import java.io.Serializable;


public class AvatarProfileDto implements Serializable {
    private Long tutorId;
    private String fullName;
    private String introduction;
    private String videoPath;
    private String photoPath;
    private String userLang;
    private String gender;
    private String race;
    private String skinColor;
    private String eyeColor;
    private String hairColor;
    private String hairStyle;
    private String emotion;
    private boolean kids;
    private Integer orderNo;  // Add this field
    private String voiceName;
    private String voiceSource;

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getUserLang() {
        return userLang;
    }

    public void setUserLang(String userLang) {
        this.userLang = userLang;
    }

    public boolean isKids() {
        return kids;
    }

    public void setKids(boolean kids) {
        this.kids = kids;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public String getHairStyle() {
        return hairStyle;
    }

    public void setHairStyle(String hairStyle) {
        this.hairStyle = hairStyle;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public String getVoiceSource() {
        return voiceSource;
    }

    public void setVoiceSource(String voiceSource) {
        this.voiceSource = voiceSource;
    }
    
}
