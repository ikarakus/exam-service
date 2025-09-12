package com.exam.dto;

import java.io.Serializable;

public class SimpleMessageDto implements Serializable {
    private String sender;
    private String message;

    // Getters and setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
