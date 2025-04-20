package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.AuthDTO;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.model.enums.AuthProvider;
import com.workflowgo.workflowgoserver.payload.*;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import com.workflowgo.workflowgoserver.security.TokenProvider;
import com.workflowgo.workflowgoserver.security.UserPrincipal;
import com.workflowgo.workflowgoserver.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final VerificationService verificationService;

    public AuthController(AuthenticationManager authenticationManager, 
                         UserRepository userRepository, 
                         PasswordEncoder passwordEncoder, 
                         TokenProvider tokenProvider,
                         VerificationService verificationService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.verificationService = verificationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
            String token = tokenProvider.createToken(authentication);
            
            AuthDTO authDTO = AuthDTO.fromUser(user, token);
            return ResponseEntity.ok(new AuthResponse(authDTO, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, e.getMessage() != null ? e.getMessage() : "Login failed"));
        }
    }

    @PostMapping("/verify-email/request")
    public ResponseEntity<?> requestEmailVerification(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email address already in use."));
            }
            
            verificationService.requestEmailVerification(request.getEmail());
            
            return ResponseEntity.ok(new ApiResponse(true, "Verification code sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Failed to send verification code: " + e.getMessage()));
        }
    }
    
    @PostMapping("/verify-email/verify")
    public ResponseEntity<?> verifyEmailCode(@Valid @RequestBody EmailVerificationCodeRequest request) {
        try {
            boolean isValid = verificationService.verifyEmailCode(request.getEmail(), request.getCode());
            
            if (isValid) {
                return ResponseEntity.ok(new ApiResponse(true, "Email verification successful"));
            } else {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid verification code"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Failed to verify code: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Email address already in use."));
        }

        boolean isCodeValid = verificationService.verifyEmailCode(
            signUpRequest.getEmail(), signUpRequest.getVerificationCode());
            
        if (!isCodeValid) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Invalid verification code"));
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setProvider(AuthProvider.local);
        user.setProviderId(UUID.randomUUID().toString());
        user.setEmailVerified(true); 
        user = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                signUpRequest.getEmail(),
                signUpRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);
        
        AuthDTO authDTO = AuthDTO.fromUser(user, token);
        return ResponseEntity.ok(new RegisterResponse(true, "User registered successfully", authDTO, token));
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        return ResponseEntity.ok().build();
    }
}
