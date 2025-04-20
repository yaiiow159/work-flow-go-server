package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.User;
import lombok.Data;

@Data
public class UserProfileDTO {
    private String id;
    private String name;
    private String email;
    private String bio;
    private String phone;
    private String location;
    private String company;
    private String position;
    private String photoURL;
    private UserPreferencesProfileDTO preferences;
    
    public static UserProfileDTO fromUser(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId().toString());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setBio(user.getBio() != null ? user.getBio() : "");
        dto.setPhone(user.getPhone() != null ? user.getPhone() : "");
        dto.setLocation(user.getLocation() != null ? user.getLocation() : "");
        dto.setCompany(user.getCompany() != null ? user.getCompany() : "");
        dto.setPosition(user.getPosition() != null ? user.getPosition() : "");
        dto.setPhotoURL(user.getPhotoURL());
        dto.setPreferences(UserPreferencesProfileDTO.fromUserPreferences(user.getPreferences()));
        return dto;
    }
}
