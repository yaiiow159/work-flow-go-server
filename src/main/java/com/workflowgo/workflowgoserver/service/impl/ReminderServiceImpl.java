package com.workflowgo.workflowgoserver.service.impl;

import com.workflowgo.workflowgoserver.dto.ReminderDTO;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.Reminder;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import com.workflowgo.workflowgoserver.repository.ReminderRepository;
import com.workflowgo.workflowgoserver.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final InterviewRepository interviewRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<ReminderDTO> getAllReminders() {
        log.debug("Fetching all reminders");
        return reminderRepository.findAll().stream()
                .map(ReminderDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReminderDTO> getRemindersByUserId(UUID userId) {
        log.debug("Fetching reminders for user with id: {}", userId);
        return reminderRepository.findByInterviewUserId(userId).stream()
                .map(ReminderDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ReminderDTO getReminderById(UUID id) {
        log.debug("Fetching reminder with id: {}", id);
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
        return ReminderDTO.fromEntity(reminder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReminderDTO> getRemindersByInterviewId(UUID interviewId) {
        log.debug("Fetching reminders for interview with id: {}", interviewId);
        return reminderRepository.findByInterviewId(interviewId).stream()
                .map(ReminderDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ReminderDTO createReminderFromDTO(ReminderDTO reminderDTO) {
        log.debug("Creating new reminder from DTO for interview: {}", reminderDTO.getInterviewId());

        UUID interviewId = reminderDTO.getInterviewId();
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));

        Reminder reminder = new Reminder();
        reminder.setInterview(interview);
        reminder.setTime(reminderDTO.getTime());
        reminder.setMessage(reminderDTO.getMessage());
        reminder.setCompleted(reminderDTO.isCompleted());

        Reminder savedReminder = reminderRepository.save(reminder);
        return ReminderDTO.fromEntity(savedReminder);
    }
    
    @Override
    @Transactional
    public ReminderDTO updateReminderFromDTO(UUID id, ReminderDTO reminderDTO) {
        log.debug("Updating reminder from DTO with id: {}", id);
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));

        reminder.setTime(reminderDTO.getTime());
        reminder.setMessage(reminderDTO.getMessage());
        reminder.setCompleted(reminderDTO.isCompleted());

        if (reminderDTO.getInterviewId() != null &&
                !reminder.getInterview().getId().equals(reminderDTO.getInterviewId())) {
            UUID interviewId = reminderDTO.getInterviewId();
            Interview interview = interviewRepository.findById(interviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));
            reminder.setInterview(interview);
        }

        Reminder updatedReminder = reminderRepository.save(reminder);
        return ReminderDTO.fromEntity(updatedReminder);
    }
    
    @Override
    @Transactional
    public ReminderDTO markReminderAsCompleted(UUID id) {
        log.debug("Marking reminder with id: {} as completed", id);
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
        reminder.setCompleted(true);
        Reminder updatedReminder = reminderRepository.save(reminder);
        return ReminderDTO.fromEntity(updatedReminder);
    }
    
    @Override
    @Transactional
    public void deleteReminder(UUID id) {
        log.debug("Deleting reminder with id: {}", id);
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
        reminderRepository.delete(reminder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderDTO> getPendingReminders() {
        log.debug("Fetching pending reminders");
        return reminderRepository.findByIsCompletedFalseAndTimeBefore(LocalDateTime.now()).stream()
                .map(ReminderDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
