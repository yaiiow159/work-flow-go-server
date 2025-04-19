package com.workflowgo.workflowgoserver.service.impl;

import com.workflowgo.workflowgoserver.dto.DocumentDTO;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.enums.DocumentType;
import com.workflowgo.workflowgoserver.repository.DocumentRepository;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import com.workflowgo.workflowgoserver.service.CloudinaryService;
import com.workflowgo.workflowgoserver.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {
    
    private final DocumentRepository documentRepository;
    private final InterviewRepository interviewRepository;
    private final CloudinaryService cloudinaryService;
    
    @Override
    @Transactional(readOnly = true)
    public List<DocumentDTO> getAllDocuments() {
        log.debug("Fetching all documents");
        return documentRepository.findAll().stream()
                .map(DocumentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public DocumentDTO getDocumentById(UUID id) {
        log.debug("Fetching document with id: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        return DocumentDTO.fromEntity(document);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DocumentDTO> getDocumentsByInterviewId(UUID interviewId) {
        log.debug("Fetching documents for interview with id: {}", interviewId);
        return documentRepository.findByInterviewId(interviewId).stream()
                .map(DocumentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DocumentDTO> getDocumentsByUserId(UUID userId) {
        log.debug("Fetching documents for user with id: {}", userId);
        return documentRepository.findByInterviewUserId(userId).stream()
                .map(DocumentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public DocumentDTO uploadDocument(MultipartFile file, String name, DocumentType type, UUID interviewId, UUID userId) {
        log.debug("Uploading document: {}, type: {}, interviewId: {}, userId: {}", name, type, interviewId, userId);
        
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
        
        Document savedDocument = documentRepository.save(document);
        return DocumentDTO.fromEntity(savedDocument);
    }
    
    @Override
    @Transactional
    public void deleteDocument(UUID id) {
        log.debug("Deleting document with id: {}", id);
        
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        
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

            return getString(url, uploadIndex);
        } catch (Exception e) {
            log.error("Error extracting public ID from URL: {}", url, e);
            return null;
        }
    }

    private static String getString(String url, int uploadIndex) {
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
