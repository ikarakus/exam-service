package com.exam.dto;

import java.io.Serializable;

public class SessionMessageDto implements Serializable {
	private Long tutorId;
	private Long userId;
	private Integer levelId;
	private Integer lessonId;
	private Integer checkpoint;  // New optional field

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getLevelId() {
		return levelId;
	}

	public void setLevelId(Integer levelId) {
		this.levelId = levelId;
	}

	public Integer getLessonId() {
		return lessonId;
	}

	public void setLessonId(Integer lessonId) {
		this.lessonId = lessonId;
	}

	public Integer getCheckpoint() {
		return checkpoint;
	}

	public void setCheckpoint(Integer checkpoint) {
		this.checkpoint = checkpoint;
	}

	public Long getTutorId() {
		return tutorId;
	}

	public void setTutorId(Long tutorId) {
		this.tutorId = tutorId;
	}
}
