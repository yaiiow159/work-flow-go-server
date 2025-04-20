package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String photoURL;
    private Boolean emailVerified;
    private String bio;
    private String phone;
    private String location;
    private String company;
    private String position;
    private UserPreferencesDTO preferences;
    
    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhotoURL(user.getPhotoURL());
        userDTO.setEmailVerified(user.getEmailVerified());
        userDTO.setBio(user.getBio());
        userDTO.setPhone(user.getPhone());
        userDTO.setLocation(user.getLocation());
        userDTO.setCompany(user.getCompany());
        userDTO.setPosition(user.getPosition());
        userDTO.setPreferences(UserPreferencesDTO.fromUserPreferences(user.getPreferences()));
        return userDTO;
    }
}
