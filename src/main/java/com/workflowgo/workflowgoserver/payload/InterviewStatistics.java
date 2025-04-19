package com.workflowgo.workflowgoserver.payload;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InterviewStatistics {
    private long totalInterviews;
    private long upcomingInterviews;
    private long completedInterviews;
    private long rejectedInterviews;
    private Map<String, Long> byStatus;
    private List<StatisticItem> byCompany;
    private List<StatisticItem> byMonth;
}
