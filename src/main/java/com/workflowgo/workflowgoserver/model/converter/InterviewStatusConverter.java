package com.workflowgo.workflowgoserver.model.converter;

import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class InterviewStatusConverter implements AttributeConverter<InterviewStatus, String> {

    @Override
    public String convertToDatabaseColumn(InterviewStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name(); // Store uppercase enum name to match database constraints
    }

    @Override
    public InterviewStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return InterviewStatus.valueOf(dbData); // Use valueOf for uppercase values
    }
}
