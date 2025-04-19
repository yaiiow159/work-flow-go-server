package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.DocumentDTO;
import com.workflowgo.workflowgoserver.model.enums.DocumentType;
import com.workflowgo.workflowgoserver.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Documents", description = "Document management endpoints")
public class DocumentController {
    
    private final DocumentService documentService;
    
    @GetMapping
    @Operation(summary = "Get all documents", description = "Retrieve all documents")
    public ResponseEntity<List<DocumentDTO>> getAllDocuments(
            @RequestParam(value = "interviewId", required = false) UUID interviewId,
            @RequestParam(value = "userId", required = false) UUID userId) {
        
        List<DocumentDTO> documentDTOs;
        if (interviewId != null) {
            log.debug("Fetching documents for interview ID: {}", interviewId);
            documentDTOs = documentService.getDocumentsByInterviewId(interviewId);
        } else if (userId != null) {
            log.debug("Fetching documents for user ID: {}", userId);
            documentDTOs = documentService.getDocumentsByUserId(userId);
        } else {
            log.debug("Fetching all documents");
            documentDTOs = documentService.getAllDocuments();
        }
        
        return ResponseEntity.ok(documentDTOs);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Get document metadata by ID")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable UUID id) {
        log.debug("Fetching document with ID: {}", id);
        DocumentDTO documentDTO = documentService.getDocumentById(id);
        return ResponseEntity.ok(documentDTO);
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document", description = "Upload a new document")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") DocumentType type,
            @RequestParam(value = "interviewId", required = false) UUID interviewId,
            @RequestParam(value = "userId", required = false) UUID userId) {
        
        log.debug("Uploading document: {}, type: {}, interviewId: {}, userId: {}", 
                name, type, interviewId, userId);
        
        DocumentDTO uploadedDocument = documentService.uploadDocument(file, name, type, interviewId, userId);
        return new ResponseEntity<>(uploadedDocument, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Delete a document")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        log.debug("Deleting document with ID: {}", id);
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
