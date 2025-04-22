package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.User;
import lombok.Data;

@Data
public class AuthDTO {
    private String id;
    private String email;
    private String displayName;
    private String photoURL;
    private String authProvider;
    private String token;
    private UserPreferencesDTO preferences;
    
    public static AuthDTO fromUser(User user, String token) {
        if (user == null) {
            return null;
        }
        
        AuthDTO dto = new AuthDTO();
        dto.setId(user.getId().toString());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getName());
        dto.setPhotoURL(user.getPhotoURL());
        dto.setAuthProvider(user.getProvider().toString());
        dto.setToken(token);

        if (user.getPreferences() != null) {
            dto.setPreferences(UserPreferencesDTO.fromUserPreferences(user.getPreferences()));
        }
        return dto;
    }
}
