package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.Question;
import com.workflowgo.workflowgoserver.model.enums.QuestionCategory;
import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String question;
    private String answer;
    private QuestionCategory category;
    private boolean important;
    
    public static QuestionDTO fromQuestion(Question question) {
        if (question == null) {
            return null;
        }
        
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setQuestion(question.getQuestion());
        dto.setAnswer(question.getAnswer());
        dto.setCategory(question.getCategory());
        dto.setImportant(question.isImportant());
        return dto;
    }
}
