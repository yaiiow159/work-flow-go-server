package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.UserInfoDTO;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.payload.UserSettingsRequest;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    public UserProfileService(UserRepository userRepository, CloudinaryService cloudinaryService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserInfoDTO getCurrentUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return convertToUserInfoDTO(user);
    }

    @Transactional
    public UserInfoDTO updateUserProfile(Long userId, UserSettingsRequest userSettingsRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setName(Optional.ofNullable(userSettingsRequest.getDisplayName()).orElse(user.getName()));
        user.setBio(userSettingsRequest.getBio());
        user.setPhone(userSettingsRequest.getPhone());
        user.setLocation(userSettingsRequest.getLocation());
        user.setCompany(userSettingsRequest.getCompany());
        user.setPosition(userSettingsRequest.getPosition());

        user = userRepository.save(user);

        return convertToUserInfoDTO(user);
    }

    @Transactional
    public String updateProfileImage(Long userId, MultipartFile imageFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getPhotoURL() != null && !user.getPhotoURL().isEmpty()) {
            try {
                String publicId = extractPublicIdFromUrl(user.getPhotoURL());
                if (publicId != null) {
                    cloudinaryService.deleteFile(publicId);
                }
            } catch (Exception e) {
                System.err.println("Failed to delete old profile image: " + e.getMessage());
            }
        }

        String imageUrl = cloudinaryService.uploadFile(imageFile);

        user.setPhotoURL(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    private String extractPublicIdFromUrl(String url) {
        try {
            return getUrlString(url);
        } catch (Exception e) {
            log.error("Error extracting public ID from URL: {}", url, e);
            return null;
        }
    }

    static String getUrlString(String url) {
        if (url != null && url.contains("/upload/")) {
            String[] parts = url.split("/upload/");
            if (parts.length > 1) {
                String afterVersion = parts[1].replaceFirst("v\\d+/", "");
                return afterVersion.replaceFirst("\\.[^.]+$", "");
            }
        }
        return null;
    }

    private UserInfoDTO convertToUserInfoDTO(User user) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(user.getId());
        userInfoDTO.setName(user.getName());
        userInfoDTO.setEmail(user.getEmail());
        userInfoDTO.setBio(user.getBio());
        userInfoDTO.setPhone(user.getPhone());
        userInfoDTO.setLocation(user.getLocation());
        userInfoDTO.setCompany(user.getCompany());
        userInfoDTO.setPosition(user.getPosition());
        userInfoDTO.setPhotoURL(user.getPhotoURL());
        
        UserInfoDTO.Preferences preferences = new UserInfoDTO.Preferences();
        
        UserInfoDTO.ThemePreferences themePreferences = new UserInfoDTO.ThemePreferences();
        themePreferences.setDarkMode(user.getPreferences().isDarkMode());
        themePreferences.setPrimaryColor(user.getPreferences().getPrimaryColor());
        preferences.setTheme(themePreferences);

        UserInfoDTO.NotificationPreferences notificationPreferences = new UserInfoDTO.NotificationPreferences();
        notificationPreferences.setEnabled(true);
        notificationPreferences.setEmailNotifications(user.getPreferences().isEmailNotifications());
        notificationPreferences.setReminderTime(user.getPreferences().getReminderTime());
        preferences.setNotifications(notificationPreferences);
        
        UserInfoDTO.DisplayPreferences displayPreferences = new UserInfoDTO.DisplayPreferences();
        displayPreferences.setDefaultView(user.getPreferences().getDefaultView());
        displayPreferences.setCompactMode(user.getPreferences().isCompactMode());
        preferences.setDisplay(displayPreferences);
        
        userInfoDTO.setPreferences(preferences);
        
        return userInfoDTO;
    }
}
