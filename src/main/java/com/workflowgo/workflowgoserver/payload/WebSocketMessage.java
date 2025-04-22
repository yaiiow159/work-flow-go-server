package com.workflowgo.workflowgoserver.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketMessage {

    private WebSocketMessageType type;
    private Object data;

    public enum WebSocketMessageType {
        NOTIFICATION,
        NOTIFICATION_UPDATE,
        NOTIFICATION_READ,
        NOTIFICATION_DELETE,
        INTERVIEW_REMINDER,
        SYSTEM_MESSAGE
    }
}