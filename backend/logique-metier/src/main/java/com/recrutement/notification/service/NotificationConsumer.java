package com.recrutement.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "notification.consumer.enabled", havingValue = "true")
public class NotificationConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "recruitment.queue")
    public void consumeNotificationEvent(Map<String, Object> payload) {
        log.info("Received notification event from RabbitMQ: {}", payload);

        String email = (String) payload.get("email");
        String recipientName = (String) payload.get("recipientName");
        String subject = (String) payload.get("subject");
        String message = (String) payload.get("message");
        String type = (String) payload.get("type"); // EMAIL, SMS, IN_APP

        if ("EMAIL".equalsIgnoreCase(type) && email != null) {
            String emailBody = String.format("Bonjour %s,\n\n%s\n\nCordialement,\nL'équipe de Recrutement", recipientName, message);
            emailService.sendSimpleEmail(email, subject, emailBody);
        } else if ("SMS".equalsIgnoreCase(type)) {
            log.info("[SMS simulé] À {} : {}", recipientName, message);
        } else {
            log.warn("Unsupported notification type or missing email: {}", type);
        }
    }
}
