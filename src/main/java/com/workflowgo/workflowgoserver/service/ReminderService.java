package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.ReminderDTO;
import com.workflowgo.workflowgoserver.model.Reminder;

import java.util.List;
import java.util.UUID;

public interface ReminderService {
    
    List<ReminderDTO> getAllReminders();
    
    List<ReminderDTO> getRemindersByUserId(UUID userId);
    
    ReminderDTO getReminderById(UUID id);
    
    List<ReminderDTO> getRemindersByInterviewId(UUID interviewId);

    ReminderDTO createReminderFromDTO(ReminderDTO reminderDTO);

    ReminderDTO updateReminderFromDTO(UUID id, ReminderDTO reminderDTO);
    
    ReminderDTO markReminderAsCompleted(UUID id);
    
    void deleteReminder(UUID id);

    List<ReminderDTO> getPendingReminders();
}
