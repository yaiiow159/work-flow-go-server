package com.workflowgo.workflowgoserver.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthProvider {
    local,
    google;

    @JsonCreator
    public static AuthProvider fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (AuthProvider provider : AuthProvider.values()) {
            if (provider.name().equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Invalid AuthProvider: " + value);
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
