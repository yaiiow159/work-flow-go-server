package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.UserPreferences;
import lombok.Data;

@Data
public class UserPreferencesDTO {
    private ThemeDTO theme;
    private NotificationsDTO notifications;
    private DisplayDTO display;
    
    public static UserPreferencesDTO fromUserPreferences(UserPreferences preferences) {
        if (preferences == null) {
            return null;
        }
        
        UserPreferencesDTO dto = new UserPreferencesDTO();
        
        ThemeDTO themeDTO = new ThemeDTO();
        themeDTO.setDarkMode(preferences.isDarkMode());
        themeDTO.setPrimaryColor(preferences.getPrimaryColor());
        dto.setTheme(themeDTO);
        
        NotificationsDTO notificationsDTO = new NotificationsDTO();
        notificationsDTO.setEnabled(true);
        notificationsDTO.setEmailNotifications(preferences.isEmailNotifications());
        notificationsDTO.setReminderTime(preferences.getReminderTime());
        dto.setNotifications(notificationsDTO);
        
        DisplayDTO displayDTO = new DisplayDTO();
        displayDTO.setDefaultView(preferences.getDefaultView());
        displayDTO.setCompactMode(preferences.isCompactMode());
        dto.setDisplay(displayDTO);
        
        return dto;
    }
    
    @Data
    public static class ThemeDTO {
        private boolean darkMode;
        private String primaryColor;
    }
    
    @Data
    public static class NotificationsDTO {
        private boolean enabled;
        private boolean emailNotifications;
        private String reminderTime;
    }
    
    @Data
    public static class DisplayDTO {
        private String defaultView;
        private boolean compactMode;
    }
}
