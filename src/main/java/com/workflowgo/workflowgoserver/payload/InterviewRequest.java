package com.workflowgo.workflowgoserver.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InterviewRequest {

    @JsonIgnore
    private Long id;

    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @NotBlank(message = "Position is required")
    private String position;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Time is required")
    private LocalTime time;
    
    @NotNull(message = "Type is required")
    private String type;
    
    @NotNull(message = "Status is required")
    private String status;
    
    private String location;
    
    private String notes;
    
    private String contactName;
    
    private String contactPosition;
    
    private String contactEmail;
    
    private String contactPhone;
    
    private Integer rating;
    
    private String feedback;
}
