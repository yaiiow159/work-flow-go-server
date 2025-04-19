package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.InterviewDTO;
import com.workflowgo.workflowgoserver.dto.InterviewStatisticsDTO;
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
    public ResponseEntity<List<InterviewDTO>> getAllInterviews(
            @RequestParam(required = false) InterviewStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String company,
            @RequestParam(required = false, defaultValue = "date") String sort,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false) UUID userId) {
        
        List<InterviewDTO> interviews = interviewService.getAllInterviews(status, from, to, company, sort, order, userId);
        return ResponseEntity.ok(interviews);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get interview by ID", description = "Retrieve a specific interview by ID")
    public ResponseEntity<InterviewDTO> getInterviewById(@PathVariable UUID id) {
        InterviewDTO interview = interviewService.getInterviewDTOById(id);
        return ResponseEntity.ok(interview);
    }
    
    @PostMapping
    @Operation(summary = "Create interview", description = "Create a new interview")
    public ResponseEntity<InterviewDTO> createInterview(@Valid @RequestBody InterviewDTO interviewDTO) {
        InterviewDTO createdInterview = interviewService.createInterviewFromDTO(interviewDTO);
        return new ResponseEntity<>(createdInterview, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update interview", description = "Update an existing interview")
    public ResponseEntity<InterviewDTO> updateInterview(
            @PathVariable UUID id,
            @Valid @RequestBody InterviewDTO interviewDTO) {
        
        InterviewDTO updatedInterview = interviewService.updateInterviewFromDTO(id, interviewDTO);
        return ResponseEntity.ok(updatedInterview);
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update interview status", description = "Update only the status of an interview")
    public ResponseEntity<InterviewDTO> updateInterviewStatus(
            @PathVariable UUID id,
            @Valid @RequestBody Map<String, InterviewStatus> statusUpdate) {
        
        InterviewStatus status = statusUpdate.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        
        InterviewDTO updatedInterview = interviewService.updateInterviewStatus(id, status);
        return ResponseEntity.ok(updatedInterview);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete interview", description = "Delete an interview")
    public ResponseEntity<Void> deleteInterview(@PathVariable UUID id) {
        interviewService.deleteInterview(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get interview statistics", description = "Get statistics about interviews")
    public ResponseEntity<InterviewStatisticsDTO> getInterviewStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID userId) {
        
        InterviewStatisticsDTO statistics = interviewService.getInterviewStatistics(from, to, userId);
        return ResponseEntity.ok(statistics);
    }
}
