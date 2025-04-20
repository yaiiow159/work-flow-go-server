package com.workflowgo.workflowgoserver.service;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.workflowgo.workflowgoserver.config.AppProperties;
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
import java.util.Properties;

@Service
@Slf4j
public class EmailService {

    private final Gmail gmail;
    private final AppProperties appProperties;

    public EmailService(Gmail gmail, AppProperties appProperties) {
        this.gmail = gmail;
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
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        
        Message message = new Message();
        message.setRaw(encodedEmail);
        
        log.debug("Sending HTML email via Gmail API");
        try {
            message = gmail.users().messages().send("me", message).execute();
            log.info("HTML email sent successfully with message ID: {}", message.getId());
        } catch (IOException e) {
            log.error("Error sending HTML email via Gmail API", e);
            throw e;
        }
    }
}
