package com.workflowgo.workflowgoserver.model.converter;

import com.workflowgo.workflowgoserver.model.enums.DocumentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DocumentTypeConverter implements AttributeConverter<DocumentType, String> {

    @Override
    public String convertToDatabaseColumn(DocumentType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public DocumentType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return DocumentType.valueOf(dbData);
    }
}
