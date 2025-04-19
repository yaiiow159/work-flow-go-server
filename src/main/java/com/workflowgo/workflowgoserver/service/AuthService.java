package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.AuthRequestDTO;
import com.workflowgo.workflowgoserver.dto.AuthResponseDTO;
import com.workflowgo.workflowgoserver.dto.RegisterRequestDTO;

public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(AuthRequestDTO request);

    boolean validateToken();
}
