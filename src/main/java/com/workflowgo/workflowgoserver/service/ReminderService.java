package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.Reminder;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import com.workflowgo.workflowgoserver.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final InterviewRepository interviewRepository;

    @Transactional(readOnly = true)
    public List<Reminder> getAllReminders() {
        log.debug("Fetching all reminders");
        return reminderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Reminder getReminderById(UUID id) {
        log.debug("Fetching reminder with id: {}", id);
        return reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Reminder> getRemindersByInterviewId(UUID interviewId) {
        log.debug("Fetching reminders for interview with id: {}", interviewId);
        return reminderRepository.findByInterviewId(interviewId);
    }

    @Transactional
    public Reminder createReminder(Reminder reminder) {
        log.debug("Creating new reminder for interview: {}", reminder.getInterview().getId());
        
        UUID interviewId = reminder.getInterview().getId();
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));
        
        reminder.setInterview(interview);
        return reminderRepository.save(reminder);
    }

    @Transactional
    public Reminder updateReminder(UUID id, Reminder reminderDetails) {
        log.debug("Updating reminder with id: {}", id);
        Reminder reminder = getReminderById(id);
        
        reminder.setTime(reminderDetails.getTime());
        reminder.setMessage(reminderDetails.getMessage());
        reminder.setCompleted(reminderDetails.isCompleted());
        
        if (reminderDetails.getInterview() != null &&
                !reminder.getInterview().getId().equals(reminderDetails.getInterview().getId())) {
            UUID interviewId = reminderDetails.getInterview().getId();
            Interview interview = interviewRepository.findById(interviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));
            reminder.setInterview(interview);
        }
        
        return reminderRepository.save(reminder);
    }

    @Transactional
    public Reminder markReminderAsCompleted(UUID id) {
        log.debug("Marking reminder with id: {} as completed", id);
        Reminder reminder = getReminderById(id);
        reminder.setCompleted(true);
        return reminderRepository.save(reminder);
    }

    @Transactional
    public void deleteReminder(UUID id) {
        log.debug("Deleting reminder with id: {}", id);
        Reminder reminder = getReminderById(id);
        reminderRepository.delete(reminder);
    }
    
    @Transactional(readOnly = true)
    public List<Reminder> getPendingReminders() {
        log.debug("Fetching pending reminders");
        return reminderRepository.findByIsCompletedFalseAndTimeBefore(LocalDateTime.now());
    }
}
