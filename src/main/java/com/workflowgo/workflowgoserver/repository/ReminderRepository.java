package com.workflowgo.workflowgoserver.repository;

import com.workflowgo.workflowgoserver.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    
    List<Reminder> findByInterviewId(UUID interviewId);

    @Query("SELECT r FROM Reminder r WHERE r.isCompleted = false AND r.time < :time")
    List<Reminder> findByIsCompletedFalseAndTimeBefore(LocalDateTime time);
    
    List<Reminder> findByInterviewUserId(UUID userId);
}
