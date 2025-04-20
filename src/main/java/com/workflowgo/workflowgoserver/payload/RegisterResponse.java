package com.workflowgo.workflowgoserver.payload;

import com.workflowgo.workflowgoserver.dto.AuthDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private boolean success;
    private String message;
    private AuthDTO user;
    private String token;
}
