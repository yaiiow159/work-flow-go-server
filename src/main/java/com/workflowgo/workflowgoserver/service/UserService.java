package com.workflowgo.workflowgoserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final InterviewService interviewService;
    private final DocumentService documentService;
    private final ObjectMapper objectMapper;
    
    private static final String DEFAULT_USER_EMAIL = "default@workflowgo.com";
    
    @Transactional(readOnly = true)
    public User getUserSettings() {
        return getOrCreateDefaultUser();
    }
    
    @Transactional
    public User updateUserSettings(User userDetails) {
        User user = getOrCreateDefaultUser();
        
        if (userDetails.getName() != null) {
            user.setName(userDetails.getName());
        }
        
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        
        if (userDetails.getThemePreferences() != null) {
            user.setThemePreferences(userDetails.getThemePreferences());
        }
        
        if (userDetails.getNotificationPreferences() != null) {
            user.setNotificationPreferences(userDetails.getNotificationPreferences());
        }
        
        if (userDetails.getDisplayPreferences() != null) {
            user.setDisplayPreferences(userDetails.getDisplayPreferences());
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User resetUserSettings() {
        User user = getOrCreateDefaultUser();
        
        // Reset to default settings
        user.setThemePreferences(User.ThemePreferences.builder()
                .darkMode(false)
                .primaryColor("#6366F1")
                .build());
        
        user.setNotificationPreferences(User.NotificationPreferences.builder()
                .enabled(true)
                .emailNotifications(false)
                .reminderTime("1day")
                .build());
        
        user.setDisplayPreferences(User.DisplayPreferences.builder()
                .defaultView(User.DisplayPreferences.DefaultView.CALENDAR)
                .compactMode(false)
                .build());
        
        return userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> exportUserData() {
        User user = getOrCreateDefaultUser();
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("user", user);
        userData.put("interviews", interviewService.getAllInterviews(null, null, null, null, null, null));
        userData.put("documents", documentService.getAllDocuments());
        
        return userData;
    }
    
    private User getOrCreateDefaultUser() {
        Optional<User> userOptional = userRepository.findByEmail(DEFAULT_USER_EMAIL);
        
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            User defaultUser = User.builder()
                    .name("Default User")
                    .email(DEFAULT_USER_EMAIL)
                    .themePreferences(User.ThemePreferences.builder()
                            .darkMode(false)
                            .primaryColor("#6366F1")
                            .build())
                    .notificationPreferences(User.NotificationPreferences.builder()
                            .enabled(true)
                            .emailNotifications(false)
                            .reminderTime("1day")
                            .build())
                    .displayPreferences(User.DisplayPreferences.builder()
                            .defaultView(User.DisplayPreferences.DefaultView.CALENDAR)
                            .compactMode(false)
                            .build())
                    .build();
            
            return userRepository.save(defaultUser);
        }
    }
}
