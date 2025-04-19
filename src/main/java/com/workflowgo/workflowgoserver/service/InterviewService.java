package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.InterviewDTO;
import com.workflowgo.workflowgoserver.dto.InterviewStatisticsDTO;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InterviewService {

    List<InterviewDTO> getAllInterviews(InterviewStatus status, LocalDate fromDate, LocalDate toDate, String company, String sort, String order, UUID userId);
    
    InterviewDTO getInterviewById(UUID id);
    
    InterviewDTO getInterviewDTOById(UUID id);

    InterviewDTO createInterviewFromDTO(InterviewDTO interviewDTO);

    InterviewDTO updateInterviewFromDTO(UUID id, InterviewDTO interviewDTO);
    
    InterviewDTO updateInterviewStatus(UUID id, InterviewStatus status);
    
    void deleteInterview(UUID id);
    
    InterviewStatisticsDTO getInterviewStatistics(LocalDate fromDate, LocalDate toDate);
    
    InterviewStatisticsDTO getInterviewStatistics(LocalDate fromDate, LocalDate toDate, UUID userId);

}
