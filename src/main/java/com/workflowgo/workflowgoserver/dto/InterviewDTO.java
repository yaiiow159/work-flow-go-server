package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.ContactPerson;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.Question;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import com.workflowgo.workflowgoserver.model.enums.InterviewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewDTO {
    
    private UUID id;
    private String companyName;
    private String position;
    private LocalDate date;
    private LocalTime time;
    private InterviewType type;
    private InterviewStatus status;
    private String location;
    private String notes;
    private ContactPerson contactPerson;
    private List<Question> questions;
    private List<Document> documents;
    private Integer rating;
    private String feedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID userId;
}
