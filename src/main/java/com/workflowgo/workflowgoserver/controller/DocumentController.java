package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.DocumentDTO;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.payload.ApiResponse;
import com.workflowgo.workflowgoserver.security.CurrentUser;
import com.workflowgo.workflowgoserver.security.UserPrincipal;
import com.workflowgo.workflowgoserver.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Set<DocumentDTO>> getAllDocuments(@CurrentUser UserPrincipal currentUser) {
        Set<Document> documents = documentService.getAllDocumentsByUser(currentUser.getId());
        return ResponseEntity.ok(DocumentDTO.fromDocuments(documents));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadDocumentFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @CurrentUser UserPrincipal currentUser) {
        
        Document document = documentService.storeDocument(file, name, type, currentUser.getId());
        DocumentDTO documentDTO = DocumentDTO.fromDocument(document);

        return ResponseEntity.ok(documentDTO);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        Document document = documentService.getDocumentById(id, currentUser.getId());
        return ResponseEntity.ok(DocumentDTO.fromDocument(document));
    }
    
    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> downloadDocument(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        try {
            Document document = documentService.getDocumentById(id, currentUser.getId());
            byte[] fileContent = documentService.getDocumentContent(id, currentUser.getId());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getContentType()))
                    .contentLength(fileContent.length)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
                    .body(fileContent);
        } catch (Exception e) {
            log.error("Error downloading document: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error downloading document: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/file")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getDocumentFile(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        try {
            Document document = documentService.getDocumentById(id, currentUser.getId());
            byte[] fileContent = documentService.getDocumentContent(id, currentUser.getId());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getContentType()))
                    .contentLength(fileContent.length)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getName() + "\"")
                    .body(fileContent);
        } catch (Exception e) {
            log.error("Error retrieving document file: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving document: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        documentService.deleteDocument(id, currentUser.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Document deleted successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DocumentDTO> updateDocument(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @CurrentUser UserPrincipal currentUser) {
        
        Document document = documentService.updateDocument(id, name, file, currentUser.getId());
        return ResponseEntity.ok(DocumentDTO.fromDocument(document));
    }
    
    @GetMapping("/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Long> getDocumentCount(@CurrentUser UserPrincipal currentUser) {
        long count = documentService.getDocumentCount(currentUser.getId());
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/{id}/view")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> viewDocument(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        Document document = documentService.getDocumentById(id, currentUser.getId());
        byte[] fileContent = documentService.getDocumentContent(id, currentUser.getId());
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getName() + "\"")
                .body(fileContent);
    }
}
