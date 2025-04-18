package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private UUID id;
    private String name;
    private String email;
    
    private boolean darkMode;
    private String primaryColor;
    
    private boolean notificationsEnabled;
    private boolean emailNotifications;
    private String reminderTime;
    
    private User.DisplayPreferences.DefaultView defaultView;
    private boolean compactMode;
    
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .darkMode(user.getThemePreferences() != null && user.getThemePreferences().isDarkMode())
                .primaryColor(user.getThemePreferences() != null ? user.getThemePreferences().getPrimaryColor() : null)
                .notificationsEnabled(user.getNotificationPreferences() != null && user.getNotificationPreferences().isEnabled())
                .emailNotifications(user.getNotificationPreferences() != null && user.getNotificationPreferences().isEmailNotifications())
                .reminderTime(user.getNotificationPreferences() != null ? user.getNotificationPreferences().getReminderTime() : null)
                .defaultView(user.getDisplayPreferences() != null ? user.getDisplayPreferences().getDefaultView() : null)
                .compactMode(user.getDisplayPreferences() != null && user.getDisplayPreferences().isCompactMode())
                .build();
    }
    
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        
        User.ThemePreferences themePreferences = User.ThemePreferences.builder()
                .darkMode(this.darkMode)
                .primaryColor(this.primaryColor)
                .build();
        user.setThemePreferences(themePreferences);
        
        User.NotificationPreferences notificationPreferences = User.NotificationPreferences.builder()
                .enabled(this.notificationsEnabled)
                .emailNotifications(this.emailNotifications)
                .reminderTime(this.reminderTime)
                .build();
        user.setNotificationPreferences(notificationPreferences);
        
        User.DisplayPreferences displayPreferences = User.DisplayPreferences.builder()
                .defaultView(this.defaultView)
                .compactMode(this.compactMode)
                .build();
        user.setDisplayPreferences(displayPreferences);
        
        return user;
    }
}
