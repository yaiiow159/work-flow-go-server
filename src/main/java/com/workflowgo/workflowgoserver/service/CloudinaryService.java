package com.workflowgo.workflowgoserver.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.workflowgo.workflowgoserver.exception.FileStorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private static final Pattern PUBLIC_ID_PATTERN = Pattern.compile("/([^/]+)\\.[^/\\.]+$");

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file) {
        try {
            String resourceType = determineResourceType(file.getContentType());
            
            Map<?,?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", resourceType
                    )
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new FileStorageException("Failed to upload file to Cloudinary", e);
        }
    }

    public void deleteFile(String fileUrl, String contentType) {
        String publicId = extractPublicIdFromUrl(fileUrl);
        if (publicId == null) {
            throw new FileStorageException("Could not extract public ID from URL: " + fileUrl);
        }
        
        String resourceType = determineResourceType(contentType);
        
        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", resourceType)
            );
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file from Cloudinary", e);
        }
    }

    private String determineResourceType(String contentType) {
        if (contentType == null) {
            return "auto";
        }
        
        if (contentType.startsWith("image/")) {
            return "image";
        } else if (contentType.startsWith("video/")) {
            return "video";
        } else if (contentType.startsWith("audio/")) {
            return "video";
        } else {
            return "raw";
        }
    }

    private String extractPublicIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        Matcher matcher = PUBLIC_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
