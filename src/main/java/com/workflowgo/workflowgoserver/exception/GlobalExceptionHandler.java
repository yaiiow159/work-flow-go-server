package com.workflowgo.workflowgoserver.exception;

import com.workflowgo.workflowgoserver.payload.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("Resource not found exception", ex);
        return new ApiResponse(false, ex.getMessage());
    }

    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleFileStorageException(FileStorageException ex, WebRequest request) {
        logger.error("File storage exception", ex);
        return new ApiResponse(false, ex.getMessage());
    }

    @ExceptionHandler(OAuth2AuthenticationProcessingException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse handleOAuth2AuthenticationProcessingException(OAuth2AuthenticationProcessingException ex, WebRequest request) {
        logger.error("OAuth2 authentication processing exception", ex);
        return new ApiResponse(false, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        logger.error("Authentication exception", ex);
        return new ApiResponse(false, "Authentication failed: " + ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("Access denied exception", ex);
        return new ApiResponse(false, "Access denied: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Global exception handler caught: ", ex);
        return new ApiResponse(false, "An unexpected error occurred: " + ex.getMessage());
    }
}
