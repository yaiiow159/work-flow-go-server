package com.workflowgo.workflowgoserver.payload;

import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import com.workflowgo.workflowgoserver.model.enums.InterviewType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class InterviewRequest {
    @NotBlank
    private String companyName;
    
    @NotBlank
    private String position;
    
    @NotNull
    private LocalDate date;
    
    @NotNull
    private LocalTime time;
    
    @NotNull
    private InterviewType type;
    
    @NotNull
    private InterviewStatus status;
    
    private String location;
    
    private String notes;
    
    private String contactName;
    
    private String contactPosition;
    
    private String contactEmail;
    
    private String contactPhone;
    
    private Integer rating;
    
    private String feedback;
}
