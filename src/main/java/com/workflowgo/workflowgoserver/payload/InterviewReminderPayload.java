package com.workflowgo.workflowgoserver.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.workflowgo.workflowgoserver.dto.InterviewDTO;
import com.workflowgo.workflowgoserver.dto.NotificationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterviewReminderPayload {
    private InterviewDTO interview;
    private String title;
    private String message;
    private String reminderLabel;
    private String id;
    private NotificationDTO notification;
}
