package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.QuestionDTO;
import com.workflowgo.workflowgoserver.model.Question;
import com.workflowgo.workflowgoserver.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@Tag(name = "Questions", description = "Question management endpoints")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    @Operation(summary = "Get all questions", description = "Retrieve all questions")
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        List<QuestionDTO> questions = questionService.getAllQuestions().stream()
                .map(QuestionDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID", description = "Retrieve a specific question by ID")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable UUID id) {
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(QuestionDTO.fromEntity(question));
    }

    @GetMapping("/interview/{interviewId}")
    @Operation(summary = "Get questions by interview ID", description = "Retrieve all questions for a specific interview")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByInterviewId(@PathVariable UUID interviewId) {
        List<QuestionDTO> questions = questionService.getQuestionsByInterviewId(interviewId).stream()
                .map(QuestionDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/important")
    @Operation(summary = "Get important questions", description = "Retrieve all questions marked as important")
    public ResponseEntity<List<QuestionDTO>> getImportantQuestions() {
        List<QuestionDTO> questions = questionService.getImportantQuestions().stream()
                .map(QuestionDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(questions);
    }

    @PostMapping
    @Operation(summary = "Create question", description = "Create a new question")
    public ResponseEntity<QuestionDTO> createQuestion(@Valid @RequestBody Question question) {
        Question createdQuestion = questionService.createQuestion(question);
        return new ResponseEntity<>(QuestionDTO.fromEntity(createdQuestion), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update question", description = "Update an existing question")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @PathVariable UUID id,
            @Valid @RequestBody Question questionDetails) {
        
        Question updatedQuestion = questionService.updateQuestion(id, questionDetails);
        return ResponseEntity.ok(QuestionDTO.fromEntity(updatedQuestion));
    }

    @PatchMapping("/{id}/toggle-importance")
    @Operation(summary = "Toggle question importance", description = "Toggle the importance flag of a question")
    public ResponseEntity<QuestionDTO> toggleQuestionImportance(@PathVariable UUID id) {
        Question updatedQuestion = questionService.toggleImportance(id);
        return ResponseEntity.ok(QuestionDTO.fromEntity(updatedQuestion));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete question", description = "Delete a question")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
