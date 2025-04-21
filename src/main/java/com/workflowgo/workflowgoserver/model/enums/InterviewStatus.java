package com.workflowgo.workflowgoserver.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InterviewStatus {
    SCHEDULED,
    CONFIRMED,
    COMPLETED,
    REJECTED,
    CANCELLED;

    @JsonCreator
    public static InterviewStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (InterviewStatus status : InterviewStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid InterviewStatus: " + value);
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
