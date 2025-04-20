package com.workflowgo.workflowgoserver.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailVerificationRequest {
    @NotBlank(message = "Email is required")
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email is not valid")
    private String email;
}
