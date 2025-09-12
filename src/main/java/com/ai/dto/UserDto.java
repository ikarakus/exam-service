package com.ai.dto;

import java.sql.Timestamp;

public class UserDto {
	private Long id;
	private String  username;
	private String  email;
	private String phoneNumber;
	private int status;
	private int userType;
	private String sourceIp;
	private boolean active;
	private String referralCode;
	private String myReferralCode;
	private boolean referralUsed;
	private boolean referralLoadMinutes;
	private Timestamp createdDateTime;
	private Timestamp updatedDateTime;
	private String deviceType;
	private String fullName;
	private String nickname;

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public String getMyReferralCode() {
		return myReferralCode;
	}

	public void setMyReferralCode(String myReferralCode) {
		this.myReferralCode = myReferralCode;
	}

	public boolean isReferralUsed() {
		return referralUsed;
	}

	public void setReferralUsed(boolean referralUsed) {
		this.referralUsed = referralUsed;
	}

	public boolean isReferralLoadMinutes() {
		return referralLoadMinutes;
	}

	public void setReferralLoadMinutes(boolean referralLoadMinutes) {
		this.referralLoadMinutes = referralLoadMinutes;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
