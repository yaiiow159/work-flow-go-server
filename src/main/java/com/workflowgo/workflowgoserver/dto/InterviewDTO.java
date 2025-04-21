package com.workflowgo.workflowgoserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import com.workflowgo.workflowgoserver.model.enums.InterviewType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InterviewDTO {
    private Long id;
    private String companyName;
    private String position;
    private LocalDate date;
    private LocalTime time;
    private InterviewType type;
    private InterviewStatus status;
    private String location;
    private String notes;
    private ContactPersonDTO contactPerson;
    private Integer rating;
    private String feedback;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<QuestionDTO> questions;
    private List<Long> documentIds;
    
    public static InterviewDTO fromInterview(Interview interview) {
        InterviewDTO dto = new InterviewDTO();
        dto.setId(interview.getId());
        dto.setCompanyName(interview.getCompanyName());
        dto.setPosition(interview.getPosition());
        dto.setDate(interview.getDate());
        dto.setTime(interview.getTime());
        dto.setType(interview.getType());
        dto.setStatus(interview.getStatus());
        dto.setLocation(interview.getLocation());
        dto.setNotes(interview.getNotes());
        
        if (interview.getContactPerson() != null) {
            dto.setContactPerson(ContactPersonDTO.fromContactPerson(interview.getContactPerson()));
        }
        
        dto.setRating(interview.getRating());
        dto.setFeedback(interview.getFeedback());
        dto.setCreatedAt(interview.getCreatedAt());
        dto.setUpdatedAt(interview.getUpdatedAt());
        
        if (interview.getQuestions() != null) {
            dto.setQuestions(interview.getQuestions().stream()
                    .map(QuestionDTO::fromQuestion)
                    .collect(Collectors.toList()));
        }
        
        if (interview.getDocuments() != null) {
            dto.setDocumentIds(interview.getDocuments().stream()
                    .map(Document::getId)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public static List<InterviewDTO> fromInterviews(List<Interview> interviews) {
        return interviews.stream()
                .map(InterviewDTO::fromInterview)
                .collect(Collectors.toList());
    }
}
