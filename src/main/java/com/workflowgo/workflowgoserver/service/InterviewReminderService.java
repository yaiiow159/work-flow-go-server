package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.InterviewDTO;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.model.UserPreferences;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class InterviewReminderService {

    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    private final InterviewService interviewService;

    private final Set<String> remindedInterviews = new HashSet<>();

    public InterviewReminderService(InterviewRepository interviewRepository, UserRepository userRepository, WebSocketService webSocketService, InterviewService interviewService) {
        this.interviewRepository = interviewRepository;
        this.userRepository = userRepository;
        this.webSocketService = webSocketService;
        this.interviewService = interviewService;
    }

    @Scheduled(fixedRate = 60_000)
    public void checkUpcomingInterviews() {
        log.debug("Checking for upcoming interviews...");
        List<User> users = userRepository.findAll();

        for (User user : users) {
            try {
                UserPreferences prefs = user.getPreferences();
                if (prefs == null || !prefs.isNotificationsEnabled()) {
                    continue;
                }
                int minutes = getReminderTimeMinutes(prefs);

                List<Interview> allInterviews = interviewRepository.findByUserId(user.getId());

                for (Interview interview : allInterviews) {
                    String key = user.getId() + "-" + interview.getId();
                    if (remindedInterviews.contains(key)) {
                        continue;
                    }
                    if (shouldSendReminder(interview, minutes)) {
                        InterviewDTO dto = interviewService.convertToDTO(interview);
                        String label = minutes + " minutes";
                        webSocketService.sendInterviewReminder(user, dto, label);

                        remindedInterviews.add(key);
                        log.info("Sent reminder to user {} for interview {} (in {} min)",
                                user.getId(), interview.getId(), minutes);
                    }
                }
            } catch (Exception e) {
                log.error("Error processing reminders for user {}: {}", user.getId(), e.getMessage());
            }
        }
    }

    @Scheduled(fixedRate = 3_600_000)
    public void cleanupRemindedInterviews() {
        log.debug("Cleaning up expired reminders...");
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Interview> allInterviews = interviewRepository.findByUserId(user.getId());
            for (Interview interview : allInterviews) {
                String key = user.getId() + "-" + interview.getId();
                if (remindedInterviews.contains(key) && hasInterviewPassed(interview)) {
                    remindedInterviews.remove(key);
                    log.debug("Removed expired reminder key {}", key);
                }
            }
        }
    }

    private int getReminderTimeMinutes(UserPreferences prefs) {
        String t = StringUtils.hasText(prefs.getReminderTime())
                ? prefs.getReminderTime()
                : "30";
        return switch (t) {
            case "1hour"  -> 60;
            case "3hours" -> 180;
            case "1day"   -> 1440;
            case "2days"  -> 2880;
            default       -> 30;
        };
    }

    private boolean shouldSendReminder(Interview interview, int minutes) {
        try {
            LocalDateTime dt = LocalDateTime.parse(
                    interview.getDate() + "T" + interview.getTime(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            );
            ZonedDateTime zdt = dt.atZone(ZoneId.systemDefault());
            ZonedDateTime now = ZonedDateTime.now();

            ZonedDateTime reminderTime = zdt.minusMinutes(minutes);
            return now.isAfter(reminderTime) && now.isBefore(zdt);
        } catch (Exception e) {
            log.error("Error checking reminder for interview {}: {}", interview.getId(), e.getMessage());
            return false;
        }
    }

    private boolean hasInterviewPassed(Interview interview) {
        try {
            LocalDateTime dt = LocalDateTime.parse(
                    interview.getDate() + "T" + interview.getTime(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            );
            return ZonedDateTime.now().isAfter(dt.atZone(ZoneId.systemDefault()));
        } catch (Exception e) {
            log.error("Error checking if interview {} has passed: {}", interview.getId(), e.getMessage());
            return false;
        }
    }
}
