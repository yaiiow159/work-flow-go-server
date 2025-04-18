package com.workflowgo.workflowgoserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reminders")
public class Reminder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;
    
    @NotNull(message = "Reminder time is required")
    private LocalDateTime time;
    
    @NotBlank(message = "Message is required")
    @Column(columnDefinition = "TEXT")
    private String message;
    
    private boolean isCompleted;
}
