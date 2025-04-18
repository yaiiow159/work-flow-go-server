package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    
    private UUID id;
    private String name;
    private DocumentType type;
    private String url;
    private String contentType;
    private Long size;
    private LocalDateTime createdAt;
    private UUID interviewId;
    
    public static DocumentDTO fromEntity(Document document) {
        return DocumentDTO.builder()
                .id(document.getId())
                .name(document.getName())
                .type(document.getType())
                .url(document.getUrl())
                .contentType(document.getContentType())
                .size(document.getSize())
                .createdAt(document.getCreatedAt())
                .interviewId(document.getInterview() != null ? document.getInterview().getId() : null)
                .build();
    }
}
