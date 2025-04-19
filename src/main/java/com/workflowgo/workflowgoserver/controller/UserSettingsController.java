package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.UserSettingsDTO;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.payload.UserSettingsRequest;
import com.workflowgo.workflowgoserver.security.CurrentUser;
import com.workflowgo.workflowgoserver.security.UserPrincipal;
import com.workflowgo.workflowgoserver.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserSettingsController {

    private final UserService userService;

    public UserSettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/settings")
    @PreAuthorize("hasRole('USER')")
    public UserSettingsDTO getUserSettings(@CurrentUser UserPrincipal currentUser) {
        User user = userService.getUserById(currentUser.getId());
        return UserSettingsDTO.fromUser(user);
    }

    @PutMapping("/settings")
    @PreAuthorize("hasRole('USER')")
    public UserSettingsDTO updateUserSettings(
            @Valid @RequestBody UserSettingsRequest settingsRequest,
            @CurrentUser UserPrincipal currentUser) {
        
        User user = userService.updateUserSettings(currentUser.getId(), settingsRequest);
        return UserSettingsDTO.fromUser(user);
    }

    @PostMapping("/profile-image")
    @PreAuthorize("hasRole('USER')")
    public UserSettingsDTO uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @CurrentUser UserPrincipal currentUser) {
        
        User user = userService.updateProfileImage(currentUser.getId(), file);
        return UserSettingsDTO.fromUser(user);
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> exportUserData(@CurrentUser UserPrincipal currentUser) {
        byte[] data = userService.exportUserData(currentUser.getId());
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"user-data.json\"")
                .header("Content-Type", "application/json")
                .body(data);
    }

    @PostMapping("/settings/reset")
    @PreAuthorize("hasRole('USER')")
    public UserSettingsDTO resetUserSettings(@CurrentUser UserPrincipal currentUser) {
        User user = userService.resetUserSettings(currentUser.getId());
        return UserSettingsDTO.fromUser(user);
    }
}
