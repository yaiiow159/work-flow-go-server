package com.workflowgo.workflowgoserver.payload;

import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotNull(message = "Status is required")
    private InterviewStatus status;
}
