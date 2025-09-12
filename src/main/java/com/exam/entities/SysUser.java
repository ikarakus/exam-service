package com.exam.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "sys_user")
public class SysUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "user_type")
    private Integer userType;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "active")
    private boolean active;

    @Column(name = "profile_email_sent1")
    private boolean profileEmailSent1;

    @Column(name = "profile_email_sent2")
    private boolean profileEmailSent2;

    @Column(name = "created_datetime")
    private Timestamp createdDateTime;

    @Column(name = "updated_datetime")
    private Timestamp updatedDateTime;

    @Column(name = "referral_used")
    private boolean referralUsed;

    @Column(name = "referral_email_sent")
    private boolean referralEmailSent;

    @Column(name = "referral_load_minutes")
    private boolean referralLoadMinutes;

    // Getters and Setters for all fields

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isProfileEmailSent1() {
        return profileEmailSent1;
    }

    public void setProfileEmailSent1(boolean profileEmailSent1) {
        this.profileEmailSent1 = profileEmailSent1;
    }

    public boolean isProfileEmailSent2() {
        return profileEmailSent2;
    }

    public void setProfileEmailSent2(boolean profileEmailSent2) {
        this.profileEmailSent2 = profileEmailSent2;
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

    public boolean isReferralUsed() {
        return referralUsed;
    }

    public void setReferralUsed(boolean referralUsed) {
        this.referralUsed = referralUsed;
    }

    public boolean isReferralEmailSent() {
        return referralEmailSent;
    }

    public void setReferralEmailSent(boolean referralEmailSent) {
        this.referralEmailSent = referralEmailSent;
    }

    public boolean isReferralLoadMinutes() {
        return referralLoadMinutes;
    }

    public void setReferralLoadMinutes(boolean referralLoadMinutes) {
        this.referralLoadMinutes = referralLoadMinutes;
    }
}
