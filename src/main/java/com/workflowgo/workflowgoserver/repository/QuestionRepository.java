package com.workflowgo.workflowgoserver.repository;

import com.workflowgo.workflowgoserver.model.Question;
import com.workflowgo.workflowgoserver.model.enums.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    
    List<Question> findByCategory(QuestionCategory category);
    
    List<Question> findByInterviewId(UUID interviewId);
    
    List<Question> findByIsImportantTrue();
}
