package com.workflowgo.workflowgoserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.workflowgo.workflowgoserver.model.User;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSettingsDTO {
    private Long id;
    private String name;
    private String email;
    private String photoURL;
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
        dto.setPhotoURL(user.getPhotoURL());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setPreferences(UserPreferencesDTO.fromUserPreferences(user.getPreferences()));
        return dto;
    }
}
