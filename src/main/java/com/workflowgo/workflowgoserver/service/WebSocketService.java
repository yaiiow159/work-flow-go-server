package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.InterviewDTO;
import com.workflowgo.workflowgoserver.dto.NotificationDTO;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.payload.InterviewReminderPayload;
import com.workflowgo.workflowgoserver.payload.NotificationPayload;
import com.workflowgo.workflowgoserver.payload.SystemMessagePayload;
import com.workflowgo.workflowgoserver.payload.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public void sendNotification(User user, NotificationDTO notification) {
        try {
            String destination = "/queue/user." + user.getId();
            
            NotificationPayload payload = NotificationPayload.builder()
                    .notification(notification)
                    .build();
            
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(WebSocketMessage.WebSocketMessageType.NOTIFICATION)
                    .data(payload)
                    .build();
            
            messagingTemplate.convertAndSend(destination, message);
            log.info("Notification sent to user {}: {}", user.getId(), notification.getTitle());
        } catch (Exception e) {
            log.error("Error sending notification via WebSocket", e);
        }
    }

    public void sendNotificationUpdate(User user) {
        try {
            String destination = "/queue/user." + user.getId();
            
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(WebSocketMessage.WebSocketMessageType.NOTIFICATION_UPDATE)
                    .data(null)
                    .build();
            
            messagingTemplate.convertAndSend(destination, message);
            log.info("Notification update sent to user {}", user.getId());
        } catch (Exception e) {
            log.error("Error sending notification update via WebSocket", e);
        }
    }


    public void sendInterviewReminder(User user, InterviewDTO interview, String reminderLabel) {
        try {
            String destination = "/queue/user." + user.getId();
            
            NotificationDTO notification = notificationService.createNotification(
                    user.getId(),
                    "Interview Reminder: " + interview.getCompanyName(),
                    interview.getPosition() + " interview at " + interview.getTime(),
                    "info",
                    String.valueOf(interview.getId()),
                    "interview"
            );
            
            InterviewReminderPayload payload = InterviewReminderPayload.builder()
                    .interview(interview)
                    .title("Interview Reminder: " + interview.getCompanyName())
                    .message(interview.getPosition() + " interview at " + interview.getTime())
                    .reminderLabel(reminderLabel)
                    .id(UUID.randomUUID().toString())
                    .notification(notification)
                    .build();
            
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(WebSocketMessage.WebSocketMessageType.INTERVIEW_REMINDER)
                    .data(payload)
                    .build();
            
            messagingTemplate.convertAndSend(destination, message);
            log.info("Interview reminder sent to user {}: {}", user.getId(), interview.getCompanyName());
        } catch (Exception e) {
            log.error("Error sending interview reminder via WebSocket", e);
        }
    }

    public void sendSystemMessage(User user, String title, String message) {
        try {
            String destination = "/queue/user." + user.getId();
            
            SystemMessagePayload payload = SystemMessagePayload.builder()
                    .title(title)
                    .message(message)
                    .build();
            
            WebSocketMessage message1 = WebSocketMessage.builder()
                    .type(WebSocketMessage.WebSocketMessageType.SYSTEM_MESSAGE)
                    .data(payload)
                    .build();
            
            messagingTemplate.convertAndSend(destination, message1);
            log.info("System message sent to user {}: {}", user.getId(), title);
        } catch (Exception e) {
            log.error("Error sending system message via WebSocket", e);
        }
    }
}
