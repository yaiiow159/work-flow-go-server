package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.QuestionDTO;

import java.util.List;
import java.util.UUID;

public interface QuestionService {
    
    List<QuestionDTO> getAllQuestions();
    
    QuestionDTO getQuestionById(UUID id);
    
    List<QuestionDTO> getQuestionsByInterviewId(UUID interviewId);
    
    List<QuestionDTO> getQuestionsByUserId(UUID userId);
    
    List<QuestionDTO> getImportantQuestions();

    QuestionDTO createQuestionFromDTO(QuestionDTO questionDTO);

    QuestionDTO updateQuestionFromDTO(UUID id, QuestionDTO questionDTO);
    
    QuestionDTO toggleImportance(UUID id);
    
    void deleteQuestion(UUID id);
}
