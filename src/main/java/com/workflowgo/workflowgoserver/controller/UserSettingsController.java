package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.UserInfoDTO;
import com.workflowgo.workflowgoserver.dto.UserSettingsDTO;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.payload.UserSettingsRequest;
import com.workflowgo.workflowgoserver.security.CurrentUser;
import com.workflowgo.workflowgoserver.security.UserPrincipal;
import com.workflowgo.workflowgoserver.service.UserService;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/user")
public class UserSettingsController {

    private final UserService userService;

    public UserSettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/settings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserSettingsDTO> getUserSettings(@CurrentUser UserPrincipal currentUser) {
        User user = userService.getUserById(currentUser.getId());
        return ResponseEntity.ok(UserSettingsDTO.fromUser(user));
    }

    @PutMapping("/settings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserSettingsDTO> updateUserSettings(
            @Valid @RequestBody UserInfoDTO userInfoRequest,
            @CurrentUser UserPrincipal currentUser) {
        
        User user = userService.updateUserSettings(currentUser.getId(), userInfoRequest);
        return ResponseEntity.ok(UserSettingsDTO.fromUser(user));
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ByteArrayResource> exportUserDataZip(@CurrentUser UserPrincipal currentUser) {
        byte[] zipBytes = userService.exportUserData(currentUser.getId());
        ByteArrayResource resource = new ByteArrayResource(zipBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"user-data.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(zipBytes.length)
                .body(resource);
    }

    @PostMapping("/settings/reset")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserSettingsDTO> resetUserSettings(@CurrentUser UserPrincipal currentUser) {
        User user = userService.resetUserSettings(currentUser.getId());
        return ResponseEntity.ok(UserSettingsDTO.fromUser(user));
    }
}
