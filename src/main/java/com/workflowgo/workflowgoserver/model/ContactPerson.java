package com.workflowgo.workflowgoserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ContactPerson {
    @Column(name = "contact_name")
    private String name;
    
    @Column(name = "contact_position")
    private String position;
    
    @Column(name = "contact_email")
    private String email;
    
    @Column(name = "contact_phone")
    private String phone;
}
