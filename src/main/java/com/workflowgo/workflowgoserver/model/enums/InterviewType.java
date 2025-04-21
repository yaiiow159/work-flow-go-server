package com.workflowgo.workflowgoserver.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InterviewType {
    REMOTE,
    ONSITE,
    PHONE;

    @JsonCreator
    public static InterviewType fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (InterviewType type : InterviewType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid InterviewType: " + value);
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
