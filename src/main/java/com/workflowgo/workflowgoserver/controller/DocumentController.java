package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.DocumentDTO;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.enums.DocumentType;
import com.workflowgo.workflowgoserver.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Document management endpoints")
public class DocumentController {
    
    private final DocumentService documentService;
    
    @GetMapping
    @Operation(summary = "Get all documents", description = "Retrieve all documents")
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        List<DocumentDTO> documents = documentService.getAllDocuments().stream()
                .map(DocumentDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Get document metadata by ID")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable UUID id) {
        Document document = documentService.getDocumentById(id);
        return ResponseEntity.ok(DocumentDTO.fromEntity(document));
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document", description = "Upload a new document")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") DocumentType type,
            @RequestParam(value = "interviewId", required = false) UUID interviewId) {
        
        Document document = documentService.uploadDocument(file, name, type, interviewId);
        return new ResponseEntity<>(DocumentDTO.fromEntity(document), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Delete a document")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
