package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.ContactPerson;
import lombok.Data;

@Data
public class ContactPersonDTO {
    private String name;
    private String email;
    private String phone;
    private String position;
    
    public static ContactPersonDTO fromContactPerson(ContactPerson contactPerson) {
        if (contactPerson == null) {
            return null;
        }
        
        ContactPersonDTO dto = new ContactPersonDTO();
        dto.setName(contactPerson.getName());
        dto.setEmail(contactPerson.getEmail());
        dto.setPhone(contactPerson.getPhone());
        dto.setPosition(contactPerson.getPosition());
        return dto;
    }
}
