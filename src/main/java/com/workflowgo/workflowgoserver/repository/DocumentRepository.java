package com.workflowgo.workflowgoserver.repository;

import com.workflowgo.workflowgoserver.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByInterviewId(UUID interviewId);
    
    @Query("SELECT d FROM Document d WHERE d.interview.userId = :userId")
    List<Document> findByInterviewUserId(@Param("userId") UUID userId);
}
