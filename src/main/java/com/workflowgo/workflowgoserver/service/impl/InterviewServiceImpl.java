package com.workflowgo.workflowgoserver.service.impl;

import com.workflowgo.workflowgoserver.dto.InterviewDTO;
import com.workflowgo.workflowgoserver.dto.InterviewStatisticsDTO;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import com.workflowgo.workflowgoserver.service.InterviewService;
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
public class InterviewServiceImpl implements InterviewService {
    
    private final InterviewRepository interviewRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InterviewDTO> getAllInterviews(InterviewStatus status, LocalDate fromDate, LocalDate toDate, String company, String sort, String order, UUID userId) {
        log.debug("Fetching interviews with filters - status: {}, fromDate: {}, toDate: {}, company: {}, sort: {}, order: {}, userId: {}", 
                status, fromDate, toDate, company, sort, order, userId);
        
        List<Interview> interviews;
        if (userId != null) {
            interviews = interviewRepository.findByFiltersAndUserId(status, fromDate, toDate, company, userId);
        } else {
            interviews = interviewRepository.findByFilters(status, fromDate, toDate, company);
        }
        
        if (sort != null && !sort.isEmpty()) {
            interviews = sortInterviews(interviews, sort, order);
        }
        
        if (interviews.isEmpty()) {
            return Collections.emptyList();
        } else {
            return interviews.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }        
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewDTO getInterviewById(UUID id) {
        log.debug("Fetching interview with id: {}", id);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + id));
        return convertToDTO(interview);
    }
    
    @Override
    @Transactional(readOnly = true)
    public InterviewDTO getInterviewDTOById(UUID id) {
        log.debug("Fetching interview DTO with id: {}", id);
        return getInterviewById(id);
    }
    
    @Override
    @Transactional
    public InterviewDTO createInterviewFromDTO(InterviewDTO interviewDTO) {
        log.debug("Creating new interview from DTO for company: {}", interviewDTO.getCompanyName());
        Interview interview = convertToEntity(interviewDTO);
        Interview savedInterview = interviewRepository.save(interview);
        return convertToDTO(savedInterview);
    }
    
    @Override
    @Transactional
    public InterviewDTO updateInterviewFromDTO(UUID id, InterviewDTO interviewDTO) {
        log.debug("Updating interview from DTO with id: {}", id);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + id));
        
        interviewDTO.setId(id);
        
        updateInterviewFields(interview, convertToEntity(interviewDTO));
        
        Interview updatedInterview = interviewRepository.save(interview);
        return convertToDTO(updatedInterview);
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
    
    @Override
    @Transactional
    public InterviewDTO updateInterviewStatus(UUID id, InterviewStatus status) {
        log.debug("Updating status of interview with id: {} to {}", id, status);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + id));
        interview.setStatus(status);
        Interview updatedInterview = interviewRepository.save(interview);
        return convertToDTO(updatedInterview);
    }
    
    @Override
    @Transactional
    public void deleteInterview(UUID id) {
        log.debug("Deleting interview with id: {}", id);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + id));
        interviewRepository.delete(interview);
    }
    
    @Override
    @Transactional(readOnly = true)
    public InterviewStatisticsDTO getInterviewStatistics(LocalDate fromDate, LocalDate toDate) {
        log.debug("Generating interview statistics for period: {} to {}", fromDate, toDate);
        
        List<Interview> interviews;
        if (fromDate != null && toDate != null) {
            interviews = interviewRepository.findByDateBetween(fromDate, toDate);
        } else {
            interviews = interviewRepository.findAll();
        }
        
        return generateStatistics(interviews);
    }
    
    @Override
    @Transactional(readOnly = true)
    public InterviewStatisticsDTO getInterviewStatistics(LocalDate fromDate, LocalDate toDate, UUID userId) {
        log.debug("Generating interview statistics for period: {} to {} and userId: {}", fromDate, toDate, userId);
        
        List<Interview> interviews;
        if (userId != null) {
            if (fromDate != null && toDate != null) {
                interviews = interviewRepository.findByDateBetweenAndUserId(fromDate, toDate, userId);
            } else {
                interviews = interviewRepository.findByUserId(userId);
            }
        } else if (fromDate != null && toDate != null) {
            interviews = interviewRepository.findByDateBetween(fromDate, toDate);
        } else {
            interviews = interviewRepository.findAll();
        }
        
        return generateStatistics(interviews);
    }
    
    private InterviewStatisticsDTO generateStatistics(List<Interview> interviews) {
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

    private InterviewDTO convertToDTO(Interview interview) {
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
                .userId(interview.getUserId())
                .build();
    }

    private Interview convertToEntity(InterviewDTO dto) {
        Interview interview = Interview.builder()
                .id(dto.getId())
                .companyName(dto.getCompanyName())
                .position(dto.getPosition())
                .date(dto.getDate())
                .time(dto.getTime())
                .type(dto.getType())
                .status(dto.getStatus())
                .location(dto.getLocation())
                .notes(dto.getNotes())
                .contactPerson(dto.getContactPerson())
                .rating(dto.getRating())
                .feedback(dto.getFeedback())
                .userId(dto.getUserId())
                .build();

        if (dto.getQuestions() != null) {
            dto.getQuestions().forEach(interview::addQuestion);
        }

        if (dto.getDocuments() != null) {
            dto.getDocuments().forEach(interview::addDocument);
        }

        return interview;
    }
}
