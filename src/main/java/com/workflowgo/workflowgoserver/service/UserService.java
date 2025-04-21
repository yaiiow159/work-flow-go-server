package com.workflowgo.workflowgoserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.workflowgo.workflowgoserver.dto.UserInfoDTO;
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

    public User updateUserSettings(Long userId, UserInfoDTO userInfoDTO) {
        User user = getUserById(userId);

        if (userInfoDTO.getName() != null && !userInfoDTO.getName().isEmpty()) {
            user.setName(userInfoDTO.getName());
        }

        if (userInfoDTO.getEmail() != null && !userInfoDTO.getEmail().isEmpty()) {
            user.setEmail(userInfoDTO.getEmail());
        }

        if (userInfoDTO.getBio() != null) {
            user.setBio(userInfoDTO.getBio());
        }

        if (userInfoDTO.getPhone() != null) {
            user.setPhone(userInfoDTO.getPhone());
        }

        if (userInfoDTO.getLocation() != null) {
            user.setLocation(userInfoDTO.getLocation());
        }

        if (userInfoDTO.getCompany() != null) {
            user.setCompany(userInfoDTO.getCompany());
        }

        if (userInfoDTO.getPosition() != null) {
            user.setPosition(userInfoDTO.getPosition());
        }

        if (userInfoDTO.getPhotoURL() != null) {
            user.setPhotoURL(userInfoDTO.getPhotoURL());
        }

        if (userInfoDTO.getPreferences() != null) {
            if (userInfoDTO.getPreferences().getTheme() != null) {
                user.getPreferences().setDarkMode(
                        userInfoDTO.getPreferences().getTheme().isDarkMode());

                if (userInfoDTO.getPreferences().getTheme().getPrimaryColor() != null) {
                    user.getPreferences().setPrimaryColor(
                            userInfoDTO.getPreferences().getTheme().getPrimaryColor());
                }
            }

            if (userInfoDTO.getPreferences().getNotifications() != null) {
                user.getPreferences().setEmailNotifications(
                        userInfoDTO.getPreferences().getNotifications().isEnabled());

                user.getPreferences().setEmailNotifications(
                        userInfoDTO.getPreferences().getNotifications().isEmailNotifications());

                if (userInfoDTO.getPreferences().getNotifications().getReminderTime() != null) {
                    user.getPreferences().setReminderTime(
                            userInfoDTO.getPreferences().getNotifications().getReminderTime());
                }
            }

            if (userInfoDTO.getPreferences().getDisplay() != null) {
                if (userInfoDTO.getPreferences().getDisplay().getDefaultView() != null) {
                    user.getPreferences().setDefaultView(
                            userInfoDTO.getPreferences().getDisplay().getDefaultView());
                }

                user.getPreferences().setCompactMode(
                        userInfoDTO.getPreferences().getDisplay().isCompactMode());
            }
        }

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
