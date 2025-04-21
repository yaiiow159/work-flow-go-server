package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.UserProfileDTO;
import com.workflowgo.workflowgoserver.dto.UserProfileRequest;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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

    public UserProfileService(UserRepository userRepository, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public UserProfileDTO getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return convertToUserProfileDTO(user);
    }

    @Transactional
    public UserProfileDTO updateUserProfile(Long userId, UserProfileRequest userProfileRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setName(Optional.ofNullable(userProfileRequest.getName()).orElse(user.getName()));
        user.setBio(userProfileRequest.getBio());
        user.setPhone(userProfileRequest.getPhone());
        user.setLocation(userProfileRequest.getLocation());
        user.setCompany(userProfileRequest.getCompany());
        user.setPosition(userProfileRequest.getPosition());

        user = userRepository.save(user);

        return convertToUserProfileDTO(user);
    }
    
    @Transactional
    public String updateProfileImage(Long userId, MultipartFile imageFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getPhotoURL() != null && !user.getPhotoURL().isEmpty()) {
            try {
                cloudinaryService.deleteFile(user.getPhotoURL(), imageFile.getContentType());
            } catch (Exception e) {
                log.error("Failed to delete old profile image: {}", e.getMessage());
            }
        }

        String imageUrl = cloudinaryService.uploadFile(imageFile);

        user.setPhotoURL(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    private UserProfileDTO convertToUserProfileDTO(User user) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(user.getId());
        userProfileDTO.setName(user.getName());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setBio(user.getBio());
        userProfileDTO.setPhone(user.getPhone());
        userProfileDTO.setLocation(user.getLocation());
        userProfileDTO.setCompany(user.getCompany());
        userProfileDTO.setPosition(user.getPosition());
        userProfileDTO.setPhotoURL(user.getPhotoURL());
        return userProfileDTO;
    }

}
