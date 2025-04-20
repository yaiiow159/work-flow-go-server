package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.config.AppProperties;
import com.workflowgo.workflowgoserver.model.VerificationCode;
import com.workflowgo.workflowgoserver.repository.VerificationCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private AppProperties.Email emailConfig;

    @InjectMocks
    private VerificationService verificationService;

    private final String testEmail = "test@example.com";
    private final String testCode = "123456";

    @BeforeEach
    void setUp() {
        when(appProperties.getEmail()).thenReturn(emailConfig);
        when(emailConfig.getVerificationCodeExpirationMinutes()).thenReturn(30);
    }

    @Test
    void requestEmailVerification_NewCode() {
        when(verificationCodeRepository.findFirstByEmailOrderByExpiryDateDesc(anyString()))
                .thenReturn(Optional.empty());
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyInt());

        verificationService.requestEmailVerification(testEmail);

        verify(verificationCodeRepository).findFirstByEmailOrderByExpiryDateDesc(testEmail);
        verify(verificationCodeRepository).save(any(VerificationCode.class));
        verify(emailService).sendVerificationEmail(eq(testEmail), anyString(), eq(30));
    }

    @Test
    void requestEmailVerification_ExistingValidCode() {
        VerificationCode existingCode = new VerificationCode();
        existingCode.setEmail(testEmail);
        existingCode.setCode(testCode);
        existingCode.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        existingCode.setUsed(false);

        when(verificationCodeRepository.findFirstByEmailOrderByExpiryDateDesc(testEmail))
                .thenReturn(Optional.of(existingCode));

        verificationService.requestEmailVerification(testEmail);

        verify(verificationCodeRepository).findFirstByEmailOrderByExpiryDateDesc(testEmail);
        verify(verificationCodeRepository, never()).save(any(VerificationCode.class));
        verify(emailService).sendVerificationEmail(testEmail, testCode, 30);
    }

    @Test
    void verifyEmailCode_ValidCode() {
        VerificationCode validCode = new VerificationCode();
        validCode.setEmail(testEmail);
        validCode.setCode(testCode);
        validCode.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        validCode.setUsed(false);

        when(verificationCodeRepository.findValidCode(eq(testEmail), eq(testCode), any(LocalDateTime.class)))
                .thenReturn(Optional.of(validCode));
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(validCode);

        boolean result = verificationService.verifyEmailCode(testEmail, testCode);

        assertTrue(result);
        verify(verificationCodeRepository).findValidCode(eq(testEmail), eq(testCode), any(LocalDateTime.class));
        verify(verificationCodeRepository).save(validCode);
        assertTrue(validCode.isUsed());
    }

    @Test
    void verifyEmailCode_InvalidCode() {
        when(verificationCodeRepository.findValidCode(eq(testEmail), eq(testCode), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        boolean result = verificationService.verifyEmailCode(testEmail, testCode);

        assertFalse(result);
        verify(verificationCodeRepository).findValidCode(eq(testEmail), eq(testCode), any(LocalDateTime.class));
        verify(verificationCodeRepository, never()).save(any(VerificationCode.class));
    }
}
