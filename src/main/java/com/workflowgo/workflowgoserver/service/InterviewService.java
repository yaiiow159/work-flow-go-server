package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.InterviewDTO;
import com.workflowgo.workflowgoserver.dto.InterviewStatisticsDTO;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {
    
    private final InterviewRepository interviewRepository;
    
    @Transactional(readOnly = true)
    public List<Interview> getAllInterviews(
            InterviewStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            String company,
            String sort,
            String order) {
        
        log.debug("Fetching interviews with filters - status: {}, fromDate: {}, toDate: {}, company: {}, sort: {}, order: {}", 
                status, fromDate, toDate, company, sort, order);
        
        List<Interview> interviews = interviewRepository.findByFilters(status, fromDate, toDate, company);
        
        if (sort != null && !sort.isEmpty()) {
            interviews = sortInterviews(interviews, sort, order);
        }
        
        return interviews;
    }
    
    @Transactional(readOnly = true)
    public Interview getInterviewById(UUID id) {
        log.debug("Fetching interview with id: {}", id);
        return interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + id));
    }
    
    @Transactional
    public Interview createInterview(Interview interview) {
        log.debug("Creating new interview for company: {}", interview.getCompanyName());
        return interviewRepository.save(interview);
    }
    
    @Transactional
    public Interview updateInterview(UUID id, Interview interviewDetails) {
        log.debug("Updating interview with id: {}", id);
        Interview interview = getInterviewById(id);
        
        updateInterviewFields(interview, interviewDetails);
        
        return interviewRepository.save(interview);
    }
    
    private void updateInterviewFields(Interview interview, Interview interviewDetails) {
        interview.setCompanyName(interviewDetails.getCompanyName());
        interview.setPosition(interviewDetails.getPosition());
        interview.setDate(interviewDetails.getDate());
        interview.setTime(interviewDetails.getTime());
        interview.setType(interviewDetails.getType());
        interview.setStatus(interviewDetails.getStatus());
        interview.setLocation(interviewDetails.getLocation());
        interview.setNotes(interviewDetails.getNotes());
        interview.setContactPerson(interviewDetails.getContactPerson());
        interview.setRating(interviewDetails.getRating());
        interview.setFeedback(interviewDetails.getFeedback());
    }
    
    @Transactional
    public Interview updateInterviewStatus(UUID id, InterviewStatus status) {
        log.debug("Updating status of interview with id: {} to {}", id, status);
        Interview interview = getInterviewById(id);
        interview.setStatus(status);
        return interviewRepository.save(interview);
    }
    
    @Transactional
    public void deleteInterview(UUID id) {
        log.debug("Deleting interview with id: {}", id);
        Interview interview = getInterviewById(id);
        interviewRepository.delete(interview);
    }
    
    @Transactional(readOnly = true)
    public InterviewStatisticsDTO getInterviewStatistics(LocalDate fromDate, LocalDate toDate) {
        log.debug("Generating interview statistics for period: {} to {}", fromDate, toDate);
        
        List<Interview> interviews;
        if (fromDate != null && toDate != null) {
            interviews = interviewRepository.findByDateBetween(fromDate, toDate);
        } else {
            interviews = interviewRepository.findAll();
        }
        
        int totalInterviews = interviews.size();
        
        LocalDate today = LocalDate.now();
        int upcomingInterviews = (int) interviews.stream()
                .filter(i -> i.getDate().isAfter(today) || i.getDate().isEqual(today))
                .count();
        
        int completedInterviews = (int) interviews.stream()
                .filter(i -> i.getStatus() == InterviewStatus.COMPLETED)
                .count();
        
        int rejectedInterviews = (int) interviews.stream()
                .filter(i -> i.getStatus() == InterviewStatus.REJECTED)
                .count();
        
        Map<InterviewStatus, Integer> byStatus = Arrays.stream(InterviewStatus.values())
                .collect(Collectors.toMap(
                        status -> status,
                        status -> (int) interviews.stream().filter(i -> i.getStatus() == status).count()
                ));
        
        Map<String, Long> companyCountMap = interviews.stream()
                .collect(Collectors.groupingBy(Interview::getCompanyName, Collectors.counting()));
        
        List<InterviewStatisticsDTO.CompanyCount> byCompany = companyCountMap.entrySet().stream()
                .map(entry -> new InterviewStatisticsDTO.CompanyCount(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
        
        Map<String, Long> monthCountMap = interviews.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getDate().getYear() + "-" + i.getDate().getMonthValue(),
                        Collectors.counting()
                ));
        
        List<InterviewStatisticsDTO.MonthCount> byMonth = monthCountMap.entrySet().stream()
                .map(entry -> new InterviewStatisticsDTO.MonthCount(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
        
        return InterviewStatisticsDTO.builder()
                .totalInterviews(totalInterviews)
                .upcomingInterviews(upcomingInterviews)
                .completedInterviews(completedInterviews)
                .rejectedInterviews(rejectedInterviews)
                .byStatus(byStatus)
                .byCompany(byCompany)
                .byMonth(byMonth)
                .build();
    }
    
    private List<Interview> sortInterviews(List<Interview> interviews, String sort, String order) {
        Comparator<Interview> comparator = switch (sort.toLowerCase()) {
            case "companyname" -> Comparator.comparing(Interview::getCompanyName);
            case "position" -> Comparator.comparing(Interview::getPosition);
            case "status" -> Comparator.comparing(interview -> interview.getStatus().name());
            case "createdat" -> Comparator.comparing(Interview::getCreatedAt);
            default -> Comparator.comparing(Interview::getDate)
                    .thenComparing(Interview::getTime);
        };
        
        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        
        return interviews.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    public InterviewDTO convertToDTO(Interview interview) {
        return InterviewDTO.builder()
                .id(interview.getId())
                .companyName(interview.getCompanyName())
                .position(interview.getPosition())
                .date(interview.getDate())
                .time(interview.getTime())
                .type(interview.getType())
                .status(interview.getStatus())
                .location(interview.getLocation())
                .notes(interview.getNotes())
                .contactPerson(interview.getContactPerson())
                .questions(interview.getQuestions())
                .documents(interview.getDocuments())
                .rating(interview.getRating())
                .feedback(interview.getFeedback())
                .createdAt(interview.getCreatedAt())
                .updatedAt(interview.getUpdatedAt())
                .build();
    }
}
