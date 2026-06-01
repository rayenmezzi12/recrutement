package com.recrutement.notification.consumer;

import com.recrutement.notification.event.NotificationEvent;
import com.recrutement.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "notification.queue")
    public void handle(Object payload) {
        NotificationEvent event = mapPayload(payload);
        log.info("Notification reçue pour {}", event.getToEmail());
        emailService.send(event);
    }

    @SuppressWarnings("unchecked")
    private NotificationEvent mapPayload(Object payload) {
        if (payload instanceof NotificationEvent ne) {
            return ne;
        }
        if (payload instanceof Map<?, ?> map) {
            return NotificationEvent.builder()
                    .toEmail(stringVal(map.get("email"), map.get("toEmail")))
                    .recipientName(stringVal(map.get("recipientName")))
                    .subject(stringVal(map.get("subject")))
                    .body(stringVal(map.get("message"), map.get("body")))
                    .type(stringVal(map.get("type"), "EMAIL"))
                    .build();
        }
        return NotificationEvent.builder()
                .toEmail("unknown@local")
                .subject("Notification")
                .body(String.valueOf(payload))
                .type("EMAIL")
                .build();
    }

    private String stringVal(Object... values) {
        for (Object v : values) {
            if (v != null && !v.toString().isBlank()) {
                return v.toString();
            }
        }
        return "";
    }
}
