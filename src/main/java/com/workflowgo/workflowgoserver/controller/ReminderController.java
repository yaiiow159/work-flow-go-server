package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.ReminderDTO;
import com.workflowgo.workflowgoserver.model.Reminder;
import com.workflowgo.workflowgoserver.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reminders")
@RequiredArgsConstructor
@Tag(name = "Reminders", description = "Reminder management endpoints")
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping
    @Operation(summary = "Get all reminders", description = "Retrieve all reminders")
    public ResponseEntity<List<ReminderDTO>> getAllReminders() {
        List<ReminderDTO> reminders = reminderService.getAllReminders().stream()
                .map(ReminderDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reminder by ID", description = "Retrieve a specific reminder by ID")
    public ResponseEntity<ReminderDTO> getReminderById(@PathVariable UUID id) {
        Reminder reminder = reminderService.getReminderById(id);
        return ResponseEntity.ok(ReminderDTO.fromEntity(reminder));
    }

    @GetMapping("/interview/{interviewId}")
    @Operation(summary = "Get reminders by interview ID", description = "Retrieve all reminders for a specific interview")
    public ResponseEntity<List<ReminderDTO>> getRemindersByInterviewId(@PathVariable UUID interviewId) {
        List<ReminderDTO> reminders = reminderService.getRemindersByInterviewId(interviewId).stream()
                .map(ReminderDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reminders);
    }

    @PostMapping
    @Operation(summary = "Create reminder", description = "Create a new reminder")
    public ResponseEntity<ReminderDTO> createReminder(@Valid @RequestBody Reminder reminder) {
        Reminder createdReminder = reminderService.createReminder(reminder);
        return new ResponseEntity<>(ReminderDTO.fromEntity(createdReminder), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reminder", description = "Update an existing reminder")
    public ResponseEntity<ReminderDTO> updateReminder(
            @PathVariable UUID id,
            @Valid @RequestBody Reminder reminderDetails) {
        
        Reminder updatedReminder = reminderService.updateReminder(id, reminderDetails);
        return ResponseEntity.ok(ReminderDTO.fromEntity(updatedReminder));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark reminder as completed", description = "Mark a reminder as completed")
    public ResponseEntity<ReminderDTO> markReminderAsCompleted(@PathVariable UUID id) {
        Reminder updatedReminder = reminderService.markReminderAsCompleted(id);
        return ResponseEntity.ok(ReminderDTO.fromEntity(updatedReminder));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reminder", description = "Delete a reminder")
    public ResponseEntity<Void> deleteReminder(@PathVariable UUID id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }
}
