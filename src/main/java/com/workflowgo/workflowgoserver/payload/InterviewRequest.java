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
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @NotBlank(message = "Position is required")
    private String position;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Time is required")
    private LocalTime time;
    
    @NotNull(message = "Type is required")
    private InterviewType type;
    
    @NotNull(message = "Status is required")
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
