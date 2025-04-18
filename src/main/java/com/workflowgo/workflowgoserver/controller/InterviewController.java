package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import com.workflowgo.workflowgoserver.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
@Tag(name = "Interviews", description = "Interview management endpoints")
public class InterviewController {
    
    private final InterviewService interviewService;
    
    @GetMapping
    @Operation(summary = "Get all interviews", description = "Retrieve all interviews with optional filtering and sorting")
    public ResponseEntity<List<Interview>> getAllInterviews(
            @RequestParam(required = false) InterviewStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String company,
            @RequestParam(required = false, defaultValue = "date") String sort,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        
        List<Interview> interviews = interviewService.getAllInterviews(status, from, to, company, sort, order);
        return ResponseEntity.ok(interviews);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get interview by ID", description = "Retrieve a specific interview by ID")
    public ResponseEntity<Interview> getInterviewById(@PathVariable UUID id) {
        Interview interview = interviewService.getInterviewById(id);
        return ResponseEntity.ok(interview);
    }
    
    @PostMapping
    @Operation(summary = "Create interview", description = "Create a new interview")
    public ResponseEntity<Interview> createInterview(@Valid @RequestBody Interview interview) {
        Interview createdInterview = interviewService.createInterview(interview);
        return new ResponseEntity<>(createdInterview, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update interview", description = "Update an existing interview")
    public ResponseEntity<Interview> updateInterview(
            @PathVariable UUID id,
            @Valid @RequestBody Interview interviewDetails) {
        
        Interview updatedInterview = interviewService.updateInterview(id, interviewDetails);
        return ResponseEntity.ok(updatedInterview);
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update interview status", description = "Update only the status of an interview")
    public ResponseEntity<Interview> updateInterviewStatus(
            @PathVariable UUID id,
            @Valid @RequestBody Map<String, InterviewStatus> statusUpdate) {
        
        InterviewStatus status = statusUpdate.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Interview updatedInterview = interviewService.updateInterviewStatus(id, status);
        return ResponseEntity.ok(updatedInterview);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete interview", description = "Delete an interview")
    public ResponseEntity<Void> deleteInterview(@PathVariable UUID id) {
        interviewService.deleteInterview(id);
        return ResponseEntity.noContent().build();
    }
}
