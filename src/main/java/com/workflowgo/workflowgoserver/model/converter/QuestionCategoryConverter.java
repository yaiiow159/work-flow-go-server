package com.workflowgo.workflowgoserver.model.converter;

import com.workflowgo.workflowgoserver.model.enums.QuestionCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class QuestionCategoryConverter implements AttributeConverter<QuestionCategory, String> {

    @Override
    public String convertToDatabaseColumn(QuestionCategory attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name(); // Store uppercase enum name to match database constraints
    }

    @Override
    public QuestionCategory convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return QuestionCategory.valueOf(dbData); // Use valueOf for uppercase values
    }
}
