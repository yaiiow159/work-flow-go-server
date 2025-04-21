package com.workflowgo.workflowgoserver.controller;

import com.workflowgo.workflowgoserver.dto.DocumentDTO;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.payload.ApiResponse;
import com.workflowgo.workflowgoserver.security.CurrentUser;
import com.workflowgo.workflowgoserver.security.UserPrincipal;
import com.workflowgo.workflowgoserver.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

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
    public ResponseEntity<List<DocumentDTO>> getAllDocuments(@CurrentUser UserPrincipal currentUser) {
        List<Document> documents = documentService.getAllDocumentsByUser(currentUser.getId());
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

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(document.getId()).toUri();

        return ResponseEntity.created(location)
                .body(documentDTO);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        Document document = documentService.getDocumentById(id, currentUser.getId());
        return ResponseEntity.ok(DocumentDTO.fromDocument(document));
    }
    
    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id, @CurrentUser UserPrincipal currentUser, HttpServletRequest request) throws IOException {
        Resource resource = documentService.loadDocumentAsResource(id, currentUser.getId());
        
        String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        
        Document document = documentService.getDocumentById(id, currentUser.getId());
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
                .body(resource);
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
    public ResponseEntity<Resource> viewDocument(
            @PathVariable Long id,
            @CurrentUser UserPrincipal currentUser,
            HttpServletRequest request) {
        
        Resource resource = documentService.loadDocumentAsResource(id, currentUser.getId());

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Failed to determine file type", ex);
            contentType = "application/octet-stream";
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline", "filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
