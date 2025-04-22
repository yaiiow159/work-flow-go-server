package com.workflowgo.workflowgoserver.repository;

import com.workflowgo.workflowgoserver.model.Interview;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByUserId(Long userId, Sort sort);
    Optional<Interview> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT i FROM Interview i WHERE i.date >= CURRENT_DATE AND i.user.id = :id")
    List<Interview> findUpcomingInterviewsByUserId(Long id);

    @Query("SELECT i FROM Interview i WHERE i.date < CURRENT_DATE AND i.user.id = :id")
    List<Interview> findPastInterviewsByUserId(Long id);
}
