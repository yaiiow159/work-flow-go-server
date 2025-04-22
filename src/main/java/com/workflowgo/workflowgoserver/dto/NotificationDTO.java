package com.workflowgo.workflowgoserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String id;
    private String userId;
    private String title;
    private String message;
    private String type;
    private boolean isRead;
    private ZonedDateTime createdAt;
    private String relatedEntityId;
    private String relatedEntityType;
}
