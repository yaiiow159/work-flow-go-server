package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.Question;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import com.workflowgo.workflowgoserver.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final InterviewRepository interviewRepository;

    @Transactional(readOnly = true)
    public List<Question> getAllQuestions() {
        log.debug("Fetching all questions");
        return questionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Question getQuestionById(UUID id) {
        log.debug("Fetching question with id: {}", id);
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionsByInterviewId(UUID interviewId) {
        log.debug("Fetching questions for interview with id: {}", interviewId);
        return questionRepository.findByInterviewId(interviewId);
    }

    @Transactional(readOnly = true)
    public List<Question> getImportantQuestions() {
        log.debug("Fetching important questions");
        return questionRepository.findByIsImportantTrue();
    }

    @Transactional
    public Question createQuestion(Question question) {
        log.debug("Creating new question for interview: {}", question.getInterview().getId());
        
        UUID interviewId = question.getInterview().getId();
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));
        
        question.setInterview(interview);
        return questionRepository.save(question);
    }

    @Transactional
    public Question updateQuestion(UUID id, Question questionDetails) {
        log.debug("Updating question with id: {}", id);
        Question question = getQuestionById(id);
        
        question.setQuestion(questionDetails.getQuestion());
        question.setAnswer(questionDetails.getAnswer());
        question.setImportant(questionDetails.isImportant());
        
        if (questionDetails.getInterview() != null && 
                !question.getInterview().getId().equals(questionDetails.getInterview().getId())) {
            UUID interviewId = questionDetails.getInterview().getId();
            Interview interview = interviewRepository.findById(interviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));
            question.setInterview(interview);
        }
        
        return questionRepository.save(question);
    }

    @Transactional
    public Question toggleImportance(UUID id) {
        log.debug("Toggling importance for question with id: {}", id);
        Question question = getQuestionById(id);
        question.setImportant(!question.isImportant());
        return questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(UUID id) {
        log.debug("Deleting question with id: {}", id);
        Question question = getQuestionById(id);
        questionRepository.delete(question);
    }
}
