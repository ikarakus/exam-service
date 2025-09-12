package com.ai.enums;

public enum UserType {
    USER(1),
    TUTOR(2);

    private final int value;

    UserType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
