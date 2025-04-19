package com.workflowgo.workflowgoserver.security;

import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    
    @Value("${app.oauth2.redirectUri:http://localhost:3000/auth/google/callback}")
    private String redirectUri;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) 
            throws IOException, ServletException {
        
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oAuth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            
            try {
                String email = oAuth2User.getAttribute("email");
                if (email == null) {
                    log.error("Email not found in OAuth2 attributes");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided by OAuth provider");
                    return;
                }
                
                String name = oAuth2User.getAttribute("name");
                String googleId = oAuth2User.getAttribute("sub");
                String picture = oAuth2User.getAttribute("picture");
                
                log.debug("OAuth2 login successful for email: {}", email);
                
                User user = findOrCreateUser(email, name, googleId, picture);
                
                String token = jwtTokenUtil.generateToken(user);
                
                String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                        .queryParam("token", token)
                        .build().toUriString();
                
                log.debug("Redirecting to: {}", targetUrl);
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
            } catch (Exception e) {
                log.error("Error during OAuth2 authentication success handling", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication processing failed");
            }
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
    
    private User findOrCreateUser(String email, String name, String googleId, String picture) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            boolean updated = false;
            
            if (googleId != null && (user.getGoogleId() == null || !user.getGoogleId().equals(googleId))) {
                user.setGoogleId(googleId);
                updated = true;
            }
            
            if (picture != null && (user.getPhotoUrl() == null || !user.getPhotoUrl().equals(picture))) {
                user.setPhotoUrl(picture);
                updated = true;
            }
            
            if (updated) {
                log.debug("Updating existing user with OAuth2 information: {}", email);
                return userRepository.save(user);
            }
            
            return user;
        }
        
        log.debug("Creating new user from OAuth2 login: {}", email);
        User newUser = User.builder()
                .name(name)
                .email(email)
                .googleId(googleId)
                .photoUrl(picture)
                .build();
        
        return userRepository.save(newUser);
    }
}
