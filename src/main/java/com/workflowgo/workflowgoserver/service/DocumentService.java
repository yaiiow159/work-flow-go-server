package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.exception.FileStorageException;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.model.enums.DocumentType;
import com.workflowgo.workflowgoserver.repository.DocumentRepository;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public DocumentService(DocumentRepository documentRepository, 
                          UserRepository userRepository,
                          EmailService emailService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Set<Document> getAllDocumentsByUser(Long userId) {
        return documentRepository.findByUserId(userId);
    }

    @Transactional
    public Document storeDocument(MultipartFile file, String name, String type, Long userId) {
        try {
            Document document = new Document();
            document.setName(name);
            document.setType(DocumentType.valueOf(type.toUpperCase()));

            String fileUrl = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            document.setUrl(fileUrl);
            document.setContentType(file.getContentType());
            document.setSize(file.getSize());
            
            byte[] fileContent = file.getBytes();
            document.setContent(fileContent);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            document.setUser(user);

            Document savedDocument = documentRepository.save(document);

            if (user.getPreferences().isNotificationsEnabled()) {
                emailService.sendDocumentNotification(user.getEmail(), savedDocument, "uploaded");
            }

            return savedDocument;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file", e);
        }
    }

    @Transactional
    public Document getDocumentById(Long documentId, Long userId) {
        return documentRepository.findByIdAndUserId(documentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));
    }

    @Transactional
    public Document updateDocument(Long documentId, String name, MultipartFile file, Long userId) {
        Document document = getDocumentById(documentId, userId);
        document.setName(name);

        if (file != null && !file.isEmpty()) {
            try {
                byte[] fileContent = file.getBytes();
                document.setContent(fileContent);

                String fileUrl = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                document.setUrl(fileUrl);
                document.setContentType(file.getContentType());
                document.setSize(file.getSize());
            } catch (IOException e) {
                throw new FileStorageException("Failed to update file", e);
            }
        }

        Document updatedDocument = documentRepository.save(document);

        User user = document.getUser();
        if (user.getPreferences().isNotificationsEnabled()) {
            emailService.sendDocumentNotification(user.getEmail(), updatedDocument, "updated");
        }

        return updatedDocument;
    }

    @Transactional
    public void deleteDocument(Long documentId, Long userId) {
        Document document = getDocumentById(documentId, userId);
        User user = document.getUser();

        Document documentCopy = new Document();
        documentCopy.setId(document.getId());
        documentCopy.setName(document.getName());
        documentCopy.setType(document.getType());
        documentCopy.setSize(document.getSize());
        documentCopy.setCreatedAt(document.getCreatedAt());

        documentRepository.delete(document);

        if (user.getPreferences().isNotificationsEnabled()) {
            emailService.sendDocumentNotification(user.getEmail(), documentCopy, "deleted");
        }
    }

    @Transactional
    public long getDocumentCount(Long userId) {
        return documentRepository.countByUserId(userId);
    }

    @Transactional
    public byte[] getDocumentContent(Long documentId, Long userId) {
        Document document = getDocumentById(documentId, userId);
        return document.getContent();
    }
}
