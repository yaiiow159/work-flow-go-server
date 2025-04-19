package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.UserDTO;
import com.workflowgo.workflowgoserver.model.User;

import java.util.Map;
import java.util.UUID;

public interface UserService {
    
    User getUserById(UUID id);
    
    User getCurrentUser();
    
    User updateUser(UUID id, UserDTO userDTO);
    
    User updateUserPreferences(UUID id, User.Preferences preferences);
    
    User resetUserPreferences(UUID id);
    
    Map<String, Object> exportUserData(UUID id);
}
