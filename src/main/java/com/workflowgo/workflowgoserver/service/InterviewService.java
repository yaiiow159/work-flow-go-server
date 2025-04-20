package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.ContactPerson;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import com.workflowgo.workflowgoserver.payload.InterviewRequest;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;

    private final UserRepository userRepository;

    public InterviewService(InterviewRepository interviewRepository, UserRepository userRepository) {
        this.interviewRepository = interviewRepository;
        this.userRepository = userRepository;
    }

    public List<Interview> getInterviews(Long userId, InterviewStatus status, LocalDate from, LocalDate to,
                                         String company, String sort, String order) {
        
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(direction, sort);
        
        List<Interview> interviews = interviewRepository.findByUserId(userId, sortObj);
        
        return interviews.stream()
                .filter(i -> status == null || i.getStatus() == status)
                .filter(i -> from == null || !i.getDate().isBefore(from))
                .filter(i -> to == null || !i.getDate().isAfter(to))
                .filter(i -> company == null || i.getCompanyName().toLowerCase().contains(company.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Interview createInterview(InterviewRequest interviewRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Interview interview = new Interview();
        updateInterviewFromRequest(interview, interviewRequest);
        interview.setUser(user);
        
        return interviewRepository.save(interview);
    }

    public Interview getInterviewById(Long interviewId, Long userId) {
        return interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", interviewId));
    }

    public Interview updateInterview(Long interviewId, InterviewRequest interviewRequest, Long userId) {
        Interview interview = getInterviewById(interviewId, userId);
        updateInterviewFromRequest(interview, interviewRequest);
        return interviewRepository.save(interview);
    }

    public Interview updateInterviewStatus(Long interviewId, InterviewStatus status, Long userId) {
        Interview interview = getInterviewById(interviewId, userId);
        interview.setStatus(status);
        return interviewRepository.save(interview);
    }

    public void deleteInterview(Long interviewId, Long userId) {
        Interview interview = getInterviewById(interviewId, userId);
        interviewRepository.delete(interview);
    }

    private void updateInterviewFromRequest(Interview interview, InterviewRequest request) {
        interview.setCompanyName(request.getCompanyName());
        interview.setPosition(request.getPosition());
        interview.setDate(request.getDate());
        interview.setTime(request.getTime());
        interview.setType(request.getType());
        interview.setStatus(request.getStatus());
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
