package com.workflowgo.workflowgoserver.service;

import com.workflowgo.workflowgoserver.config.AppProperties;
import com.workflowgo.workflowgoserver.model.Document;
import com.workflowgo.workflowgoserver.model.Interview;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
@Slf4j
public class EmailService {

    private final AppProperties appProperties;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public EmailService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Async("taskExecutor")
    public void sendVerificationEmail(String to, String verificationCode, int expirationMinutes) {
        try {
            log.info("Preparing to send verification email to: {}", to);
            String subject = appProperties.getEmail().getVerificationSubject();
            String template = appProperties.getEmail().getVerificationTemplate();
            String body = String.format(template, verificationCode, expirationMinutes);
            String htmlBody = createHtmlVerificationEmail(verificationCode, expirationMinutes);
            
            log.debug("Email subject: {}", subject);
            log.debug("Verification code: {}", verificationCode);
            log.debug("Expiration minutes: {}", expirationMinutes);
            
            sendEmail(to, subject, body, htmlBody);
            log.info("Verification email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
    
    @Async("taskExecutor")
    public void sendInterviewUpdateNotification(String to, Interview interview, String changeType) {
        try {
            log.info("Preparing to send interview update notification to: {}", to);
            String subject = "WorkflowGo - Interview Update: " + interview.getCompanyName();
            String textBody = createInterviewUpdateTextEmail(interview, changeType);
            String htmlBody = createInterviewUpdateHtmlEmail(interview, changeType);
            
            sendEmail(to, subject, textBody, htmlBody);
            log.info("Interview update notification sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send interview update notification to: {}", to, e);
        }
    }

    @Async("taskExecutor")
    public void sendDocumentNotification(String to, Document document, String actionType) {
        try {
            log.info("Preparing to send document notification to: {}", to);
            String subject = "WorkflowGo - Document " + capitalize(actionType);
            String textBody = createDocumentTextEmail(document, actionType);
            String htmlBody = createDocumentHtmlEmail(document, actionType);
            
            sendEmail(to, subject, textBody, htmlBody);
            log.info("Document notification sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send document notification to: {}", to, e);
        }
    }
    
    private String createInterviewUpdateTextEmail(Interview interview, String changeType) {
        StringBuilder sb = new StringBuilder();
        sb.append("WorkflowGo - Interview Update\n\n");
        sb.append("Hello,\n\n");
        
        switch (changeType) {
            case "created":
                sb.append("A new interview has been added to your WorkflowGo account.\n\n");
                break;
            case "updated":
                sb.append("An interview in your WorkflowGo account has been updated.\n\n");
                break;
            case "deleted":
                sb.append("An interview in your WorkflowGo account has been deleted.\n\n");
                break;
            case "status_changed":
                sb.append("The status of an interview in your WorkflowGo account has been changed to ")
                  .append(interview.getStatus()).append(".\n\n");
                break;
        }
        
        if (!changeType.equals("deleted")) {
            sb.append("Interview Details:\n");
            sb.append("Company: ").append(interview.getCompanyName()).append("\n");
            sb.append("Position: ").append(interview.getPosition()).append("\n");
            sb.append("Date: ").append(interview.getDate().format(DATE_FORMATTER)).append("\n");
            sb.append("Time: ").append(interview.getTime().format(TIME_FORMATTER)).append("\n");
            sb.append("Type: ").append(interview.getType()).append("\n");
            sb.append("Status: ").append(interview.getStatus()).append("\n");
            
            if (interview.getLocation() != null && !interview.getLocation().isEmpty()) {
                sb.append("Location: ").append(interview.getLocation()).append("\n");
            }
        }
        
        sb.append("\nYou can view more details by logging into your WorkflowGo account.\n\n");
        sb.append("This is an automated message, please do not reply to this email.\n");
        sb.append("© ").append(java.time.Year.now().getValue()).append(" WorkflowGo. All rights reserved.");
        
        return sb.toString();
    }
    
    private String createInterviewUpdateHtmlEmail(Interview interview, String changeType) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>")
          .append("<html>")
          .append("<head>")
          .append("<meta charset=\"UTF-8\">")
          .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
          .append("<title>Interview Update</title>")
          .append("<style>")
          .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }")
          .append(".container { border: 1px solid #ddd; border-radius: 5px; padding: 20px; }")
          .append(".header { text-align: center; padding-bottom: 10px; border-bottom: 1px solid #eee; margin-bottom: 20px; }")
          .append(".details { background-color: #f9f9f9; padding: 15px; border-radius: 4px; margin: 15px 0; }")
          .append(".status { display: inline-block; padding: 5px 10px; border-radius: 3px; font-weight: bold; color: white; background-color: ");

        if (interview.getStatus() != null) {
            switch (interview.getStatus()) {
                case SCHEDULED:
                    sb.append("#4f46e5");
                    break;
                case COMPLETED:
                    sb.append("#10b981");
                    break;
                case CANCELLED:
                    sb.append("#ef4444");
                    break;
                case CONFIRMED:
                    sb.append("#f59e0b");
                default:
                    sb.append("#f59e0b");
                    break;
            }
        } else {
            sb.append("#6b7280");
        }
        
        sb.append("; }")
          .append(".footer { font-size: 12px; color: #777; text-align: center; margin-top: 30px; padding-top: 10px; border-top: 1px solid #eee; }")
          .append("</style>")
          .append("</head>")
          .append("<body>")
          .append("<div class=\"container\">")
          .append("<div class=\"header\"><h2>WorkflowGo Interview Update</h2></div>")
          .append("<p>Hello,</p>");
        
        switch (changeType) {
            case "created":
                sb.append("<p>A new interview has been added to your WorkflowGo account.</p>");
                break;
            case "updated":
                sb.append("<p>An interview in your WorkflowGo account has been updated.</p>");
                break;
            case "deleted":
                sb.append("<p>An interview in your WorkflowGo account has been deleted.</p>");
                break;
            case "status_changed":
                sb.append("<p>The status of an interview in your WorkflowGo account has been changed to <strong>")
                  .append(interview.getStatus()).append("</strong>.</p>");
                break;
        }
        
        if (!changeType.equals("deleted")) {
            sb.append("<div class=\"details\">")
              .append("<h3>Interview Details</h3>")
              .append("<p><strong>Company:</strong> ").append(interview.getCompanyName()).append("</p>")
              .append("<p><strong>Position:</strong> ").append(interview.getPosition()).append("</p>")
              .append("<p><strong>Date:</strong> ").append(interview.getDate().format(DATE_FORMATTER)).append("</p>")
              .append("<p><strong>Time:</strong> ").append(interview.getTime().format(TIME_FORMATTER)).append("</p>")
              .append("<p><strong>Type:</strong> ").append(interview.getType()).append("</p>");
            
            if (interview.getStatus() != null) {
                sb.append("<p><strong>Status:</strong> <span class=\"status\">").append(interview.getStatus()).append("</span></p>");
            }
            
            if (interview.getLocation() != null && !interview.getLocation().isEmpty()) {
                sb.append("<p><strong>Location:</strong> ").append(interview.getLocation()).append("</p>");
            }
            
            sb.append("</div>");
        }
        
        sb.append("<p>You can view more details by logging into your WorkflowGo account.</p>")
          .append("<div class=\"footer\">")
          .append("<p>This is an automated message, please do not reply to this email.</p>")
          .append("<p>&copy; ").append(java.time.Year.now().getValue()).append(" WorkflowGo. All rights reserved.</p>")
          .append("</div>")
          .append("</div>")
          .append("</body>")
          .append("</html>");
        
        return sb.toString();
    }

    private String createDocumentTextEmail(Document document, String actionType) {
        StringBuilder sb = new StringBuilder();
        sb.append("WorkflowGo - Document Notification\n\n");
        sb.append("Hello,\n\n");
        
        switch (actionType) {
            case "uploaded":
                sb.append("A new document has been uploaded to your WorkflowGo account.\n\n");
                break;
            case "updated":
                sb.append("A document in your WorkflowGo account has been updated.\n\n");
                break;
            case "deleted":
                sb.append("A document in your WorkflowGo account has been deleted.\n\n");
                break;
        }
        
        if (!actionType.equals("deleted")) {
            sb.append("Document Details:\n");
            sb.append("Name: ").append(document.getName()).append("\n");
            if (document.getType() != null) {
                sb.append("Type: ").append(document.getType()).append("\n");
            }
            sb.append("Size: ").append(formatFileSize(document.getSize())).append("\n");
            sb.append("Uploaded: ").append(document.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        }
        
        sb.append("\nYou can view all your documents by logging into your WorkflowGo account.\n\n");
        sb.append("This is an automated message, please do not reply to this email.\n");
        sb.append("© ").append(java.time.Year.now().getValue()).append(" WorkflowGo. All rights reserved.");
        
        return sb.toString();
    }
    
    private String createDocumentHtmlEmail(Document document, String actionType) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>")
          .append("<html>")
          .append("<head>")
          .append("<meta charset=\"UTF-8\">")
          .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
          .append("<title>Document Notification</title>")
          .append("<style>")
          .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }")
          .append(".container { border: 1px solid #ddd; border-radius: 5px; padding: 20px; }")
          .append(".header { text-align: center; padding-bottom: 10px; border-bottom: 1px solid #eee; margin-bottom: 20px; }")
          .append(".details { background-color: #f9f9f9; padding: 15px; border-radius: 4px; margin: 15px 0; }")
          .append(".footer { font-size: 12px; color: #777; text-align: center; margin-top: 30px; padding-top: 10px; border-top: 1px solid #eee; }")
          .append("</style>")
          .append("</head>")
          .append("<body>")
          .append("<div class=\"container\">")
          .append("<div class=\"header\"><h2>WorkflowGo Document Notification</h2></div>")
          .append("<p>Hello,</p>");
        
        switch (actionType) {
            case "uploaded":
                sb.append("<p>A new document has been uploaded to your WorkflowGo account.</p>");
                break;
            case "updated":
                sb.append("<p>A document in your WorkflowGo account has been updated.</p>");
                break;
            case "deleted":
                sb.append("<p>A document in your WorkflowGo account has been deleted.</p>");
                break;
        }
        
        if (!actionType.equals("deleted")) {
            sb.append("<div class=\"details\">")
              .append("<h3>Document Details</h3>")
              .append("<p><strong>Name:</strong> ").append(document.getName()).append("</p>");
              
            if (document.getType() != null) {
                sb.append("<p><strong>Type:</strong> ").append(document.getType()).append("</p>");
            }
            
            sb.append("<p><strong>Size:</strong> ").append(formatFileSize(document.getSize())).append("</p>")
              .append("<p><strong>Uploaded:</strong> ").append(document.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("</p>")
              .append("</div>");
        }
        
        sb.append("<p>You can view all your documents by logging into your WorkflowGo account.</p>")
          .append("<div class=\"footer\">")
          .append("<p>This is an automated message, please do not reply to this email.</p>")
          .append("<p>&copy; ").append(java.time.Year.now().getValue()).append(" WorkflowGo. All rights reserved.</p>")
          .append("</div>")
          .append("</div>")
          .append("</body>")
          .append("</html>");
        
        return sb.toString();
    }
    
    private String formatFileSize(Long size) {
        if (size == null) return "Unknown";
        
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String createHtmlVerificationEmail(String code, int expirationMinutes) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>Email Verification</title>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }"
                + ".container { border: 1px solid #ddd; border-radius: 5px; padding: 20px; }"
                + ".header { text-align: center; padding-bottom: 10px; border-bottom: 1px solid #eee; margin-bottom: 20px; }"
                + ".code { font-size: 24px; font-weight: bold; text-align: center; letter-spacing: 5px; margin: 30px 0; padding: 10px; background-color: #f5f5f5; border-radius: 4px; }"
                + ".footer { font-size: 12px; color: #777; text-align: center; margin-top: 30px; padding-top: 10px; border-top: 1px solid #eee; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"container\">"
                + "<div class=\"header\"><h2>WorkflowGo Email Verification</h2></div>"
                + "<p>Hello,</p>"
                + "<p>Thank you for registering with WorkflowGo. Please use the verification code below to complete your registration:</p>"
                + "<div class=\"code\">" + code + "</div>"
                + "<p>This code will expire in <strong>" + expirationMinutes + " minutes</strong>.</p>"
                + "<p>If you did not request this code, please ignore this email.</p>"
                + "<div class=\"footer\">"
                + "<p>This is an automated message, please do not reply to this email.</p>"
                + "<p>&copy; " + java.time.Year.now().getValue() + " WorkflowGo. All rights reserved.</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
    
    private void sendEmail(String to, String subject, String textBody, String htmlBody) throws MessagingException, IOException {
        log.debug("Creating HTML email message for recipient: {}", to);
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        
        String fromEmail = appProperties.getEmail().getSenderEmail();
        log.debug("Using sender email: {}", fromEmail);
        
        email.setFrom(new InternetAddress(fromEmail));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        MimeMultipart multipart = new MimeMultipart("alternative");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(textBody, "utf-8");
        multipart.addBodyPart(textPart);
        
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlBody, "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);
        
        email.setContent(multipart);
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedMessage = java.util.Base64.getEncoder().encodeToString(rawMessageBytes);
        String encodedEmail = java.util.Base64.getEncoder().encodeToString(rawMessageBytes);

        log.debug("Sending email to: {}", to);
    }
}
