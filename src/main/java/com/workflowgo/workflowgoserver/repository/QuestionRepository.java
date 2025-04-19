package com.workflowgo.workflowgoserver.repository;

import com.workflowgo.workflowgoserver.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    @Query("SELECT q FROM Question q WHERE q.interview.id = :interviewId")
    List<Question> findByInterviewId(@Param("interviewId") UUID interviewId);

    @Query("SELECT q FROM Question q WHERE q.isImportant = true")
    List<Question> findByIsImportantTrue();
    
    @Query("SELECT q FROM Question q WHERE q.interview.userId = :userId")
    List<Question> findByInterviewUserId(@Param("userId") UUID userId);
}
