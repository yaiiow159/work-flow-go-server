package com.workflowgo.workflowgoserver.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentType {
    RESUME,
    COVER_LETTER,
    OFFER_LETTER,
    CONTRACT,
    OTHER;

    @JsonCreator
    public static DocumentType fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (DocumentType type : DocumentType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid DocumentType: " + value);
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
