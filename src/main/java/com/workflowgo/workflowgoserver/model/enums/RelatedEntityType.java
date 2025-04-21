package com.workflowgo.workflowgoserver.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RelatedEntityType {
    INTERVIEW,
    DOCUMENT,
    SYSTEM;

    @JsonCreator
    public static RelatedEntityType fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (RelatedEntityType type : RelatedEntityType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid RelatedEntityType: " + value);
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
