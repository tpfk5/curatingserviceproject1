package com.example.curatingserviceproject.enums;

public enum TimePreference {
    MORNING("오전"),
    AFTERNOON("오후"),
    EVENING("저녁/야간"),
    ANY("언제든지");

    private final String displayName;

    TimePreference(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
