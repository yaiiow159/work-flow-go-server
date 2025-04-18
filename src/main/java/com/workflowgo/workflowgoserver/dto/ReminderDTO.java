package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.Reminder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDTO {
    
    private UUID id;
    private UUID interviewId;
    private LocalDateTime time;
    private String message;
    private boolean completed;
    
    public static ReminderDTO fromEntity(Reminder reminder) {
        return ReminderDTO.builder()
                .id(reminder.getId())
                .interviewId(reminder.getInterview() != null ? reminder.getInterview().getId() : null)
                .time(reminder.getTime())
                .message(reminder.getMessage())
                .completed(reminder.isCompleted())
                .build();
    }
}
