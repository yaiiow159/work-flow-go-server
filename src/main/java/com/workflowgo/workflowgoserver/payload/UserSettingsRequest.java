package com.workflowgo.workflowgoserver.payload;

import lombok.Data;

@Data
public class UserSettingsRequest {
    private String displayName;
    private String bio;
    private String phone;
    private String location;
    private String company;
    private String position;
    private boolean darkMode;
    private String primaryColor;
    private boolean emailNotifications;
    private String reminderTime;
    private String defaultView;
    private boolean compactMode;
}
