package com.workflowgo.workflowgoserver.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ContactPerson {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String position;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String phone;
}
