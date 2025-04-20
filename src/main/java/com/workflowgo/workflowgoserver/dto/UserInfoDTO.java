package com.workflowgo.workflowgoserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String name;
    private String email;
    private String bio;
    private String phone;
    private String location;
    private String company;
    private String position;
    private String photoURL;
    
    @Data
    @NoArgsConstructor
    public static class ThemePreferences {
        private boolean darkMode = false;
        private String primaryColor = "#4f46e5";
    }
    
    @Data
    @NoArgsConstructor
    public static class NotificationPreferences {
        private boolean enabled = true;
        private boolean emailNotifications = true;
        private String reminderTime = "1h";
    }
    
    @Data
    @NoArgsConstructor
    public static class DisplayPreferences {
        private String defaultView = "list";
        private boolean compactMode = false;
    }
    
    @Data
    @NoArgsConstructor
    public static class Preferences {
        private ThemePreferences theme = new ThemePreferences();
        private NotificationPreferences notifications = new NotificationPreferences();
        private DisplayPreferences display = new DisplayPreferences();
    }
    
    private Preferences preferences = new Preferences();
}
