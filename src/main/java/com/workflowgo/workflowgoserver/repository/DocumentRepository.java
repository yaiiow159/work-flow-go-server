package com.workflowgo.workflowgoserver.repository;

import com.workflowgo.workflowgoserver.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserId(Long userId);
    Optional<Document> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
}
