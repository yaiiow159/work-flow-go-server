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
            Map<?,?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto"
                    )
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new FileStorageException("Failed to upload file to Cloudinary", e);
        }
    }

    public void deleteFile(String fileUrl) {
        String publicId = extractPublicIdFromUrl(fileUrl);
        if (publicId == null) {
            throw new FileStorageException("Could not extract public ID from URL: " + fileUrl);
        }
        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
            );
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file from Cloudinary", e);
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
