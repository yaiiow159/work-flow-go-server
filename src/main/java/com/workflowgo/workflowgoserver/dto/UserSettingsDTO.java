package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.User;
import lombok.Data;

@Data
public class UserSettingsDTO {
    private Long id;
    private String name;
    private String email;
    private String imageUrl;
    private Boolean emailVerified;
    private UserPreferencesDTO preferences;
    
    public static UserSettingsDTO fromUser(User user) {
        if (user == null) {
            return null;
        }
        
        UserSettingsDTO dto = new UserSettingsDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setImageUrl(user.getImageUrl());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setPreferences(UserPreferencesDTO.fromUserPreferences(user.getPreferences()));
        return dto;
    }
}
