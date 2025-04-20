package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import com.workflowgo.workflowgoserver.payload.InterviewStatistics;
import com.workflowgo.workflowgoserver.payload.StatisticItem;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final InterviewRepository interviewRepository;

    public StatisticsService(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }

    public InterviewStatistics getInterviewStatistics(Long userId, LocalDate from, LocalDate to) {
        List<Interview> interviews = interviewRepository.findByUserId(userId, Sort.by(Sort.Direction.ASC, "date"));
        
        if (from != null) {
            interviews = interviews.stream()
                    .filter(i -> !i.getDate().isBefore(from))
                    .collect(Collectors.toList());
        }
        
        if (to != null) {
            interviews = interviews.stream()
                    .filter(i -> !i.getDate().isAfter(to))
                    .collect(Collectors.toList());
        }
        
        InterviewStatistics statistics = new InterviewStatistics();
        
        statistics.setTotalInterviews(interviews.size());
        
        Map<InterviewStatus, Long> countByStatus = interviews.stream()
                .collect(Collectors.groupingBy(Interview::getStatus, Collectors.counting()));
        
        statistics.setUpcomingInterviews(
                countByStatus.getOrDefault(InterviewStatus.SCHEDULED, 0L) + 
                countByStatus.getOrDefault(InterviewStatus.CONFIRMED, 0L)
        );
        
        statistics.setCompletedInterviews(countByStatus.getOrDefault(InterviewStatus.COMPLETED, 0L));
        statistics.setRejectedInterviews(countByStatus.getOrDefault(InterviewStatus.REJECTED, 0L));
        
        Map<String, Long> statusMap = new HashMap<>();
        for (InterviewStatus status : InterviewStatus.values()) {
            statusMap.put(status.name(), countByStatus.getOrDefault(status, 0L));
        }
        statistics.setByStatus(statusMap);
        
        Map<String, Long> countByCompany = interviews.stream()
                .collect(Collectors.groupingBy(Interview::getCompanyName, Collectors.counting()));
        
        List<StatisticItem> byCompany = countByCompany.entrySet().stream()
                .map(entry -> new StatisticItem(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(StatisticItem::getCount).reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        statistics.setByCompany(byCompany);
        
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> countByMonth = interviews.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getDate().format(monthFormatter),
                        Collectors.counting()
                ));
        
        if (from != null && to != null) {
            YearMonth start = YearMonth.from(from);
            YearMonth end = YearMonth.from(to);
            
            while (!start.isAfter(end)) {
                String monthKey = start.format(monthFormatter);
                if (!countByMonth.containsKey(monthKey)) {
                    countByMonth.put(monthKey, 0L);
                }
                start = start.plusMonths(1);
            }
        }
        
        List<StatisticItem> byMonth = countByMonth.entrySet().stream()
                .map(entry -> new StatisticItem(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(StatisticItem::getLabel))
                .collect(Collectors.toList());
        
        statistics.setByMonth(byMonth);
        
        return statistics;
    }
}
