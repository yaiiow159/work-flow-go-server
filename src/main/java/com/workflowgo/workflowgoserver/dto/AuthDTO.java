package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.User;
import lombok.Data;

@Data
public class AuthDTO {
    private Long id;
    private String name;
    private String email;
    private String imageUrl;
    
    public static AuthDTO fromUser(User user) {
        if (user == null) {
            return null;
        }
        
        AuthDTO dto = new AuthDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setImageUrl(user.getImageUrl());
        return dto;
    }
}
