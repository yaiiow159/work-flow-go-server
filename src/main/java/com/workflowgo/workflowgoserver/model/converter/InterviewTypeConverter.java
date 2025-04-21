package com.workflowgo.workflowgoserver.model.converter;

import com.workflowgo.workflowgoserver.model.enums.InterviewType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class InterviewTypeConverter implements AttributeConverter<InterviewType, String> {

    @Override
    public String convertToDatabaseColumn(InterviewType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public InterviewType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return InterviewType.valueOf(dbData);
    }
}
