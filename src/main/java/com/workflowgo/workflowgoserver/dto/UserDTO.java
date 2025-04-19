package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String imageUrl;
    private Boolean emailVerified;
    private UserPreferencesDTO preferences;
    
    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setImageUrl(user.getImageUrl());
        userDTO.setEmailVerified(user.getEmailVerified());
        userDTO.setPreferences(UserPreferencesDTO.fromUserPreferences(user.getPreferences()));
        return userDTO;
    }
}
