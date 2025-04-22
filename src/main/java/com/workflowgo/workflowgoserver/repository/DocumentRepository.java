package com.workflowgo.workflowgoserver.repository;

import com.workflowgo.workflowgoserver.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Set<Document> findByUserId(Long userId);
    Optional<Document> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
}
