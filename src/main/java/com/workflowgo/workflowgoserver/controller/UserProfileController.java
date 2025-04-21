package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.*;
import com.workflowgo.workflowgoserver.payload.PasswordChangeRequest;
import com.workflowgo.workflowgoserver.security.CurrentUser;
import com.workflowgo.workflowgoserver.security.UserPrincipal;
import com.workflowgo.workflowgoserver.service.AuthService;
import com.workflowgo.workflowgoserver.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users/profile")
public class UserProfileController {

    private final AuthService authService;
    private final UserProfileService userProfileService;

    public UserProfileController( AuthService authService, UserProfileService userProfileService) {
        this.authService = authService;
        this.userProfileService = userProfileService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile(@CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userPrincipal.getId()));
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody UserProfileRequest userProfileRequest) {
        UserProfileDTO userProfileDTO = userProfileService.updateUserProfile(userPrincipal.getId(), userProfileRequest);
        return ResponseEntity.ok(userProfileDTO);
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
