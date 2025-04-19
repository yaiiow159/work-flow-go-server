package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.enums.DocumentType;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.repository.DocumentRepository;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@Slf4j
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    
    @Autowired
    public DocumentService(DocumentRepository documentRepository, 
                          UserRepository userRepository,
                          CloudinaryService cloudinaryService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public List<Document> getAllDocumentsByUser(Long userId) {
        return documentRepository.findByUserId(userId);
    }

    public Document storeDocument(MultipartFile file, String name, String type, Long userId) {
        String fileUrl = cloudinaryService.uploadFile(file);
        
        Document document = new Document();
        document.setName(name);
        document.setType(DocumentType.valueOf(type.toUpperCase()));
        document.setUrl(fileUrl);
        document.setContentType(file.getContentType());
        document.setSize(file.getSize());
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        document.setUser(user);
        
        return documentRepository.save(document);
    }

    public Document getDocumentById(Long documentId, Long userId) {
        return documentRepository.findByIdAndUserId(documentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));
    }

    public Document updateDocument(Long documentId, String name, MultipartFile file, Long userId) {
        Document document = getDocumentById(documentId, userId);
        document.setName(name);
        
        if (file != null && !file.isEmpty()) {
            // Delete old file if it exists
            String publicId = extractPublicIdFromUrl(document.getUrl());
            if (publicId != null) {
                cloudinaryService.deleteFile(publicId);
            }
            
            // Upload new file
            String fileUrl = cloudinaryService.uploadFile(file);
            document.setUrl(fileUrl);
            document.setContentType(file.getContentType());
            document.setSize(file.getSize());
        }
        
        return documentRepository.save(document);
    }

    public void deleteDocument(Long documentId, Long userId) {
        Document document = getDocumentById(documentId, userId);
        
        // Extract public_id from Cloudinary URL
        String publicId = extractPublicIdFromUrl(document.getUrl());
        if (publicId != null) {
            cloudinaryService.deleteFile(publicId);
        }
        
        // Delete from database
        documentRepository.delete(document);
    }

    public Resource loadDocumentAsResource(Long documentId, Long userId) {
        try {
            Document document = getDocumentById(documentId, userId);
            return new UrlResource(document.getUrl());
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File", "id", documentId);
        }
    }
    
    public long getDocumentCount(Long userId) {
        return documentRepository.countByUserId(userId);
    }
    
    private String extractPublicIdFromUrl(String url) {
        try {
            if (url != null && url.contains("/upload/")) {
                String afterUpload = url.split("/upload/")[1];
                if (afterUpload.startsWith("v")) {
                    afterUpload = afterUpload.replaceFirst("v\\d+/", "");
                }
                return afterUpload.substring(0, afterUpload.lastIndexOf("."));
            }
        } catch (Exception e) {
            log.error("Failed to extract public_id from Cloudinary URL", e);
        }
        return null;
    }
}
