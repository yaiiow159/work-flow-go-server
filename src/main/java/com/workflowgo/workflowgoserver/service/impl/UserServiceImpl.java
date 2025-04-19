package com.workflowgo.workflowgoserver.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflowgo.workflowgoserver.dto.UserDTO;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import com.workflowgo.workflowgoserver.security.JwtTokenUtil;
import com.workflowgo.workflowgoserver.service.DocumentService;
import com.workflowgo.workflowgoserver.service.InterviewService;
import com.workflowgo.workflowgoserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final InterviewService interviewService;
    private final DocumentService documentService;
    
    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        log.debug("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        log.debug("Fetching current user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
    
    @Override
    @Transactional
    public User updateUser(UUID id, UserDTO userDTO) {
        log.debug("Updating user with id: {}", id);
        User user = getUserById(id);
        
        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User updateUserPreferences(UUID id, User.Preferences preferences) {
        log.debug("Updating preferences for user with id: {}", id);
        User user = getUserById(id);
        
        user.setPreferences(preferences);
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User resetUserPreferences(UUID id) {
        log.debug("Resetting preferences for user with id: {}", id);
        User user = getUserById(id);
        
        User.Theme defaultTheme = User.Theme.builder()
                .darkMode(false)
                .primaryColor("#6366F1")
                .build();
        
        User.Notifications defaultNotifications = User.Notifications.builder()
                .enabled(true)
                .emailNotifications(false)
                .reminderTime("1day")
                .build();
        
        User.Display defaultDisplay = User.Display.builder()
                .defaultView(User.Display.DefaultView.CALENDAR)
                .compactMode(false)
                .build();
        
        User.Preferences defaultPreferences = User.Preferences.builder()
                .theme(defaultTheme)
                .notifications(defaultNotifications)
                .display(defaultDisplay)
                .build();
        
        user.setPreferences(defaultPreferences);
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> exportUserData(UUID id) {
        log.debug("Exporting data for user with id: {}", id);
        User user = getUserById(id);
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("user", user);
        userData.put("interviews", interviewService.getAllInterviews(null, null, null, null, null, null, id));
        userData.put("documents", documentService.getDocumentsByUserId(id));
        
        return userData;
    }
}
