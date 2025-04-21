package com.workflowgo.workflowgoserver.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpRequest {
    @NotBlank(message = "Name is required")
    @JsonProperty("displayName")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",message = "Email is not valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

}
