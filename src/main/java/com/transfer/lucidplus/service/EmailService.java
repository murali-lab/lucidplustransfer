package com.transfer.lucidplus.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.transfer.lucidplus.exception.EmailSendException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Service
public class EmailService {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${email.sender.address}")
    private String senderEmail;

    @Value("${email.support.address}")
    private String supportEmail;

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String type, String data, long expirationTime) {
        validateInputs(to, type, data, expirationTime);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, applicationName + " Security");
            helper.setTo(to);
            helper.setSubject(generateSubject(type));
            helper.setText(generateHtmlContent(type, data, expirationTime), true);

            mailSender.send(message);

            log.info("[EMAIL SENT] {} email sent successfully to: {}", type, maskEmail(to));

        } catch (MessagingException ex) {
            log.error("[EMAIL ERROR] Failed to send {} email to {}. Error: {}",
                    type, maskEmail(to), ex.getMessage());
            throw new EmailSendException("Failed to send security email", ex);
        } catch (Exception e) {
            log.error("[EMAIL ERROR] Unexpected error while sending {} email: {}", type, e.getMessage());
            throw new EmailSendException("Unexpected error while sending email", e);
        }
    }

    private void validateInputs(String to, String type, String data, long expirationTime) {
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient email cannot be empty");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Email type cannot be empty");
        }
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Email data cannot be empty");
        }
        if (expirationTime <= 0) {
            throw new IllegalArgumentException("Expiration time must be positive");
        }
    }

    private String generateSubject(String type) {
        return switch (type.toUpperCase(Locale.ROOT)) {
            case "OTP" -> applicationName + " - Your One Time Password (OTP)";
            default -> applicationName + " - Notification";
        };
    }

    private String generateHtmlContent(String type, String data, long expirationTime) {
        String formattedTime = ZonedDateTime.now()
                .plusMinutes(expirationTime)
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss z", Locale.ENGLISH));

        if ("OTP".equalsIgnoreCase(type)) {
            return """
                    <html>
                        <body style="font-family: Arial, sans-serif;">
                            <h2 style="color: #1a73e8;">%s Security Verification</h2>
                            <p>Hello,</p>
                            <p>Your OTP code is:</p>
                            <h3 style="color:#000;background:#f0f0f0;padding:10px;width:fit-content;border-radius:5px;">
                                %s
                            </h3>
                            <p>This OTP is valid until <b>%s</b>.</p>
                            <p>If you did not request this, please contact our support immediately at 
                               <a href="mailto:%s">%s</a>.</p>
                            <br/>
                            <p>Regards,<br/><b>%s Team</b></p>
                        </body>
                    </html>
                    """.formatted(applicationName, data, formattedTime, supportEmail, supportEmail, applicationName);
        }
        
        return """
                <p>Hello,</p>
                <p>%s</p>
                <br/>
                <p>Regards,<br/>%s Team</p>
                """.formatted(data, applicationName);
    }

    private String maskEmail(String email) {
        if (email == null || email.length() <= 6 || !email.contains("@")) {
            return "***";
        }
        int atIndex = email.indexOf('@');
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (local.length() <= 3) {
            return local.charAt(0) + "***" + domain;
        }
        return local.substring(0, 3) + "***" + domain;
    }
}

