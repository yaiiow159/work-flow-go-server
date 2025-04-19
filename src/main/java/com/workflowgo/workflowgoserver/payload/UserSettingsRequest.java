package com.workflowgo.workflowgoserver.payload;

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
