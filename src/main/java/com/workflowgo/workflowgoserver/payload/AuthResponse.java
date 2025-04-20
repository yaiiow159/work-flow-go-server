package com.workflowgo.workflowgoserver.payload;

import com.workflowgo.workflowgoserver.dto.AuthDTO;
import lombok.Data;

@Data
public class AuthResponse {
    private AuthDTO user;
    private String token;
    private String tokenType = "Bearer";

    public AuthResponse(AuthDTO user, String token) {
        this.user = user;
        this.token = token;
    }
}
