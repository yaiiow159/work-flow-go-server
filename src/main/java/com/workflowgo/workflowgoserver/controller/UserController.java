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

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "User settings endpoints")
public class UserController {
    
    private final UserService userService;
    private final ObjectMapper objectMapper;
    
    @GetMapping("/settings")
    @Operation(summary = "Get user settings", description = "Retrieve the current user's settings")
    public ResponseEntity<UserDTO> getUserSettings() {
        User user = userService.getUserSettings();
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }
    
    @PutMapping("/settings")
    @Operation(summary = "Update user settings", description = "Update the current user's settings")
    public ResponseEntity<UserDTO> updateUserSettings(@RequestBody User userDetails) {
        User updatedUser = userService.updateUserSettings(userDetails);
        return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
    }
    
    @PostMapping("/settings/reset")
    @Operation(summary = "Reset user settings", description = "Reset user settings to default values")
    public ResponseEntity<UserDTO> resetUserSettings() {
        User user = userService.resetUserSettings();
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }
    
    @GetMapping("/export")
    @Operation(summary = "Export user data", description = "Export all user data (interviews, documents, settings)")
    public void exportUserData(HttpServletResponse response) throws IOException {
        Map<String, Object> userData = userService.exportUserData();
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=workflowgo_data.json");
        
        objectMapper.writeValue(response.getOutputStream(), userData);
    }
}
