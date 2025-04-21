package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.InterviewDTO;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.payload.ApiResponse;
import com.workflowgo.workflowgoserver.payload.StatusUpdateRequest;
import com.workflowgo.workflowgoserver.security.CurrentUser;
import com.workflowgo.workflowgoserver.security.UserPrincipal;
import com.workflowgo.workflowgoserver.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<InterviewDTO>> getInterviews(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String company,
            @RequestParam(required = false, defaultValue = "date") String sort,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        
        List<Interview> interviews = interviewService.getInterviews(currentUser.getId(), status, from, to, company, sort, order);
        return ResponseEntity.ok(InterviewDTO.fromInterviews(interviews));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createInterview(@Valid @RequestBody InterviewDTO interviewRequest,
                                           @CurrentUser UserPrincipal currentUser) {
        try {
            Interview interview = interviewService.createInterview(interviewRequest, currentUser.getId());
            InterviewDTO interviewDTO = InterviewDTO.fromInterview(interview);
            
            return ResponseEntity.ok(interviewDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Failed to create interview: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getInterviewById(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        try {
            Interview interview = interviewService.getInterviewById(id, currentUser.getId());
            return ResponseEntity.ok(InterviewDTO.fromInterview(interview));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Interview not found: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateInterview(@PathVariable Long id,
                                   @Valid @RequestBody InterviewDTO interviewRequest,
                                   @CurrentUser UserPrincipal currentUser) {
        try {
            Interview interview = interviewService.updateInterview(id, interviewRequest, currentUser.getId());
            return ResponseEntity.ok(InterviewDTO.fromInterview(interview));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Failed to update interview: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateInterviewStatus(@PathVariable Long id,
                                         @Valid @RequestBody StatusUpdateRequest statusRequest,
                                         @CurrentUser UserPrincipal currentUser) {
        try {
            Interview interview = interviewService.updateInterviewStatus(id, statusRequest.getStatus(), currentUser.getId());
            return ResponseEntity.ok(InterviewDTO.fromInterview(interview));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Failed to update interview status: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteInterview(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        try {
            interviewService.deleteInterview(id, currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Interview deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Failed to delete interview: " + e.getMessage()));
        }
    }
}
