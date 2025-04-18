package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.InterviewStatisticsDTO;
import com.workflowgo.workflowgoserver.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Statistics endpoints")
public class StatisticsController {
    
    private final InterviewService interviewService;
    
    @GetMapping("/interviews")
    @Operation(summary = "Get interview statistics", description = "Get interview statistics with optional date range filtering")
    public ResponseEntity<InterviewStatisticsDTO> getInterviewStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        InterviewStatisticsDTO statistics = interviewService.getInterviewStatistics(from, to);
        return ResponseEntity.ok(statistics);
    }
}
