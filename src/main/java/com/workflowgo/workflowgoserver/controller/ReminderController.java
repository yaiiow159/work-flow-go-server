package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.ReminderDTO;
import com.workflowgo.workflowgoserver.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reminders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reminders", description = "Reminder management endpoints")
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping
    @Operation(summary = "Get all reminders", description = "Retrieve all reminders")
    public ResponseEntity<List<ReminderDTO>> getAllReminders(@RequestParam(required = false) UUID userId) {
        List<ReminderDTO> reminders;
        if (userId != null) {
            log.debug("Fetching reminders for user ID: {}", userId);
            reminders = reminderService.getRemindersByUserId(userId);
        } else {
            log.debug("Fetching all reminders");
            reminders = reminderService.getAllReminders();
        }
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reminder by ID", description = "Retrieve a specific reminder by ID")
    public ResponseEntity<ReminderDTO> getReminderById(@PathVariable UUID id) {
        log.debug("Fetching reminder with ID: {}", id);
        ReminderDTO reminder = reminderService.getReminderById(id);
        return ResponseEntity.ok(reminder);
    }

    @GetMapping("/interview/{interviewId}")
    @Operation(summary = "Get reminders by interview ID", description = "Retrieve all reminders for a specific interview")
    public ResponseEntity<List<ReminderDTO>> getRemindersByInterviewId(@PathVariable UUID interviewId) {
        log.debug("Fetching reminders for interview ID: {}", interviewId);
        List<ReminderDTO> reminders = reminderService.getRemindersByInterviewId(interviewId);
        return ResponseEntity.ok(reminders);
    }

    @PostMapping
    @Operation(summary = "Create reminder", description = "Create a new reminder")
    public ResponseEntity<ReminderDTO> createReminder(@Valid @RequestBody ReminderDTO reminderDTO) {
        log.debug("Creating new reminder for interview ID: {}", reminderDTO.getInterviewId());
        ReminderDTO createdReminder = reminderService.createReminderFromDTO(reminderDTO);
        return new ResponseEntity<>(createdReminder, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reminder", description = "Update an existing reminder")
    public ResponseEntity<ReminderDTO> updateReminder(
            @PathVariable UUID id,
            @Valid @RequestBody ReminderDTO reminderDTO) {
        
        log.debug("Updating reminder with ID: {}", id);
        ReminderDTO updatedReminder = reminderService.updateReminderFromDTO(id, reminderDTO);
        return ResponseEntity.ok(updatedReminder);
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark reminder as completed", description = "Mark a reminder as completed")
    public ResponseEntity<ReminderDTO> markReminderAsCompleted(@PathVariable UUID id) {
        log.debug("Marking reminder with ID: {} as completed", id);
        ReminderDTO updatedReminder = reminderService.markReminderAsCompleted(id);
        return ResponseEntity.ok(updatedReminder);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reminder", description = "Delete a reminder")
    public ResponseEntity<Void> deleteReminder(@PathVariable UUID id) {
        log.debug("Deleting reminder with ID: {}", id);
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }
}
