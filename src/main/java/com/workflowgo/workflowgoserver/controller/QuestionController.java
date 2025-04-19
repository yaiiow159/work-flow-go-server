package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.QuestionDTO;
import com.workflowgo.workflowgoserver.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Questions", description = "Question management endpoints")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    @Operation(summary = "Get all questions", description = "Retrieve all questions")
    public ResponseEntity<List<QuestionDTO>> getAllQuestions(
            @RequestParam(value = "interviewId", required = false) UUID interviewId,
            @RequestParam(value = "userId", required = false) UUID userId,
            @RequestParam(value = "important", required = false) Boolean important) {
        
        List<QuestionDTO> questionDTOs;
        if (interviewId != null) {
            log.debug("Fetching questions for interview ID: {}", interviewId);
            questionDTOs = questionService.getQuestionsByInterviewId(interviewId);
        } else if (userId != null) {
            log.debug("Fetching questions for user ID: {}", userId);
            questionDTOs = questionService.getQuestionsByUserId(userId);
        } else if (important != null && important) {
            log.debug("Fetching important questions");
            questionDTOs = questionService.getImportantQuestions();
        } else {
            log.debug("Fetching all questions");
            questionDTOs = questionService.getAllQuestions();
        }
        
        return ResponseEntity.ok(questionDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID", description = "Retrieve a specific question by ID")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable UUID id) {
        log.debug("Fetching question with ID: {}", id);
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        return ResponseEntity.ok(questionDTO);
    }

    @PostMapping
    @Operation(summary = "Create question", description = "Create a new question")
    public ResponseEntity<QuestionDTO> createQuestion(@Valid @RequestBody QuestionDTO questionDTO) {
        log.debug("Creating new question for interview ID: {}", questionDTO.getInterviewId());
        QuestionDTO createdQuestion = questionService.createQuestionFromDTO(questionDTO);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update question", description = "Update an existing question")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @PathVariable UUID id,
            @Valid @RequestBody QuestionDTO questionDTO) {
        
        log.debug("Updating question with ID: {}", id);
        QuestionDTO updatedQuestion = questionService.updateQuestionFromDTO(id, questionDTO);
        return ResponseEntity.ok(updatedQuestion);
    }

    @PatchMapping("/{id}/toggle-importance")
    @Operation(summary = "Toggle question importance", description = "Toggle the importance flag of a question")
    public ResponseEntity<QuestionDTO> toggleQuestionImportance(@PathVariable UUID id) {
        log.debug("Toggling importance for question with ID: {}", id);
        QuestionDTO updatedQuestion = questionService.toggleImportance(id);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete question", description = "Delete a question")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        log.debug("Deleting question with ID: {}", id);
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
