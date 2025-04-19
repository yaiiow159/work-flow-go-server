package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.AuthRequestDTO;
import com.workflowgo.workflowgoserver.dto.AuthResponseDTO;
import com.workflowgo.workflowgoserver.dto.RegisterRequestDTO;
import com.workflowgo.workflowgoserver.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user with email, password, and display name")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.debug("Registration request received for email: {}", request.getEmail());
        AuthResponseDTO response = authService.register(request);
        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password", description = "Authenticate user with email and password")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        log.debug("Login request received for email: {}", request.getEmail());
        AuthResponseDTO response = authService.login(request);
        log.info("User logged in successfully: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate a JWT token")
    public ResponseEntity<String> validateToken() {
        log.debug("Token validation request received");
        boolean isValid = authService.validateToken();

        if (!isValid) {
            return ResponseEntity.status(401).body("Token is invalid");
        }

        return ResponseEntity.ok("Token is valid");
    }
    
    @GetMapping("/success")
    @Operation(summary = "OAuth2 success redirect", description = "Endpoint for OAuth2 success redirect with token")
    public ResponseEntity<String> oauthSuccess(@RequestParam(required = false) String token) {
        if (token != null && !token.isEmpty()) {
            log.debug("OAuth2 success with token provided");
            return ResponseEntity.ok("Authentication successful. You can close this window and return to the application.");
        } else {
            log.debug("OAuth2 success without token");
            return ResponseEntity.ok("Authentication successful. You can close this window.");
        }
    }
}
