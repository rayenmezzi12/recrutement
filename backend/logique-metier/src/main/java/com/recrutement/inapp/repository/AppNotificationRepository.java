package com.recrutement.inapp.repository;

import com.recrutement.inapp.model.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findByUsernameOrderByCreatedAtDesc(String username);
    long countByUsernameAndIsReadFalse(String username);
}
