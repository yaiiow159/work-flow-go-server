package com.workflowgo.workflowgoserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.workflowgo.workflowgoserver.model.Document;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Set;
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
    
    public static Set<DocumentDTO> fromDocuments(Set<Document> documents) {
        return documents.stream()
                .map(DocumentDTO::fromDocument)
                .collect(Collectors.toSet());
    }
}
