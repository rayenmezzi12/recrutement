package com.recrutement.inapp.service;

import com.recrutement.inapp.model.AppNotification;
import com.recrutement.inapp.repository.AppNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppNotificationService {

    private final AppNotificationRepository repository;

    public AppNotification create(String username, String title, String body, String type) {
        return repository.save(AppNotification.builder()
                .username(username)
                .title(title)
                .body(body)
                .type(type != null ? type : "INFO")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<AppNotification> getForUser(String username) {
        return repository.findByUsernameOrderByCreatedAtDesc(username);
    }

    public long countUnread(String username) {
        return repository.countByUsernameAndIsReadFalse(username);
    }

    public void markAllRead(String username) {
        repository.findByUsernameOrderByCreatedAtDesc(username).forEach(n -> {
            n.setIsRead(true);
            repository.save(n);
        });
    }
}
