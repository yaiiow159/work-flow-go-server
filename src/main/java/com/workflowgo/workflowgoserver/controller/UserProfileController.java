package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.UserDTO;
import com.workflowgo.workflowgoserver.dto.UserInfoDTO;
import com.workflowgo.workflowgoserver.payload.PasswordChangeRequest;
import com.workflowgo.workflowgoserver.payload.UserSettingsRequest;
import com.workflowgo.workflowgoserver.security.CurrentUser;
import com.workflowgo.workflowgoserver.security.UserPrincipal;
import com.workflowgo.workflowgoserver.service.AuthService;
import com.workflowgo.workflowgoserver.service.UserProfileService;
import com.workflowgo.workflowgoserver.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users/profile")
public class UserProfileController {

    private final UserService userService;
    private final AuthService authService;
    private final UserProfileService userProfileService;

    public UserProfileController(UserService userService, AuthService authService, UserProfileService userProfileService) {
        this.userService = userService;
        this.authService = authService;
        this.userProfileService = userProfileService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(UserDTO.fromUser(userService.getUserById(userPrincipal.getId())));
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserInfoDTO> updateUserProfile(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody UserSettingsRequest userSettingsRequest) {
        UserInfoDTO userInfoDTO = userProfileService.updateUserProfile(userPrincipal.getId(), userSettingsRequest);
        return ResponseEntity.ok(userInfoDTO);
    }

    @PostMapping("/profile-image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @CurrentUser UserPrincipal currentUser) throws IOException {
        return ResponseEntity.ok(userProfileService.updateProfileImage(currentUser.getId(), file));
    }
    
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> changePassword(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        authService.changePassword(
            userPrincipal.getId(), 
            passwordChangeRequest.getCurrentPassword(), 
            passwordChangeRequest.getNewPassword(), 
            passwordChangeRequest.getConfirmPassword()
        );
        return ResponseEntity.ok("Password changed successfully");
    }
}
