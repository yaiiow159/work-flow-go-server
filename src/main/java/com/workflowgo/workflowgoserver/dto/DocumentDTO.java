package com.workflowgo.workflowgoserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.enums.DocumentType;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentDTO {
    private Long id;
    private String name;
    private String type;
    private String url;
    private String contentType;
    private Long size;
    private ZonedDateTime createdAt;
    
    public static DocumentDTO fromDocument(Document document) {
        if (document == null) {
            return null;
        }
        
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setName(document.getName());

        if (document.getType() != null) {
            dto.setType(document.getType().getValue());
        }

        dto.setUrl(document.getUrl());
        dto.setContentType(document.getContentType());
        dto.setSize(document.getSize());
        dto.setCreatedAt(document.getCreatedAt());
        
        return dto;
    }
    
    public static List<DocumentDTO> fromDocuments(List<Document> documents) {
        return documents.stream()
                .map(DocumentDTO::fromDocument)
                .collect(Collectors.toList());
    }
}
