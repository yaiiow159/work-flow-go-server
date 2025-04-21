package com.workflowgo.workflowgoserver.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentType {
    RESUME("resume"),
    COVER_LETTER("cover_letter"),
    PORTFOLIO("portfolio"),
    OTHER("other");

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static DocumentType fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (DocumentType type : DocumentType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid DocumentType: " + value);
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
