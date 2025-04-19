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
    
    private User.Display.DefaultView defaultView;
    private boolean compactMode;
    
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .darkMode(user.getPreferences() != null && user.getPreferences().getTheme() != null && user.getPreferences().getTheme().isDarkMode())
                .primaryColor(user.getPreferences() != null && user.getPreferences().getTheme() != null ? user.getPreferences().getTheme().getPrimaryColor() : null)
                .notificationsEnabled(user.getPreferences() != null && user.getPreferences().getNotifications() != null && user.getPreferences().getNotifications().isEnabled())
                .emailNotifications(user.getPreferences() != null && user.getPreferences().getNotifications() != null && user.getPreferences().getNotifications().isEmailNotifications())
                .reminderTime(user.getPreferences() != null && user.getPreferences().getNotifications() != null ? user.getPreferences().getNotifications().getReminderTime() : null)
                .defaultView(user.getPreferences() != null && user.getPreferences().getDisplay() != null ? user.getPreferences().getDisplay().getDefaultView() : null)
                .compactMode(user.getPreferences() != null && user.getPreferences().getDisplay() != null && user.getPreferences().getDisplay().isCompactMode())
                .build();
    }
    
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        
        User.Theme theme = User.Theme.builder()
                .darkMode(this.darkMode)
                .primaryColor(this.primaryColor)
                .build();
        
        User.Notifications notifications = User.Notifications.builder()
                .enabled(this.notificationsEnabled)
                .emailNotifications(this.emailNotifications)
                .reminderTime(this.reminderTime)
                .build();
        
        User.Display display = User.Display.builder()
                .defaultView(this.defaultView)
                .compactMode(this.compactMode)
                .build();
        
        User.Preferences preferences = User.Preferences.builder()
                .theme(theme)
                .notifications(notifications)
                .display(display)
                .build();
        
        user.setPreferences(preferences);
        
        return user;
    }
}
