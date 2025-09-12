package com.exam.enums;

public enum RequestStatus {
    REQUESTED(0),
    ACCEPTED(1),
    CANCELED(2);
    private final int value;

    RequestStatus(int i) {
        this.value = i;
    }
    public int getValue() {
        return value;
    }
}


