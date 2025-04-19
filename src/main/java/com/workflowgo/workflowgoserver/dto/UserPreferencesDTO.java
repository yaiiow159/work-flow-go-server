package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.UserPreferences;
import lombok.Data;

@Data
public class UserPreferencesDTO {
    private boolean darkMode;
    private String primaryColor;
    private boolean emailNotifications;
    private String reminderTime;
    private String defaultView;
    private boolean compactMode;
    
    public static UserPreferencesDTO fromUserPreferences(UserPreferences preferences) {
        if (preferences == null) {
            return null;
        }
        
        UserPreferencesDTO dto = new UserPreferencesDTO();
        dto.setDarkMode(preferences.isDarkMode());
        dto.setPrimaryColor(preferences.getPrimaryColor());
        dto.setEmailNotifications(preferences.isEmailNotifications());
        dto.setReminderTime(preferences.getReminderTime());
        dto.setDefaultView(preferences.getDefaultView());
        dto.setCompactMode(preferences.isCompactMode());
        return dto;
    }
}
