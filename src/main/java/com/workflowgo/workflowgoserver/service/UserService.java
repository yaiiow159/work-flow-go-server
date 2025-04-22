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
import java.util.Set;
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
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

            writeCsvEntry(zip,
                    "user.csv",
                    new String[]{"ID", "Name", "Email"},
                    csv -> csv.printRecord(user.getId(), user.getName(), user.getEmail())
            );

            List<Interview> interviews = interviewRepository.findByUserId(
                    userId, Sort.by(Sort.Direction.DESC, "createdAt")
            );
            writeCsvEntry(zip,
                    "interviews.csv",
                    new String[]{"InterviewID", "Company", "Position", "Date", "Time", "Type", "Status", "Location", "Rating"},
                    csv -> {
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
                    }
            );

            Set<Document> docs = documentRepository.findByUserId(userId);
            writeCsvEntry(zip,
                    "documents.csv",
                    new String[]{"DocumentID", "Name", "Url", "UploadedAt"},
                    csv -> {
                        for (Document d : docs) {
                            csv.printRecord(
                                d.getId(),
                                d.getName(),
                                d.getUrl(),
                                d.getCreatedAt()
                            );
                        }
                    }
            );

            zip.finish();
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error exporting data to ZIP", ex);
        }
    }

    private void writeCsvEntry(ZipOutputStream zip,
                               String entryName,
                               String[] header,
                               CsvConsumer recordWriter) throws IOException {

        zip.putNextEntry(new ZipEntry(entryName));
        OutputStreamWriter osWriter = new OutputStreamWriter(
                new UncloseableOutputStream(zip), StandardCharsets.UTF_8);
        try (CSVPrinter csv = new CSVPrinter(osWriter, CSVFormat.DEFAULT.withHeader(header))) {
            recordWriter.accept(csv);
            csv.flush();
        }
        zip.closeEntry();
    }

    @FunctionalInterface
    private interface CsvConsumer {
        void accept(CSVPrinter csv) throws IOException;
    }

    private static class UncloseableOutputStream extends java.io.FilterOutputStream {
        UncloseableOutputStream(java.io.OutputStream out) {
            super(out);
        }
        @Override public void close() throws IOException { }
    }

    public User resetUserSettings(Long userId) {
        User user = getUserById(userId);
        user.setPreferences(new UserPreferences());
        return userRepository.save(user);
    }
}
