package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.event.InterviewEvent;
import com.workflowgo.workflowgoserver.event.InterviewEventListener;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.ContactPerson;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import com.workflowgo.workflowgoserver.model.enums.InterviewType;
import com.workflowgo.workflowgoserver.payload.InterviewRequest;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InterviewService(InterviewRepository interviewRepository, 
                           UserRepository userRepository,
                            ApplicationEventPublisher eventPublisher) {
        this.interviewRepository = interviewRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<Interview> getInterviews(Long userId, String status, LocalDate from, LocalDate to,
                                         String company, String sort, String order) {
        
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(direction, sort);
        
        List<Interview> interviews = interviewRepository.findByUserId(userId, sortObj);

        InterviewStatus interviewStatus = status == null ? null : InterviewStatus.fromString(status);
        
        return interviews.stream()
                .filter(i -> status == null || i.getStatus() == interviewStatus)
                .filter(i -> from == null || !i.getDate().isBefore(from))
                .filter(i -> to == null || !i.getDate().isAfter(to))
                .filter(i -> company == null || i.getCompanyName().toLowerCase().contains(company.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Interview createInterview(InterviewRequest interviewRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Interview interview = new Interview();
        updateInterviewFromRequest(interview, interviewRequest);
        interview.setUser(user);
        
        Interview savedInterview = interviewRepository.save(interview);
        
        if (user.getPreferences() != null && user.getPreferences().isEmailNotifications()) {
            eventPublisher.publishEvent(new InterviewEvent(savedInterview, user.getEmail(), "created"));
            log.debug("Published interview creation event for user: {}", user.getEmail());
        }
        
        return savedInterview;
    }

    public Interview getInterviewById(Long interviewId, Long userId) {
        return interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", interviewId));
    }

    @Transactional
    public Interview updateInterview(Long interviewId, InterviewRequest interviewRequest, Long userId) {
        Interview interview = getInterviewById(interviewId, userId);
        User user = interview.getUser();
        
        InterviewStatus oldStatus = interview.getStatus();
        
        updateInterviewFromRequest(interview, interviewRequest);
        Interview updatedInterview = interviewRepository.save(interview);
        
        if (user.getPreferences() != null && user.getPreferences().isEmailNotifications()) {
            try {
                if (oldStatus != updatedInterview.getStatus()) {
                    eventPublisher.publishEvent(new InterviewEvent(updatedInterview, user.getEmail(), "status_changed"));
                    log.debug("Published interview status change event for user: {}", user.getEmail());
                } else {
                    eventPublisher.publishEvent(new InterviewEvent(updatedInterview, user.getEmail(), "updated"));
                    log.debug("Published interview update event for user: {}", user.getEmail());
                }
            } catch (Exception e) {
                log.error("Failed to publish interview update event", e);
            }
        }
        
        return updatedInterview;
    }

    @Transactional
    public Interview updateInterviewStatus(Long interviewId, InterviewStatus status, Long userId) {
        Interview interview = getInterviewById(interviewId, userId);
        User user = interview.getUser();
        
        boolean statusChanged = interview.getStatus() != status;
        
        interview.setStatus(status);
        Interview updatedInterview = interviewRepository.save(interview);
        
        if (statusChanged && user.getPreferences() != null && user.getPreferences().isEmailNotifications()) {
            try {
                eventPublisher.publishEvent(new InterviewEvent(updatedInterview, user.getEmail(), "status_changed"));
                log.debug("Published interview status change event for user: {}", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to publish interview status change event", e);
            }
        }
        
        return updatedInterview;
    }

    @Transactional
    public void deleteInterview(Long interviewId, Long userId) {
        Interview interview = getInterviewById(interviewId, userId);
        User user = interview.getUser();
        
        Interview interviewCopy = new Interview();
        interviewCopy.setId(interview.getId());
        interviewCopy.setCompanyName(interview.getCompanyName());
        interviewCopy.setPosition(interview.getPosition());
        interviewCopy.setDate(interview.getDate());
        interviewCopy.setTime(interview.getTime());
        interviewCopy.setType(interview.getType());
        interviewCopy.setStatus(interview.getStatus());
        interviewCopy.setLocation(interview.getLocation());
        
        interviewRepository.delete(interview);
        
        if (user.getPreferences() != null && user.getPreferences().isEmailNotifications()) {
            try {
                eventPublisher.publishEvent(new InterviewEvent(interviewCopy, user.getEmail(), "deleted"));
                log.debug("Published interview deletion event for user: {}", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to publish interview deletion event", e);
            }
        }
    }

    private void updateInterviewFromRequest(Interview interview, InterviewRequest request) {
        interview.setCompanyName(request.getCompanyName());
        interview.setPosition(request.getPosition());
        interview.setDate(request.getDate());
        interview.setTime(request.getTime());

        if(request.getStatus() != null) {
            InterviewStatus status = InterviewStatus.fromString(request.getStatus());
            interview.setStatus(status);
        }

        if(request.getType() != null) {
            InterviewType type = InterviewType.fromString(request.getType());
            interview.setType(type);
        }

        interview.setLocation(request.getLocation());
        interview.setNotes(request.getNotes());
        
        ContactPerson contactPerson = new ContactPerson();
        contactPerson.setName(request.getContactName());
        contactPerson.setPosition(request.getContactPosition());
        contactPerson.setEmail(request.getContactEmail());
        contactPerson.setPhone(request.getContactPhone());
        interview.setContactPerson(contactPerson);
        
        interview.setRating(request.getRating());
        interview.setFeedback(request.getFeedback());
    }
}
