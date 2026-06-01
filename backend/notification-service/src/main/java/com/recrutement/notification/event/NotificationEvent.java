package com.recrutement.notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String toEmail;
    private String recipientName;
    private String subject;
    private String body;
    private String type; // EMAIL or SMS
}
