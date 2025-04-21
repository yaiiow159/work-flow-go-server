package com.workflowgo.workflowgoserver.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InterviewStatus {
    SCHEDULED("scheduled"),
    CONFIRMED("confirmed"),
    COMPLETED("completed"),
    REJECTED("rejected"),
    CANCELLED("cancelled");

    private final String value;

    InterviewStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static InterviewStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (InterviewStatus status : InterviewStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid InterviewStatus: " + value);
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
