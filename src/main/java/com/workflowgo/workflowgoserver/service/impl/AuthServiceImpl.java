package com.workflowgo.workflowgoserver.service.impl;

import com.workflowgo.workflowgoserver.dto.AuthRequestDTO;
import com.workflowgo.workflowgoserver.dto.AuthResponseDTO;
import com.workflowgo.workflowgoserver.dto.RegisterRequestDTO;
import com.workflowgo.workflowgoserver.exception.BadRequestException;
import com.workflowgo.workflowgoserver.exception.UnauthorizedException;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import com.workflowgo.workflowgoserver.security.JwtTokenUtil;
import com.workflowgo.workflowgoserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.debug("Registering new user with email: {}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email is already registered");
        }

        User user = User.builder()
                .name(request.getDisplayName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        
        initializeUserDefaults(user);
        
        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());
        
        String token = jwtTokenUtil.generateToken(savedUser);
        
        return createAuthResponse(savedUser, token, "password");
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {
        log.debug("Authenticating user with email: {}", request.getEmail());
        
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
            
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                log.warn("Password login attempt for OAuth-only user: {}", request.getEmail());
                throw new UnauthorizedException("This account can only be accessed via Google login");
            }
            
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Failed login attempt for user: {}", request.getEmail());
                throw new BadCredentialsException("Invalid credentials");
            }
            
            log.info("User logged in successfully: {}", user.getEmail());
            
            String token = jwtTokenUtil.generateToken(user);
            
            return createAuthResponse(user, token, "password");
        } catch (BadCredentialsException e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    @Override
    public boolean validateToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
    
    private void initializeUserDefaults(User user) {
        User.Theme defaultTheme = User.Theme.builder()
                .darkMode(false)
                .primaryColor("#6366F1")
                .build();
        
        User.Notifications defaultNotifications = User.Notifications.builder()
                .enabled(true)
                .emailNotifications(false)
                .reminderTime("1day")
                .build();
        
        User.Display defaultDisplay = User.Display.builder()
                .defaultView(User.Display.DefaultView.CALENDAR)
                .compactMode(false)
                .build();
        
        User.Preferences defaultPreferences = User.Preferences.builder()
                .theme(defaultTheme)
                .notifications(defaultNotifications)
                .display(defaultDisplay)
                .build();
        
        user.setPreferences(defaultPreferences);
    }
    
    private AuthResponseDTO createAuthResponse(User user, String token, String authProvider) {
        return AuthResponseDTO.builder()
                .user(AuthResponseDTO.UserInfoDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .displayName(user.getName())
                        .photoURL(user.getPhotoUrl())
                        .authProvider(authProvider)
                        .build())
                .token(token)
                .build();
    }
}
