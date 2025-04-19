package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.InterviewDTO;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import com.workflowgo.workflowgoserver.payload.InterviewRequest;
import com.workflowgo.workflowgoserver.payload.StatusUpdateRequest;
import com.workflowgo.workflowgoserver.security.CurrentUser;
import com.workflowgo.workflowgoserver.security.UserPrincipal;
import com.workflowgo.workflowgoserver.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public List<InterviewDTO> getInterviews(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(required = false) InterviewStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String company,
            @RequestParam(required = false, defaultValue = "date") String sort,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        
        List<Interview> interviews = interviewService.getInterviews(currentUser.getId(), status, from, to, company, sort, order);
        return InterviewDTO.fromInterviews(interviews);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<InterviewDTO> createInterview(@Valid @RequestBody InterviewRequest interviewRequest,
                                           @CurrentUser UserPrincipal currentUser) {
        Interview interview = interviewService.createInterview(interviewRequest, currentUser.getId());
        InterviewDTO interviewDTO = InterviewDTO.fromInterview(interview);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(interview.getId()).toUri();

        return ResponseEntity.created(location)
                .body(interviewDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public InterviewDTO getInterviewById(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        Interview interview = interviewService.getInterviewById(id, currentUser.getId());
        return InterviewDTO.fromInterview(interview);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public InterviewDTO updateInterview(@PathVariable Long id,
                                   @Valid @RequestBody InterviewRequest interviewRequest,
                                   @CurrentUser UserPrincipal currentUser) {
        Interview interview = interviewService.updateInterview(id, interviewRequest, currentUser.getId());
        return InterviewDTO.fromInterview(interview);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    public InterviewDTO updateInterviewStatus(@PathVariable Long id,
                                         @Valid @RequestBody StatusUpdateRequest statusRequest,
                                         @CurrentUser UserPrincipal currentUser) {
        Interview interview = interviewService.updateInterviewStatus(id, statusRequest.getStatus(), currentUser.getId());
        return InterviewDTO.fromInterview(interview);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteInterview(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        interviewService.deleteInterview(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
