package com.workflowgo.workflowgoserver.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.workflowgo.workflowgoserver.exception.FileStorageException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private static final Pattern PUBLIC_ID_PATTERN =
            Pattern.compile(".*/upload/(?:raw/)?v\\d+/(.+?)\\.[^/.]+$");

    private static final String[] OFFICE_EXT = {
            "doc", "docx", "ppt", "pptx", "xls", "xlsx"
    };

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            String resourceType = determineResourceType(contentType);

            String original = file.getOriginalFilename();
            String baseName = FilenameUtils.getBaseName(original);
            String ext = Objects.requireNonNull(FilenameUtils.getExtension(original)).toLowerCase();

            Map<?,?> params = ObjectUtils.asMap(
                    "resource_type", resourceType,
                    "public_id",    baseName,
                    "raw_convert",
                    ( "raw".equals(resourceType) && Arrays.asList(OFFICE_EXT).contains(ext) )
                            ? "aspose"
                            : null
            );

            Map<?,?> result = cloudinary.uploader()
                    .upload(file.getBytes(), params);

            return (String) result.get("secure_url");

        } catch (IOException e) {
            throw new FileStorageException("Failed to upload file to Cloudinary", e);
        }
    }

    public String generateRawUrl(String publicId, String format) {
        return cloudinary.url()
                .resourceType("raw")
                .format(format)
                .generate(publicId);
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
        } else if (contentType.startsWith("image/")) {
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
        Matcher m = PUBLIC_ID_PATTERN.matcher(url);
        return (m.find() ? m.group(1) : null);
    }
}
