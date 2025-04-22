package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.dto.UserInfoDTO;
import com.workflowgo.workflowgoserver.exception.ResourceNotFoundException;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.Interview;
import com.workflowgo.workflowgoserver.model.User;
import com.workflowgo.workflowgoserver.model.UserPreferences;
import com.workflowgo.workflowgoserver.repository.DocumentRepository;
import com.workflowgo.workflowgoserver.repository.InterviewRepository;
import com.workflowgo.workflowgoserver.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final DocumentRepository documentRepository;

    public UserService(UserRepository userRepository,
                       InterviewRepository interviewRepository,
                       DocumentRepository documentRepository
                      ) {
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
        this.documentRepository = documentRepository;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    public User updateUserSettings(Long userId, UserInfoDTO userInfoDTO) {
        User user = getUserById(userId);

        if (userInfoDTO.getName() != null && !userInfoDTO.getName().isEmpty()) {
            user.setName(userInfoDTO.getName());
        }

        if (userInfoDTO.getEmail() != null && !userInfoDTO.getEmail().isEmpty()) {
            user.setEmail(userInfoDTO.getEmail());
        }

        if (userInfoDTO.getBio() != null) {
            user.setBio(userInfoDTO.getBio());
        }

        if (userInfoDTO.getPhone() != null) {
            user.setPhone(userInfoDTO.getPhone());
        }

        if (userInfoDTO.getLocation() != null) {
            user.setLocation(userInfoDTO.getLocation());
        }

        if (userInfoDTO.getCompany() != null) {
            user.setCompany(userInfoDTO.getCompany());
        }

        if (userInfoDTO.getPosition() != null) {
            user.setPosition(userInfoDTO.getPosition());
        }

        if (userInfoDTO.getPhotoURL() != null) {
            user.setPhotoURL(userInfoDTO.getPhotoURL());
        }

        if (userInfoDTO.getPreferences() != null) {
            if (userInfoDTO.getPreferences().getTheme() != null) {
                user.getPreferences().setDarkMode(
                        userInfoDTO.getPreferences().getTheme().isDarkMode());

                if (userInfoDTO.getPreferences().getTheme().getPrimaryColor() != null) {
                    user.getPreferences().setPrimaryColor(
                            userInfoDTO.getPreferences().getTheme().getPrimaryColor());
                }
            }

            if (userInfoDTO.getPreferences().getNotifications() != null) {
                user.getPreferences().setEmailNotifications(
                        userInfoDTO.getPreferences().getNotifications().isEnabled());

                user.getPreferences().setEmailNotifications(
                        userInfoDTO.getPreferences().getNotifications().isEmailNotifications());

                if (userInfoDTO.getPreferences().getNotifications().getReminderTime() != null) {
                    user.getPreferences().setReminderTime(
                            userInfoDTO.getPreferences().getNotifications().getReminderTime());
                }
            }

            if (userInfoDTO.getPreferences().getDisplay() != null) {
                if (userInfoDTO.getPreferences().getDisplay().getDefaultView() != null) {
                    user.getPreferences().setDefaultView(
                            userInfoDTO.getPreferences().getDisplay().getDefaultView());
                }

                user.getPreferences().setCompactMode(
                        userInfoDTO.getPreferences().getDisplay().isCompactMode());
            }
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public byte[] exportUserData(Long userId) {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zip = new ZipOutputStream(baos, StandardCharsets.UTF_8)
        ) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            zip.putNextEntry(new ZipEntry("user.csv"));
            try (CSVPrinter csv = new CSVPrinter(
                    new OutputStreamWriter(zip, StandardCharsets.UTF_8),
                    CSVFormat.DEFAULT.withHeader("ID","Name","Email")
            )) {
                csv.printRecord(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                );
                csv.flush();
            }
            zip.closeEntry();

            List<Interview> interviews = interviewRepository.findByUserId(
                    userId, Sort.by(Sort.Direction.DESC, "createdAt")
            );
            zip.putNextEntry(new ZipEntry("interviews.csv"));
            try (CSVPrinter csv = new CSVPrinter(
                    new OutputStreamWriter(zip, StandardCharsets.UTF_8),
                    CSVFormat.DEFAULT.withHeader(
                            "InterviewID","Company","Position","Date","Time","Type","Status","Location","Rating"
                    )
            )) {
                for (Interview it : interviews) {
                    csv.printRecord(
                            it.getId(),
                            it.getCompanyName(),
                            it.getPosition(),
                            it.getDate(),
                            it.getTime(),
                            it.getType(),
                            it.getStatus(),
                            it.getLocation(),
                            it.getRating()
                    );
                }
                csv.flush();
            }
            zip.closeEntry();

            List<Document> docs = documentRepository.findByUserId(userId);
            zip.putNextEntry(new ZipEntry("documents.csv"));
            try (CSVPrinter csv = new CSVPrinter(
                    new OutputStreamWriter(zip, StandardCharsets.UTF_8),
                    CSVFormat.DEFAULT.withHeader("DocumentID","Name","Url","UploadedAt")
            )) {
                for (Document d : docs) {
                    csv.printRecord(
                            d.getId(),
                            d.getName(),
                            d.getUrl(),
                            d.getCreatedAt()
                    );
                }
                csv.flush();
            }
            zip.closeEntry();

            zip.finish();
            return baos.toByteArray();

        } catch (IOException ex) {
            throw new RuntimeException("Error exporting data to ZIP", ex);
        }
    }
    public User resetUserSettings(Long userId) {
        User user = getUserById(userId);
        user.setPreferences(new UserPreferences());
        return userRepository.save(user);
    }
}
