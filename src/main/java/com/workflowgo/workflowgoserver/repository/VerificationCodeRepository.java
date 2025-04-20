package com.workflowgo.workflowgoserver.repository;

import com.workflowgo.workflowgoserver.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    
    @Query("SELECT v FROM VerificationCode v WHERE v.email = :email AND v.code = :code AND v.expiryDate > :now AND v.used = false")
    Optional<VerificationCode> findValidCode(@Param("email") String email, 
                                            @Param("code") String code, 
                                            @Param("now") LocalDateTime now);
    
    Optional<VerificationCode> findFirstByEmailOrderByExpiryDateDesc(String email);
}
