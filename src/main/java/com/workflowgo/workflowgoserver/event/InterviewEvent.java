package com.workflowgo.workflowgoserver.event;

import com.workflowgo.workflowgoserver.model.Interview;
import lombok.Getter;

@Getter
public class InterviewEvent {
    private final Interview interview;
    private final String email;
    private final String changeType;

    public InterviewEvent(Interview interview, String email, String changeType) {
        this.interview = interview;
        this.email = email;
        this.changeType = changeType;
    }
}
