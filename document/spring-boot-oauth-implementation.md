# Spring Boot Google OAuth2 Implementation Guide

This guide provides detailed instructions for implementing Google OAuth2 authentication in a Spring Boot application for the Work-Flow-Go project.

## 1. Project Setup

### Maven Dependencies

Add the following dependencies to your `pom.xml`:

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- JWT Dependencies -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok for reducing boilerplate code -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### Application Properties

Configure your `application.yml` file:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            redirect-uri: "{baseUrl}/oauth2/callback/google"
            scope:
              - email
              - profile
  
  datasource:
    url: jdbc:h2:mem:workflowdb
    username: sa
    password: password
    driver-class-name: org.h2.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# JWT Configuration
app:
  auth:
    tokenSecret: ${JWT_SECRET:bd04b7c89bfd96a0f14a997f58f2246c37883f3825dd1a3f3d636f9fb0d386a0}
    tokenExpirationMsec: 864000000 # 10 days
    authorizedRedirectUris: ${FRONTEND_URL:http://localhost:3000}/login/success
```

## 2. Domain Models

### User Entity

```java
package com.workflowgo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String imageUrl;
    
    @Column(nullable = false)
    private Boolean emailVerified = false;
    
    private String password;
    
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
    
    private String providerId;
    
    // User preferences
    @Embedded
    private UserPreferences preferences = new UserPreferences();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Interview> interviews = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Document> documents = new HashSet<>();
}
```

### UserPreferences Embedded Entity

```java
package com.workflowgo.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class UserPreferences {
    private boolean darkMode = false;
    private String primaryColor = "#4f46e5";
    private boolean emailNotifications = true;
    private String reminderTime = "1h";
    private String defaultView = "list";
    private boolean compactMode = false;
}
```

### AuthProvider Enum

```java
package com.workflowgo.model;

public enum AuthProvider {
    local,
    google
}
```

### Interview Entity

```java
package com.workflowgo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
public class Interview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String companyName;
    
    @Column(nullable = false)
    private String position;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private LocalTime time;
    
    @Enumerated(EnumType.STRING)
    private InterviewType type;
    
    @Enumerated(EnumType.STRING)
    private InterviewStatus status;
    
    private String location;
    
    @Column(length = 2000)
    private String notes;
    
    @Embedded
    private ContactPerson contactPerson;
    
    private Integer rating;
    
    @Column(length = 2000)
    private String feedback;
    
    @CreationTimestamp
    private ZonedDateTime createdAt;
    
    @UpdateTimestamp
    private ZonedDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "interview_documents",
        joinColumns = @JoinColumn(name = "interview_id"),
        inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private Set<Document> documents = new HashSet<>();
    
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reminder> reminders = new ArrayList<>();
}
```

### InterviewType Enum

```java
package com.workflowgo.model;

public enum InterviewType {
    REMOTE,
    ONSITE,
    PHONE
}
```

### InterviewStatus Enum

```java
package com.workflowgo.model;

public enum InterviewStatus {
    SCHEDULED,
    CONFIRMED,
    COMPLETED,
    REJECTED,
    CANCELLED
}
```

### ContactPerson Embedded Entity

```java
package com.workflowgo.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class ContactPerson {
    private String name;
    private String position;
    private String email;
    private String phone;
}
```

### Question Entity

```java
package com.workflowgo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 1000)
    private String question;
    
    @Column(length = 2000)
    private String answer;
    
    @Enumerated(EnumType.STRING)
    private QuestionCategory category;
    
    private boolean isImportant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;
}
```

### QuestionCategory Enum

```java
package com.workflowgo.model;

public enum QuestionCategory {
    TECHNICAL,
    BEHAVIORAL,
    COMPANY,
    ROLE,
    OTHER
}
```

### Document Entity

```java
package com.workflowgo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private DocumentType type;
    
    @Column(nullable = false)
    private String url;
    
    private String contentType;
    
    private Long size;
    
    @CreationTimestamp
    private ZonedDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToMany(mappedBy = "documents")
    private Set<Interview> interviews = new HashSet<>();
}
```

### DocumentType Enum

```java
package com.workflowgo.model;

public enum DocumentType {
    RESUME,
    COVER_LETTER,
    PORTFOLIO,
    OTHER
}
```

### Reminder Entity

```java
package com.workflowgo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "reminders")
@Data
@NoArgsConstructor
public class Reminder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;
    
    @Column(nullable = false)
    private ZonedDateTime time;
    
    @Column(nullable = false)
    private String message;
    
    private boolean isCompleted;
}
```

## 3. Security Configuration

### Security Config

```java
package com.workflowgo.config;

import com.workflowgo.security.CustomUserDetailsService;
import com.workflowgo.security.OAuth2AuthenticationSuccessHandler;
import com.workflowgo.security.OAuth2UserService;
import com.workflowgo.security.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private OAuth2UserService oAuth2UserService;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors()
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .csrf()
                .disable()
            .formLogin()
                .disable()
            .httpBasic()
                .disable()
            .authorizeRequests()
                .antMatchers("/",
                    "/error",
                    "/favicon.ico",
                    "/**/*.png",
                    "/**/*.gif",
                    "/**/*.svg",
                    "/**/*.jpg",
                    "/**/*.html",
                    "/**/*.css",
                    "/**/*.js")
                    .permitAll()
                .antMatchers("/auth/**", "/oauth2/**")
                    .permitAll()
                .anyRequest()
                    .authenticated()
                .and()
            .oauth2Login()
                .authorizationEndpoint()
                    .baseUri("/oauth2/authorize")
                    .and()
                .redirectionEndpoint()
                    .baseUri("/oauth2/callback/*")
                    .and()
                .userInfoEndpoint()
                    .userService(oAuth2UserService)
                    .and()
                .successHandler(oAuth2AuthenticationSuccessHandler);

        // Add our custom Token based authentication filter
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
```

## 4. JWT Token Provider

```java
package com.workflowgo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    @Value("${app.auth.tokenSecret}")
    private String tokenSecret;

    @Value("${app.auth.tokenExpirationMsec}")
    private long tokenExpirationMsec;

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpirationMsec);

        SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            logger.error("Invalid JWT token", ex);
            return false;
        }
    }
}
```

## 5. OAuth2 User Service

```java
package com.workflowgo.security;

import com.workflowgo.exception.OAuth2AuthenticationProcessingException;
import com.workflowgo.model.AuthProvider;
import com.workflowgo.model.User;
import com.workflowgo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.OAuth2AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(), 
                oAuth2User.getAttributes()
        );
        
        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        
        if (userOptional.isPresent()) {
            user = userOptional.get();
            
            if (!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("You've signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        user.setEmailVerified(true);
        
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        
        return userRepository.save(existingUser);
    }
}
```

## 6. OAuth2 User Info

### OAuth2UserInfo Interface

```java
package com.workflowgo.security;

import java.util.Map;

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();
}
```

### GoogleOAuth2UserInfo Implementation

```java
package com.workflowgo.security;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
```

### OAuth2UserInfoFactory

```java
package com.workflowgo.security;

import com.workflowgo.exception.OAuth2AuthenticationProcessingException;
import com.workflowgo.model.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
```

## 7. OAuth2 Authentication Success Handler

```java
package com.workflowgo.security;

import com.workflowgo.config.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private TokenProvider tokenProvider;

    @Value("${app.auth.authorizedRedirectUris}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = tokenProvider.createToken(authentication);

        return UriComponentsBuilder.fromUriString(redirectUri)
                .fragment("token=" + token)
                .build().toUriString();
    }
}
```

## 8. Token Authentication Filter

```java
package com.workflowgo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Long userId = tokenProvider.getUserIdFromToken(jwt);

                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

## 9. User Controller

```java
package com.workflowgo.controller;

import com.workflowgo.exception.ResourceNotFoundException;
import com.workflowgo.model.User;
import com.workflowgo.repository.UserRepository;
import com.workflowgo.security.CurrentUser;
import com.workflowgo.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/current")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}
```

## 10. Auth Controller

```java
package com.workflowgo.controller;

import com.workflowgo.model.AuthProvider;
import com.workflowgo.model.User;
import com.workflowgo.payload.ApiResponse;
import com.workflowgo.payload.AuthResponse;
import com.workflowgo.payload.LoginRequest;
import com.workflowgo.payload.SignUpRequest;
import com.workflowgo.repository.UserRepository;
import com.workflowgo.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Email address already in use."));
        }

        // Creating user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setProvider(AuthProvider.local);

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully"));
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        // If the request gets here, the token is valid (due to security filter)
        return ResponseEntity.ok().build();
    }
}
```

## 11. Interview Controller

```java
package com.workflowgo.controller;

import com.workflowgo.exception.ResourceNotFoundException;
import com.workflowgo.model.Interview;
import com.workflowgo.payload.InterviewRequest;
import com.workflowgo.payload.StatusUpdateRequest;
import com.workflowgo.repository.InterviewRepository;
import com.workflowgo.repository.UserRepository;
import com.workflowgo.security.CurrentUser;
import com.workflowgo.security.UserPrincipal;
import com.workflowgo.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/interviews")
public class InterviewController {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InterviewService interviewService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<Interview> getInterviews(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(required = false) InterviewStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String company,
            @RequestParam(required = false, defaultValue = "date") String sort,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        
        return interviewService.getInterviews(currentUser.getId(), status, from, to, company, sort, order);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createInterview(@Valid @RequestBody InterviewRequest interviewRequest, 
                                           @CurrentUser UserPrincipal currentUser) {
        Interview interview = interviewService.createInterview(interviewRequest, currentUser.getId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(interview.getId()).toUri();

        return ResponseEntity.created(location)
                .body(interview);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Interview getInterviewById(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        return interviewService.getInterviewById(id, currentUser.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Interview updateInterview(@PathVariable Long id,
                                   @Valid @RequestBody InterviewRequest interviewRequest,
                                   @CurrentUser UserPrincipal currentUser) {
        return interviewService.updateInterview(id, interviewRequest, currentUser.getId());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    public Interview updateInterviewStatus(@PathVariable Long id,
                                         @Valid @RequestBody StatusUpdateRequest statusRequest,
                                         @CurrentUser UserPrincipal currentUser) {
        return interviewService.updateInterviewStatus(id, statusRequest.getStatus(), currentUser.getId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteInterview(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        interviewService.deleteInterview(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
```

## 12. Interview Service

```java
package com.workflowgo.service;

import com.workflowgo.exception.ResourceNotFoundException;
import com.workflowgo.model.*;
import com.workflowgo.payload.InterviewRequest;
import com.workflowgo.payload.StatusUpdateRequest;
import com.workflowgo.repository.InterviewRepository;
import com.workflowgo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewService {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Interview> getInterviews(Long userId, InterviewStatus status, LocalDate from, LocalDate to, 
                                        String company, String sort, String order) {
        
        // Create sort object based on parameters
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(direction, sort);
        
        // Get all interviews for user
        List<Interview> interviews = interviewRepository.findByUserId(userId, sortObj);
        
        // Apply filters
        return interviews.stream()
                .filter(i -> status == null || i.getStatus() == status)
                .filter(i -> from == null || !i.getDate().isBefore(from))
                .filter(i -> to == null || !i.getDate().isAfter(to))
                .filter(i -> company == null || i.getCompanyName().toLowerCase().contains(company.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Interview createInterview(InterviewRequest interviewRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Interview interview = new Interview();
        updateInterviewFromRequest(interview, interviewRequest);
        interview.setUser(user);
        
        return interviewRepository.save(interview);
    }

    public Interview getInterviewById(Long interviewId, Long userId) {
        return interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", interviewId));
    }

    public Interview updateInterview(Long interviewId, InterviewRequest interviewRequest, Long userId) {
        Interview interview = getInterviewById(interviewId, userId);
        updateInterviewFromRequest(interview, interviewRequest);
        return interviewRepository.save(interview);
    }

    public Interview updateInterviewStatus(Long interviewId, InterviewStatus status, Long userId) {
        Interview interview = getInterviewById(interviewId, userId);
        interview.setStatus(status);
        return interviewRepository.save(interview);
    }

    public void deleteInterview(Long interviewId, Long userId) {
        Interview interview = getInterviewById(interviewId, userId);
        interviewRepository.delete(interview);
    }

    private void updateInterviewFromRequest(Interview interview, InterviewRequest request) {
        interview.setCompanyName(request.getCompanyName());
        interview.setPosition(request.getPosition());
        interview.setDate(request.getDate());
        interview.setTime(request.getTime());
        interview.setType(request.getType());
        interview.setStatus(request.getStatus());
        interview.setLocation(request.getLocation());
        interview.setNotes(request.getNotes());
        
        // Set contact person
        ContactPerson contactPerson = new ContactPerson();
        contactPerson.setName(request.getContactName());
        contactPerson.setPosition(request.getContactPosition());
        contactPerson.setEmail(request.getContactEmail());
        contactPerson.setPhone(request.getContactPhone());
        interview.setContactPerson(contactPerson);
        
        interview.setRating(request.getRating());
        interview.setFeedback(request.getFeedback());
    }
}
```

## 13. Interview Repository

```java
package com.workflowgo.repository;

import com.workflowgo.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByUserId(Long userId, Sort sort);
    Optional<Interview> findByIdAndUserId(Long id, Long userId);
}
```

## 14. Interview Request DTO

```java
package com.workflowgo.payload;

import com.workflowgo.model.InterviewStatus;
import com.workflowgo.model.InterviewType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class InterviewRequest {
    @NotBlank
    private String companyName;
    
    @NotBlank
    private String position;
    
    @NotNull
    private LocalDate date;
    
    @NotNull
    private LocalTime time;
    
    @NotNull
    private InterviewType type;
    
    @NotNull
    private InterviewStatus status;
    
    private String location;
    
    private String notes;
    
    private String contactName;
    
    private String contactPosition;
    
    private String contactEmail;
    
    private String contactPhone;
    
    private Integer rating;
    
    private String feedback;
}
```

## 15. Status Update Request DTO

```java
package com.workflowgo.payload;

import com.workflowgo.model.InterviewStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StatusUpdateRequest {
    @NotNull
    private InterviewStatus status;
}
```

## 16. Payload Classes

### LoginRequest

```java
package com.workflowgo.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
```

### SignUpRequest

```java
package com.workflowgo.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SignUpRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
```

### AuthResponse

```java
package com.workflowgo.payload;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
```

### ApiResponse

```java
package com.workflowgo.payload;

import lombok.Data;

@Data
public class ApiResponse {
    private boolean success;
    private String message;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
```

## 17. Repository

```java
package com.workflowgo.repository;

import com.workflowgo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}
```

## 18. Exception Classes

### ResourceNotFoundException

```java
package com.workflowgo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
```

### OAuth2AuthenticationProcessingException

```java
package com.workflowgo.exception;

import org.springframework.security.core.AuthenticationException;

public class OAuth2AuthenticationProcessingException extends AuthenticationException {
    public OAuth2AuthenticationProcessingException(String msg) {
        super(msg);
    }
}
```

## 19. CORS Configuration

```java
package com.workflowgo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowedOrigins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

## 20. Main Application Class

```java
package com.workflowgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class WorkFlowGoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkFlowGoApplication.class, args);
    }
}
```

## 21. Google OAuth Setup

1. Go to the [Google Developer Console](https://console.developers.google.com/)
2. Create a new project
3. Navigate to "Credentials" and create OAuth client ID
4. Configure the OAuth consent screen
5. Add authorized redirect URIs:
   - `http://localhost:8080/oauth2/callback/google` (for development)
   - `https://your-production-domain.com/oauth2/callback/google` (for production)
6. Note your Client ID and Client Secret
7. Set these as environment variables or in your application.yml:
   - `GOOGLE_CLIENT_ID`
   - `GOOGLE_CLIENT_SECRET`

## 22. Running the Application

1. Set the required environment variables:
   ```
   export GOOGLE_CLIENT_ID=your-client-id
   export GOOGLE_CLIENT_SECRET=your-client-secret
   export JWT_SECRET=your-jwt-secret
   export FRONTEND_URL=http://localhost:3000
   ```

2. Run the Spring Boot application:
   ```
   ./mvnw spring-boot:run
   ```

## 23. Testing the OAuth Flow

1. Access your frontend application
2. Click "Login with Google"
3. You will be redirected to Google's login page
4. After successful authentication, Google will redirect back to your application
5. The Spring Boot backend will:
   - Process the OAuth callback
   - Create or update the user in the database
   - Generate a JWT token
   - Redirect to your frontend with the token
6. The frontend will:
   - Extract the token from the URL
   - Store it in localStorage
   - Use it for subsequent API calls

This completes the implementation of Google OAuth2 authentication in a Spring Boot application for the Work-Flow-Go project.

## 24. Document Controller

```java
package com.workflowgo.controller;

import com.workflowgo.model.Document;
import com.workflowgo.payload.ApiResponse;
import com.workflowgo.security.CurrentUser;
import com.workflowgo.security.UserPrincipal;
import com.workflowgo.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<Document> getAllDocuments(@CurrentUser UserPrincipal currentUser) {
        return documentService.getAllDocumentsByUser(currentUser.getId());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @CurrentUser UserPrincipal currentUser) {
        
        Document document = documentService.storeDocument(file, name, type, currentUser.getId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(document.getId()).toUri();

        return ResponseEntity.created(location)
                .body(document);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Document getDocument(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        return documentService.getDocumentById(id, currentUser.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Document updateDocument(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @CurrentUser UserPrincipal currentUser) {
        
        return documentService.updateDocument(id, name, type, currentUser.getId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        documentService.deleteDocument(id, currentUser.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Document deleted successfully"));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id,
            @CurrentUser UserPrincipal currentUser,
            HttpServletRequest request) {
        
        // Load document as Resource
        Resource resource = documentService.loadDocumentAsResource(id, currentUser.getId());

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Fallback to the default content type
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        Document document = documentService.getDocumentById(id, currentUser.getId());
        String filename = document.getName();
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/view")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource> viewDocument(
            @PathVariable Long id,
            @CurrentUser UserPrincipal currentUser,
            HttpServletRequest request) {
        
        // Load document as Resource
        Resource resource = documentService.loadDocumentAsResource(id, currentUser.getId());

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Fallback to the default content type
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }
}
```

## 25. Document Service

```java
package com.workflowgo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflowgo.exception.FileStorageException;
import com.workflowgo.exception.ResourceNotFoundException;
import com.workflowgo.model.Document;
import com.workflowgo.model.DocumentType;
import com.workflowgo.model.User;
import com.workflowgo.payload.UserSettingsRequest;
import com.workflowgo.repository.DocumentRepository;
import com.workflowgo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentService {

    private final Path fileStorageLocation;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    public DocumentService(@Value("${app.file.upload-dir:./uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public List<Document> getAllDocumentsByUser(Long userId) {
        return documentRepository.findByUserId(userId);
    }

    public Document storeDocument(MultipartFile file, String name, String type, Long userId) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        // Generate unique file name
        String storedFileName = UUID.randomUUID().toString() + fileExtension;
        
        try {
            // Check if the file's name contains invalid characters
            if (originalFileName.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + originalFileName);
            }

            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Create document entity
            Document document = new Document();
            document.setName(name);
            document.setType(DocumentType.valueOf(type.toUpperCase()));
            document.setUrl(storedFileName);
            document.setContentType(file.getContentType());
            document.setSize(file.getSize());
            
            // Set user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            document.setUser(user);
            
            return documentRepository.save(document);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public Document getDocumentById(Long documentId, Long userId) {
        return documentRepository.findByIdAndUserId(documentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));
    }

    public Document updateDocument(Long documentId, String name, String type, Long userId) {
        Document document = getDocumentById(documentId, userId);
        document.setName(name);
        document.setType(DocumentType.valueOf(type.toUpperCase()));
        return documentRepository.save(document);
    }

    public void deleteDocument(Long documentId, Long userId) {
        Document document = getDocumentById(documentId, userId);
        
        // Delete the file
        try {
            Path filePath = this.fileStorageLocation.resolve(document.getUrl()).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // Log the error but continue with database deletion
            System.err.println("Error deleting file: " + ex.getMessage());
        }
        
        // Delete from database
        documentRepository.delete(document);
    }

    public Resource loadDocumentAsResource(Long documentId, Long userId) {
        try {
            Document document = getDocumentById(documentId, userId);
            Path filePath = this.fileStorageLocation.resolve(document.getUrl()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File", "id", documentId);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File", "id", documentId);
        }
    }
}
```

## 26. Document Repository

```java
package com.workflowgo.repository;

import com.workflowgo.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserId(Long userId);
    Optional<Document> findByIdAndUserId(Long id, Long userId);
}
```

## 27. File Storage Exception

```java
package com.workflowgo.exception;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## 28. Payload Classes

{{ ... }}

## 29. Statistics Controller

```java
package com.workflowgo.controller;

import com.workflowgo.payload.InterviewStatistics;
import com.workflowgo.security.CurrentUser;
import com.workflowgo.security.UserPrincipal;
import com.workflowgo.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/interviews")
    @PreAuthorize("hasRole('USER')")
    public InterviewStatistics getInterviewStatistics(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        return statisticsService.getInterviewStatistics(currentUser.getId(), from, to);
    }
}
```

## 30. Statistics Service

```java
package com.workflowgo.service;

import com.workflowgo.model.Interview;
import com.workflowgo.model.InterviewStatus;
import com.workflowgo.payload.InterviewStatistics;
import com.workflowgo.payload.StatisticItem;
import com.workflowgo.repository.InterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private InterviewRepository interviewRepository;

    public InterviewStatistics getInterviewStatistics(Long userId, LocalDate from, LocalDate to) {
        // Get all interviews for user
        List<Interview> interviews = interviewRepository.findByUserId(userId, Sort.by(Sort.Direction.ASC, "date"));
        
        // Apply date filters if provided
        if (from != null) {
            interviews = interviews.stream()
                    .filter(i -> !i.getDate().isBefore(from))
                    .collect(Collectors.toList());
        }
        
        if (to != null) {
            interviews = interviews.stream()
                    .filter(i -> !i.getDate().isAfter(to))
                    .collect(Collectors.toList());
        }
        
        // Create statistics object
        InterviewStatistics statistics = new InterviewStatistics();
        
        // Total interviews
        statistics.setTotalInterviews(interviews.size());
        
        // Count by status
        Map<InterviewStatus, Long> countByStatus = interviews.stream()
                .collect(Collectors.groupingBy(Interview::getStatus, Collectors.counting()));
        
        statistics.setUpcomingInterviews(
                countByStatus.getOrDefault(InterviewStatus.SCHEDULED, 0L) + 
                countByStatus.getOrDefault(InterviewStatus.CONFIRMED, 0L)
        );
        
        statistics.setCompletedInterviews(countByStatus.getOrDefault(InterviewStatus.COMPLETED, 0L));
        statistics.setRejectedInterviews(countByStatus.getOrDefault(InterviewStatus.REJECTED, 0L));
        
        Map<String, Long> statusMap = new HashMap<>();
        for (InterviewStatus status : InterviewStatus.values()) {
            statusMap.put(status.name(), countByStatus.getOrDefault(status, 0L));
        }
        statistics.setByStatus(statusMap);
        
        // Count by company
        Map<String, Long> countByCompany = interviews.stream()
                .collect(Collectors.groupingBy(Interview::getCompanyName, Collectors.counting()));
        
        List<StatisticItem> byCompany = countByCompany.entrySet().stream()
                .map(entry -> new StatisticItem(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(StatisticItem::getCount).reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        statistics.setByCompany(byCompany);
        
        // Count by month
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> countByMonth = interviews.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getDate().format(monthFormatter),
                        Collectors.counting()
                ));
        
        // Ensure all months in range are included
        if (from != null && to != null) {
            YearMonth start = YearMonth.from(from);
            YearMonth end = YearMonth.from(to);
            
            while (!start.isAfter(end)) {
                String monthKey = start.format(monthFormatter);
                if (!countByMonth.containsKey(monthKey)) {
                    countByMonth.put(monthKey, 0L);
                }
                start = start.plusMonths(1);
            }
        }
        
        List<StatisticItem> byMonth = countByMonth.entrySet().stream()
                .map(entry -> new StatisticItem(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(StatisticItem::getLabel))
                .collect(Collectors.toList());
        
        statistics.setByMonth(byMonth);
        
        return statistics;
    }
}
```

## 31. Interview Statistics DTO

```java
package com.workflowgo.payload;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InterviewStatistics {
    private long totalInterviews;
    private long upcomingInterviews;
    private long completedInterviews;
    private long rejectedInterviews;
    private Map<String, Long> byStatus;
    private List<StatisticItem> byCompany;
    private List<StatisticItem> byMonth;
}
```

## 32. Statistic Item DTO

```java
package com.workflowgo.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticItem {
    private String label;
    private Long count;
}
```

## 33. User Settings Controller

```java
package com.workflowgo.controller;

import com.workflowgo.model.User;
import com.workflowgo.payload.ApiResponse;
import com.workflowgo.payload.UserSettingsRequest;
import com.workflowgo.security.CurrentUser;
import com.workflowgo.security.UserPrincipal;
import com.workflowgo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserSettingsController {

    @Autowired
    private UserService userService;

    @GetMapping("/settings")
    @PreAuthorize("hasRole('USER')")
    public User getUserSettings(@CurrentUser UserPrincipal currentUser) {
        return userService.getUserById(currentUser.getId());
    }

    @PutMapping("/settings")
    @PreAuthorize("hasRole('USER')")
    public User updateUserSettings(
            @Valid @RequestBody UserSettingsRequest settingsRequest,
            @CurrentUser UserPrincipal currentUser) {
        
        return userService.updateUserSettings(currentUser.getId(), settingsRequest);
    }

    @PostMapping("/profile-image")
    @PreAuthorize("hasRole('USER')")
    public User uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @CurrentUser UserPrincipal currentUser) {
        
        return userService.updateProfileImage(currentUser.getId(), file);
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> exportUserData(@CurrentUser UserPrincipal currentUser) {
        byte[] data = userService.exportUserData(currentUser.getId());
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=user-data-export.json")
                .body(data);
    }

    @PostMapping("/settings/reset")
    @PreAuthorize("hasRole('USER')")
    public User resetUserSettings(@CurrentUser UserPrincipal currentUser) {
        return userService.resetUserSettings(currentUser.getId());
    }
}
```

## 34. User Settings Request DTO

```java
package com.workflowgo.payload;

import lombok.Data;

@Data
public class UserSettingsRequest {
    private String displayName;
    private boolean darkMode;
    private String primaryColor;
    private boolean emailNotifications;
    private String reminderTime;
    private String defaultView;
    private boolean compactMode;
}
```

## 35. User Service

```java
package com.workflowgo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflowgo.exception.FileStorageException;
import com.workflowgo.exception.ResourceNotFoundException;
import com.workflowgo.model.Document;
import com.workflowgo.model.Interview;
import com.workflowgo.model.User;
import com.workflowgo.model.UserPreferences;
import com.workflowgo.payload.UserSettingsRequest;
import com.workflowgo.repository.DocumentRepository;
import com.workflowgo.repository.InterviewRepository;
import com.workflowgo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InterviewRepository interviewRepository;
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private final Path profileImageStorageLocation;
    
    @Autowired
    public UserService(@Value("${app.file.profile-images-dir:./profile-images}") String profileImagesDir) {
        this.profileImageStorageLocation = Paths.get(profileImagesDir)
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.profileImageStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where profile images will be stored.", ex);
        }
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    public User updateUserSettings(Long userId, UserSettingsRequest settingsRequest) {
        User user = getUserById(userId);
        
        if (settingsRequest.getDisplayName() != null && !settingsRequest.getDisplayName().isEmpty()) {
            user.setName(settingsRequest.getDisplayName());
        }
        
        // Update preferences
        UserPreferences preferences = user.getPreferences();
        if (preferences == null) {
            preferences = new UserPreferences();
            user.setPreferences(preferences);
        }
        
        preferences.setDarkMode(settingsRequest.isDarkMode());
        preferences.setPrimaryColor(settingsRequest.getPrimaryColor());
        preferences.setEmailNotifications(settingsRequest.isEmailNotifications());
        preferences.setReminderTime(settingsRequest.getReminderTime());
        preferences.setDefaultView(settingsRequest.getDefaultView());
        preferences.setCompactMode(settingsRequest.isCompactMode());
        
        return userRepository.save(user);
    }

    public User updateProfileImage(Long userId, MultipartFile file) {
        User user = getUserById(userId);
        
        // Delete old profile image if exists
        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            try {
                Path oldImagePath = this.profileImageStorageLocation.resolve(user.getImageUrl()).normalize();
                Files.deleteIfExists(oldImagePath);
            } catch (IOException ex) {
                // Log but continue
                System.err.println("Error deleting old profile image: " + ex.getMessage());
            }
        }
        
        // Generate unique file name
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + fileExtension;
        
        try {
            // Save new image
            Path targetLocation = this.profileImageStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // Update user
            user.setImageUrl(fileName);
            return userRepository.save(user);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store profile image. Please try again!", ex);
        }
    }

    public byte[] exportUserData(Long userId) {
        try {
            User user = getUserById(userId);
            List<Interview> interviews = interviewRepository.findByUserId(userId);
            List<Document> documents = documentRepository.findByUserId(userId);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("user", user);
            userData.put("interviews", interviews);
            userData.put("documents", documents);
            
            return objectMapper.writeValueAsBytes(userData);
        } catch (IOException ex) {
            throw new RuntimeException("Error exporting user data", ex);
        }
    }

    public User resetUserSettings(Long userId) {
        User user = getUserById(userId);
        user.setPreferences(new UserPreferences());
        return userRepository.save(user);
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty() || !fileName.contains(".")) {
            return ".jpg"; // Default extension
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}

## 36. Application Configuration

### Complete application.yml Configuration

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:workflowdb
    username: sa
    password: password
    driver-class-name: org.h2.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            redirect-uri: "{baseUrl}/oauth2/callback/google"
            scope:
              - email
              - profile
  
  servlet:
    multipart:
      enabled: true
      file-size-threshold: 2KB
      max-file-size: 10MB
      max-request-size: 15MB

server:
  port: 8080
  servlet:
    context-path: /api

# Custom application properties
app:
  auth:
    tokenSecret: ${JWT_SECRET:bd04b7c89bfd96a0f14a997f58f2246c37883f3825dd1a3f3d636f9fb0d386a0}
    tokenExpirationMsec: 864000000 # 10 days
    authorizedRedirectUris: ${FRONTEND_URL:http://localhost:3000}/login/success
  
  cors:
    allowedOrigins: ${ALLOWED_ORIGINS:http://localhost:3000,http://localhost:8080}
  
  file:
    upload-dir: ${FILE_UPLOAD_DIR:./uploads}
    profile-images-dir: ${PROFILE_IMAGES_DIR:./profile-images}
```

### Logging Configuration (logback.xml)

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/work-flow-go.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/work-flow-go.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
    
    <logger name="com.workflowgo" level="DEBUG" />
    <logger name="org.springframework.security" level="DEBUG" />
</configuration>
```

## 37. Deployment Instructions

### Docker Deployment

#### Dockerfile

```dockerfile
FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/work-flow-go-0.0.1-SNAPSHOT.jar app.jar

# Create directories for file storage
RUN mkdir -p /app/uploads
RUN mkdir -p /app/profile-images

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
ENV GOOGLE_CLIENT_ID=your-client-id
ENV GOOGLE_CLIENT_SECRET=your-client-secret
ENV JWT_SECRET=your-jwt-secret
ENV FRONTEND_URL=https://your-frontend-url.com
ENV FILE_UPLOAD_DIR=/app/uploads
ENV PROFILE_IMAGES_DIR=/app/profile-images

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### docker-compose.yml

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
      - GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
      - JWT_SECRET=${JWT_SECRET}
      - FRONTEND_URL=${FRONTEND_URL}
    volumes:
      - ./uploads:/app/uploads
      - ./profile-images:/app/profile-images
    restart: always
    
  db:
    image: postgres:13
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
      - POSTGRES_DB=workflowdb
    volumes:
      - postgres-data:/var/lib/postgresql/data
    restart: always

volumes:
  postgres-data:
```

### Production Configuration (application-prod.yml)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:db}:${DB_PORT:5432}/${DB_NAME:workflowdb}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    show-sql: false

server:
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: /api

# Logging configuration
logging:
  level:
    root: INFO
    com.workflowgo: INFO
    org.springframework.security: INFO
  file:
    name: /app/logs/work-flow-go.log
```

### Deployment Steps

1. **Build the application**:
   ```bash
   ./mvnw clean package -DskipTests
   ```

2. **Set up environment variables**:
   Create a `.env` file with the following variables:
   ```
   GOOGLE_CLIENT_ID=your-google-client-id
   GOOGLE_CLIENT_SECRET=your-google-client-secret
   JWT_SECRET=your-jwt-secret
   FRONTEND_URL=https://your-frontend-url.com
   ```

3. **Start the containers**:
   ```bash
   docker-compose up -d
   ```

4. **Monitor the logs**:
   ```bash
   docker-compose logs -f app
   ```

### Cloud Deployment (AWS)

1. **Create an Elastic Beanstalk application**:
   - Create a new application in Elastic Beanstalk
   - Choose Java platform
   - Upload your JAR file

2. **Configure environment variables**:
   - Set all required environment variables in the Elastic Beanstalk environment configuration

3. **Configure database**:
   - Create an RDS PostgreSQL instance
   - Configure security groups to allow connections from your Elastic Beanstalk environment
   - Update the database connection properties in your application

4. **Configure file storage**:
   - Create an S3 bucket for file storage
   - Update the file storage configuration to use S3 instead of local storage

## 38. Integration Testing

### Sample Integration Test for OAuth2 Authentication

```java
package com.workflowgo.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OAuth2AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void testLoginPageAccessible() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAuthenticatedUserCanAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/user/current"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUnauthenticatedUserCannotAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/user/current"))
                .andExpect(status().isUnauthorized());
    }
}
```

## 39. Conclusion

This guide provides a comprehensive implementation of Google OAuth2 authentication in a Spring Boot application for the Work-Flow-Go project. The implementation includes:

1. **Authentication and Security**:
   - Google OAuth2 integration
   - JWT token-based authentication
   - Role-based access control

2. **Domain Models**:
   - User management
   - Interview tracking
   - Document management
   - User preferences

3. **API Endpoints**:
   - Authentication endpoints
   - User management
   - Interview CRUD operations
   - Document upload and management
   - Statistics and reporting

4. **File Storage**:
   - Document storage
   - Profile image management

5. **Deployment Options**:
   - Docker deployment
   - Cloud deployment (AWS)

By following this guide, you can implement a secure and scalable backend for the Work-Flow-Go application that integrates seamlessly with the Vue.js frontend.

To get started:
1. Set up your Google OAuth credentials
2. Configure the application properties
3. Build and run the application
4. Connect your frontend to the backend API

The Spring Boot backend will handle authentication, data storage, and business logic, while the Vue.js frontend will provide a responsive and user-friendly interface for managing interviews and documents.

Happy coding!
