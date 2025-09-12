package com.ai.dto;



import java.io.Serializable;

public class UserSessionDto implements Serializable {
	private Long userId;
	private Long tutorId;
	private Long requestId;
	private Integer request_duration;
	private boolean app;
	private String selectedLevel;
	private String selectedTopic;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTutorId() {
		return tutorId;
	}

	public void setTutorId(Long tutorId) {
		this.tutorId = tutorId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public String getSelectedLevel() {
		return selectedLevel;
	}

	public void setSelectedLevel(String selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	public String getSelectedTopic() {
		return selectedTopic;
	}

	public void setSelectedTopic(String selectedTopic) {
		this.selectedTopic = selectedTopic;
	}

	public Integer getRequest_duration() {
		if (request_duration==null) {
			request_duration = 0;
		}
		return request_duration;
	}

	public void setRequest_duration(Integer request_duration) {
		this.request_duration = request_duration;
	}

	public boolean isApp() {
		return app;
	}

	public void setApp(boolean app) {
		this.app = app;
	}
}
