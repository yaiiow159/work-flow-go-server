package com.workflowgo.workflowgoserver.repository;

import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    
    List<Document> findByType(DocumentType type);
    
    List<Document> findByInterviewId(UUID interviewId);
}
