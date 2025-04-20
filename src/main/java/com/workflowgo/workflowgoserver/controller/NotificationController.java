package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.NotificationDTO;
import com.workflowgo.workflowgoserver.payload.NotificationRequest;
import com.workflowgo.workflowgoserver.security.CurrentUser;
import com.workflowgo.workflowgoserver.security.UserPrincipal;
import com.workflowgo.workflowgoserver.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(@CurrentUser UserPrincipal userPrincipal) {
        List<NotificationDTO> notifications = notificationService.getAllNotificationsForUser(userPrincipal.getId());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Long> getUnreadCount(@CurrentUser UserPrincipal userPrincipal) {
        long unreadCount = notificationService.getUnreadCount(userPrincipal.getId());
        return ResponseEntity.ok(unreadCount);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<NotificationDTO> createNotification(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody NotificationRequest request) {
        NotificationDTO notification = notificationService.createNotification(
                userPrincipal.getId(),
                request.getTitle(),
                request.getMessage(),
                request.getType(),
                request.getRelatedEntityId(),
                request.getRelatedEntityType()
        );
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-all-read")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> markAllAsRead(@CurrentUser UserPrincipal userPrincipal) {
        notificationService.markAllAsRead(userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}
