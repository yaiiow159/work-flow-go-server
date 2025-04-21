package com.workflowgo.workflowgoserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.model.UserPreferences;
import com.workflowgo.workflowgoserver.payload.UserSettingsRequest;
import com.workflowgo.workflowgoserver.repository.DocumentRepository;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository,
                      InterviewRepository interviewRepository,
                      DocumentRepository documentRepository,
                      ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
        this.documentRepository = documentRepository;
        this.objectMapper = objectMapper;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    public User updateUserSettings(Long userId, UserSettingsRequest settingsRequest) {
        User user = getUserById(userId);
        
        if (settingsRequest.getDisplayName() != null && !settingsRequest.getDisplayName().isEmpty()) {
            user.setName(settingsRequest.getDisplayName());
        }
        
        user.setBio(settingsRequest.getBio());
        user.setPhone(settingsRequest.getPhone());
        user.setLocation(settingsRequest.getLocation());
        user.setCompany(settingsRequest.getCompany());
        user.setPosition(settingsRequest.getPosition());

        UserPreferences preferences = user.getPreferences();
        if (preferences == null) {
            preferences = new UserPreferences();
            user.setPreferences(preferences);
        }

        preferences.setDarkMode(settingsRequest.isDarkMode());
        preferences.setPrimaryColor(settingsRequest.getPrimaryColor());
        preferences.setEmailNotifications(settingsRequest.isEmailNotifications());
        preferences.setReminderTime(settingsRequest.getReminderTime());
        preferences.setDefaultView(settingsRequest.getDefaultView());
        preferences.setCompactMode(settingsRequest.isCompactMode());

        return userRepository.save(user);
    }

    public byte[] exportUserData(Long userId) {
        try {
            User user = getUserById(userId);
            List<Interview> interviews = interviewRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "createdAt"));
            List<Document> documents = documentRepository.findByUserId(userId);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("user", user);
            userData.put("interviews", interviews);
            userData.put("documents", documents);
            
            return objectMapper.writeValueAsBytes(userData);
        } catch (IOException ex) {
            throw new RuntimeException("Error exporting user data", ex);
        }
    }

    public User resetUserSettings(Long userId) {
        User user = getUserById(userId);
        user.setPreferences(new UserPreferences());
        return userRepository.save(user);
    }
}
