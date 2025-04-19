package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.DocumentDTO;
import com.workflowgo.workflowgoserver.model.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    
    List<DocumentDTO> getAllDocuments();
    
    DocumentDTO getDocumentById(UUID id);
    
    List<DocumentDTO> getDocumentsByInterviewId(UUID interviewId);
    
    List<DocumentDTO> getDocumentsByUserId(UUID userId);
    
    DocumentDTO uploadDocument(MultipartFile file, String name, DocumentType type, UUID interviewId, UUID userId);
    
    void deleteDocument(UUID id);
}
