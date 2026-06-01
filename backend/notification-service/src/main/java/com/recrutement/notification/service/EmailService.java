package com.recrutement.notification.service;

import com.recrutement.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.mail.enabled:false}")
    private boolean mailEnabled;

    public void send(NotificationEvent event) {
        if ("SMS".equalsIgnoreCase(event.getType())) {
            log.info("[SMS simulé] → {} : {}", event.getToEmail(), event.getBody());
            return;
        }
        if (!mailEnabled) {
            log.info("[EMAIL simulé] → {} | {} | {}", event.getToEmail(), event.getSubject(), event.getBody());
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getToEmail());
            message.setSubject(event.getSubject());
            message.setText(event.getBody());
            mailSender.send(message);
            log.info("Email envoyé à {}", event.getToEmail());
        } catch (Exception e) {
            log.error("Échec envoi email: {}", e.getMessage());
        }
    }
}
