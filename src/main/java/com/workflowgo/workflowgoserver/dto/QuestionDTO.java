package com.workflowgo.workflowgoserver.dto;

import com.workflowgo.workflowgoserver.model.Question;
import com.workflowgo.workflowgoserver.model.enums.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    
    private UUID id;
    private String question;
    private String answer;
    private QuestionCategory category;
    private boolean important;
    private UUID interviewId;
    
    public static QuestionDTO fromEntity(Question question) {
        return QuestionDTO.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .answer(question.getAnswer())
                .category(question.getCategory())
                .important(question.isImportant())
                .interviewId(question.getInterview() != null ? question.getInterview().getId() : null)
                .build();
    }
}
