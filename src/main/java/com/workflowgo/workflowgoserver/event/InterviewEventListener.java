package com.workflowgo.workflowgoserver.event;

import com.workflowgo.workflowgoserver.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class InterviewEventListener {

    private final EmailService emailService;

    public InterviewEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInterviewEvent(InterviewEvent event) {
        log.debug("Processing interview event after transaction commit: {}", event.getChangeType());
        try {
            emailService.sendInterviewUpdateNotification(
                event.getEmail(), 
                event.getInterview(), 
                event.getChangeType()
            );
            log.info("Interview notification sent successfully for change type: {}", event.getChangeType());
        } catch (Exception e) {
            log.error("Failed to send interview notification for change type: {}", event.getChangeType(), e);
        }
    }
}
