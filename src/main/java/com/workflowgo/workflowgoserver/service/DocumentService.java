package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.enums.DocumentType;
import com.workflowgo.workflowgoserver.repository.DocumentRepository;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    private final InterviewRepository interviewRepository;
    private final CloudinaryService cloudinaryService;
    
    @Transactional(readOnly = true)
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Document getDocumentById(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
    }
    
    @Transactional
    public Document uploadDocument(MultipartFile file, String name, DocumentType type, UUID interviewId) {
        String url = cloudinaryService.uploadFile(file);
        
        Document document = Document.builder()
                .name(name)
                .type(type)
                .url(url)
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();
        
        if (interviewId != null) {
            Interview interview = interviewRepository.findById(interviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));
            document.setInterview(interview);
        }
        
        return documentRepository.save(document);
    }
    
    @Transactional
    public void deleteDocument(UUID id) {
        Document document = getDocumentById(id);
        
        String url = document.getUrl();
        String publicId = extractPublicIdFromUrl(url);
        
        if (publicId != null) {
            cloudinaryService.deleteFile(publicId);
        }
        
        documentRepository.delete(document);
    }
    
    private String extractPublicIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        try {
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null;
            }

            return getUpload(url, uploadIndex);
        } catch (Exception e) {
            log.error("Error extracting public ID from URL: {}", url, e);
            return null;
        }
    }

    private static String getUpload(String url, int uploadIndex) {
        String afterUpload = url.substring(uploadIndex + 8);

        if (afterUpload.startsWith("v")) {
            int versionEndIndex = afterUpload.indexOf("/");
            if (versionEndIndex != -1) {
                afterUpload = afterUpload.substring(versionEndIndex + 1);
            }
        }

        int extensionIndex = afterUpload.lastIndexOf(".");
        if (extensionIndex != -1) {
            afterUpload = afterUpload.substring(0, extensionIndex);
        }
        return afterUpload;
    }
}
