package com.workflowgo.workflowgoserver.service.impl;

import com.workflowgo.workflowgoserver.dto.QuestionDTO;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.Question;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import com.workflowgo.workflowgoserver.repository.QuestionRepository;
import com.workflowgo.workflowgoserver.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final InterviewRepository interviewRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<QuestionDTO> getAllQuestions() {
        log.debug("Fetching all questions");
        return questionRepository.findAll().stream()
                .map(QuestionDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public QuestionDTO getQuestionById(UUID id) {
        log.debug("Fetching question with id: {}", id);
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        return QuestionDTO.fromEntity(question);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestionsByInterviewId(UUID interviewId) {
        log.debug("Fetching questions for interview with id: {}", interviewId);
        return questionRepository.findByInterviewId(interviewId).stream()
                .map(QuestionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestionsByUserId(UUID userId) {
        log.debug("Fetching questions for user with id: {}", userId);
        return questionRepository.findByInterviewUserId(userId).stream()
                .map(QuestionDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<QuestionDTO> getImportantQuestions() {
        log.debug("Fetching important questions");
        return questionRepository.findByIsImportantTrue().stream()
                .map(QuestionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuestionDTO createQuestionFromDTO(QuestionDTO questionDTO) {
        log.debug("Creating new question from DTO for interview: {}", questionDTO.getInterviewId());

        UUID interviewId = questionDTO.getInterviewId();
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));

        Question question = new Question();
        question.setQuestion(questionDTO.getQuestion());
        question.setAnswer(questionDTO.getAnswer());
        question.setCategory(questionDTO.getCategory());
        question.setImportant(questionDTO.isImportant());
        question.setInterview(interview);

        Question savedQuestion = questionRepository.save(question);
        return QuestionDTO.fromEntity(savedQuestion);
    }
    
    @Override
    @Transactional
    public QuestionDTO updateQuestionFromDTO(UUID id, QuestionDTO questionDTO) {
        log.debug("Updating question from DTO with id: {}", id);
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        question.setQuestion(questionDTO.getQuestion());
        question.setAnswer(questionDTO.getAnswer());
        question.setCategory(questionDTO.getCategory());
        question.setImportant(questionDTO.isImportant());

        if (questionDTO.getInterviewId() != null &&
                !question.getInterview().getId().equals(questionDTO.getInterviewId())) {
            UUID interviewId = questionDTO.getInterviewId();
            Interview interview = interviewRepository.findById(interviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));
            question.setInterview(interview);
        }

        Question updatedQuestion = questionRepository.save(question);
        return QuestionDTO.fromEntity(updatedQuestion);
    }
    
    @Override
    @Transactional
    public QuestionDTO toggleImportance(UUID id) {
        log.debug("Toggling importance for question with id: {}", id);
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        question.setImportant(!question.isImportant());
        Question updatedQuestion = questionRepository.save(question);
        return QuestionDTO.fromEntity(updatedQuestion);
    }
    
    @Override
    @Transactional
    public void deleteQuestion(UUID id) {
        log.debug("Deleting question with id: {}", id);
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        questionRepository.delete(question);
    }
}
