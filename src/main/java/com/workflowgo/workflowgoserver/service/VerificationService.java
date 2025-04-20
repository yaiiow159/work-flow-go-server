package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.config.AppProperties;
import com.workflowgo.workflowgoserver.model.VerificationCode;
import com.workflowgo.workflowgoserver.repository.VerificationCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class VerificationService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final AppProperties appProperties;
    private final Random random = new Random();

    public VerificationService(VerificationCodeRepository verificationCodeRepository, EmailService emailService, AppProperties appProperties) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.emailService = emailService;
        this.appProperties = appProperties;
    }

    @Transactional
    public void requestEmailVerification(String email) {
        Optional<VerificationCode> existingCode = verificationCodeRepository.findFirstByEmailOrderByExpiryDateDesc(email);
        
        if (existingCode.isPresent() && !existingCode.get().isExpired() && !existingCode.get().isUsed()) {
            log.info("Using existing verification code for email: {}", email);
            emailService.sendVerificationEmail(
                email, 
                existingCode.get().getCode(), 
                appProperties.getEmail().getVerificationCodeExpirationMinutes()
            );
            return;
        }
        
        String code = generateVerificationCode();
        int expirationMinutes = appProperties.getEmail().getVerificationCodeExpirationMinutes();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(expirationMinutes);
        
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setExpiryDate(expiryDate);
        verificationCode.setUsed(false);
        verificationCodeRepository.save(verificationCode);
        
        emailService.sendVerificationEmail(email, code, expirationMinutes);
        log.info("New verification code generated for email: {}", email);
    }
    
    @Transactional
    public boolean verifyEmailCode(String email, String code) {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findValidCode(
            email, code, LocalDateTime.now());
            
        if (verificationCode.isPresent()) {
            VerificationCode validCode = verificationCode.get();
            validCode.setUsed(true);
            verificationCodeRepository.save(validCode);
            log.info("Email verification successful for: {}", email);
            return true;
        }
        
        log.warn("Email verification failed for: {}", email);
        return false;
    }
    
    private String generateVerificationCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
