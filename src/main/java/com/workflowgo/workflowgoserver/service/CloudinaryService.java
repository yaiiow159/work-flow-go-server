package com.workflowgo.workflowgoserver.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public String uploadFile(MultipartFile file) {
        try {
            String publicId = "work-flow-go/" + UUID.randomUUID().toString();
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "auto"
                    )
            );
            
            log.info("File uploaded successfully to Cloudinary: {}", uploadResult.get("url"));
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary", e);
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }
    
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("File deleted successfully from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Error deleting file from Cloudinary", e);
            throw new RuntimeException("Failed to delete file from Cloudinary", e);
        }
    }
}
