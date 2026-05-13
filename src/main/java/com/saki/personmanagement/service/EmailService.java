package com.saki.personmanagement.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service for sending emails.
 * Service für den Email-Versand.
 *
 * @author saki
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from:noreply@person-management.com}")
    private String fromEmail;

    /**
     * Sends a simple plain text email.
     * Sendet eine einfache Plain-Text-Email.
     *
     * @param to recipient email address / Empfänger Email-Adresse
     * @param subject email subject / Email-Betreff
     * @param text email body / Email-Text
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        log.info("📧 Sending simple email to: {}", to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
        log.info("✅ Simple email sent to: {}", to);
    }

    /**
     * Sends an HTML email using Thymeleaf template - ASYNCHRONOUS!
     * Sendet eine HTML-Email mit Thymeleaf-Template - ASYNCHRON!
     *
     * @Async means this method runs in a separate thread.
     * @Async bedeutet diese Methode läuft in einem separaten Thread.
     *
     * @param to recipient email / Empfänger Email
     * @param firstName first name for template / Vorname für Template
     * @param lastName last name for template / Nachname für Template
     * @param email email for template / Email für Template
     */
    @Async
    public void sendPersonCreatedEmailAsync(
            String to,
            String firstName,
            String lastName,
            String email) {

        log.info("📧 [ASYNC] Sending person-created email to: {}", to);

        try {
            // Build Thymeleaf context / Thymeleaf-Kontext aufbauen
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("lastName", lastName);
            context.setVariable("email", email);
            context.setVariable("createdAt",
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

            // Process template / Template verarbeiten
            String htmlContent = templateEngine.process("email/welcome", context);

            // Build MIME message / MIME-Message aufbauen
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("New Person Created / Neue Person angelegt: "
                    + firstName + " " + lastName);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(mimeMessage);
            log.info("✅ [ASYNC] Person-created email sent to: {}", to);

        } catch (MessagingException e) {
            log.error("❌ [ASYNC] Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}