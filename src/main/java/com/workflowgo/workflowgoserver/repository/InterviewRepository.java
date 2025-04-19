package com.workflowgo.workflowgoserver.repository;

import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, UUID> {
    
    List<Interview> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Interview> findByUserId(UUID userId);
    
    List<Interview> findByDateBetweenAndUserId(LocalDate startDate, LocalDate endDate, UUID userId);
    
    @Query("SELECT i FROM Interview i WHERE " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:fromDate IS NULL OR i.date >= :fromDate) AND " +
           "(:toDate IS NULL OR i.date <= :toDate) AND " +
           "(:company IS NULL OR LOWER(i.companyName) LIKE LOWER(CONCAT('%', :company, '%')))")
    List<Interview> findByFilters(
            @Param("status") InterviewStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("company") String company);
    
    @Query("SELECT i FROM Interview i WHERE " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:fromDate IS NULL OR i.date >= :fromDate) AND " +
           "(:toDate IS NULL OR i.date <= :toDate) AND " +
           "(:company IS NULL OR LOWER(i.companyName) LIKE LOWER(CONCAT('%', :company, '%'))) AND " +
           "i.userId = :userId")
    List<Interview> findByFiltersAndUserId(
            @Param("status") InterviewStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("company") String company,
            @Param("userId") UUID userId);
}
