package com.workflowgo.workflowgoserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflowgo.workflowgoserver.dto.UserDTO;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    
    private final UserService userService;
    private final ObjectMapper objectMapper;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }
    
    @GetMapping("/current")
    @Operation(summary = "Get current user", description = "Retrieve the current authenticated user")
    public ResponseEntity<UserDTO> getCurrentUser() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update a user's profile information")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            @RequestBody UserDTO userDTO) {
        User updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
    }
    
    @PutMapping("/{id}/preferences")
    @Operation(summary = "Update user preferences", description = "Update a user's preferences")
    public ResponseEntity<UserDTO> updateUserPreferences(
            @PathVariable UUID id,
            @RequestBody User.Preferences preferences) {
        User updatedUser = userService.updateUserPreferences(id, preferences);
        return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
    }
    
    @PostMapping("/{id}/preferences/reset")
    @Operation(summary = "Reset user preferences", description = "Reset user preferences to default values")
    public ResponseEntity<UserDTO> resetUserPreferences(@PathVariable UUID id) {
        User user = userService.resetUserPreferences(id);
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }
    
    @GetMapping("/{id}/export")
    @Operation(summary = "Export user data", description = "Export all user data (interviews, documents, settings)")
    public void exportUserData(@PathVariable UUID id, HttpServletResponse response) throws IOException {
        Map<String, Object> userData = userService.exportUserData(id);
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=workflowgo_data.json");
        
        objectMapper.writeValue(response.getOutputStream(), userData);
    }
}
