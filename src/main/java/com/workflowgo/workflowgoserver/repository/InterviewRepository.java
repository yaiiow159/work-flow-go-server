package com.workflowgo.workflowgoserver.repository;

import com.workflowgo.workflowgoserver.model.Interview;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByUserId(Long userId);
    List<Interview> findByUserId(Long userId, Sort sort);
    Optional<Interview> findByIdAndUserId(Long id, Long userId);
}
