package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewStatisticsDTO {
    
    private int totalInterviews;
    private int upcomingInterviews;
    private int completedInterviews;
    private int rejectedInterviews;
    
    private Map<InterviewStatus, Integer> byStatus;
    
    private List<CompanyCount> byCompany;
    private List<MonthCount> byMonth;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanyCount {
        private String company;
        private int count;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthCount {
        private String month;
        private int count;
    }
}
