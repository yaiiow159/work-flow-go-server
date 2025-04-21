package com.workflowgo.workflowgoserver.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionCategory {
    TECHNICAL,
    BEHAVIORAL,
    COMPANY,
    ROLE,
    OTHER;

    @JsonCreator
    public static QuestionCategory fromString(String value) {
        if (value == null) {
            return null;
        }

        for (QuestionCategory category : QuestionCategory.values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid QuestionCategory: " + value);
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
