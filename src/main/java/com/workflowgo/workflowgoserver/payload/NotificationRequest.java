package com.workflowgo.workflowgoserver.payload;

import com.workflowgo.workflowgoserver.model.enums.RelatedEntityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    private String relatedEntityId;
    
    private RelatedEntityType relatedEntityType;
}
