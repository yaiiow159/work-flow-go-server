package com.workflowgo.workflowgoserver.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

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
