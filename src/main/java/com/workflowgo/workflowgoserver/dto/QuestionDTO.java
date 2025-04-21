package com.workflowgo.workflowgoserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.workflowgo.workflowgoserver.model.Question;
import com.workflowgo.workflowgoserver.model.enums.QuestionCategory;
import lombok.Data;

@Data
public class QuestionDTO {
    private String id;
    private String question;
    private String answer;
    private QuestionCategory category;
    @JsonProperty("isImportant")
    private boolean important;
    
    public static QuestionDTO fromQuestion(Question question) {
        if (question == null) {
            return null;
        }
        
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId() != null ? question.getId().toString() : null);
        dto.setQuestion(question.getQuestion());
        dto.setAnswer(question.getAnswer());
        dto.setCategory(question.getCategory());
        dto.setImportant(question.isImportant());
        return dto;
    }

    public static Question toQuestion(QuestionDTO dto) {
        if (dto == null) {
            return null;
        }

        Question question = new Question();
        if (dto.getId() != null && !dto.getId().isEmpty()) {
            try {
                question.setId(Long.parseLong(dto.getId()));
            } catch (NumberFormatException e) {
                question.setId(null);
            }
        }
        question.setQuestion(dto.getQuestion());
        question.setAnswer(dto.getAnswer());
        question.setCategory(dto.getCategory());
        question.setImportant(dto.isImportant());
        return question;
    }
}
